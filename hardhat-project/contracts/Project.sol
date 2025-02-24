// SPDX-License-Identifier: UNLICENSED

pragma solidity ^0.8.28;

import {ERC721} from "@openzeppelin/contracts/token/ERC721/ERC721.sol";
import {ERC721URIStorage} from "@openzeppelin/contracts/token/ERC721/extensions/ERC721URIStorage.sol";
import {AccessControl} from "@openzeppelin/contracts/access/AccessControl.sol";
import {IProject} from "./interfaces/IProject.sol";

contract Project is ERC721, ERC721URIStorage, AccessControl, IProject {
    string public projectName;
    string public description;
    address public owner;
    string public domain;
    bool public isPublic;
    bool public finished;

    address factory;

    uint public immutable DEADLINE;
    uint public constant MIN_DURATION = 1 days;
    uint public constant MIN_TASK_DURATION = 30 minutes;
    uint public constant MIN_CONFIRM_DURATION = 30 minutes;
    uint public constant MIN_CONFIRMATION_REQUIRED = 1;

    string baseURI_;
    uint _nextTokenId;

    bytes32 constant public CONFIRMER_ROLE = 0x2882d409365a50c684fae709d3db405f6fd025cd4d815228377ccc14efd8b57c; // keccak256("CONFIRMER_ROLE")
    bytes32 constant public STUDENT_ROLE = 0x36a5c4aaacb6b388bbd448bf11096b7dafc5652bcc9046084fd0e95b1fb0b2cc; // keccak256("STUDENT_ROLE")

    address[] confirmers;
    address[] students;

    struct SubmitInfo {
        bool isSubmited;
        bool isConfirmed;
        uint confirmations;
    }

    struct TaskInfo {
        string name;
        string description;
        string uri;
        uint deadline;
        uint confirmDuration;
        uint minConfirmations;
    }

    bytes32[] tasks;
    mapping(bytes32 => bool) public taskExists;
    mapping(bytes32 => TaskInfo) public tasksInfo;
    mapping(bytes32 => mapping(address => SubmitInfo)) public tasksSubmits;
    mapping(bytes32 => mapping(address => mapping(address => bool))) public tasksConfirmers;

    constructor(
        address projectOwner, 
        address[] memory _confirmers, 
        address[] memory _students,
        string memory _projectName, 
        string memory _description,
        string memory _domain,
        uint _deadline,
        string memory _tokenName,
        string memory _tokenSymbol,
        bool isPublic_
    ) ERC721(_tokenName, _tokenSymbol) {
        require(_deadline >= block.timestamp + MIN_DURATION);
        require(_confirmers.length >= 2, MinTwoConfirmersAllowed(_confirmers.length));

        if(isPublic_) {
            require(_students.length == 0, InPublicStudentsJoinOnTheirOwn());
        }

        projectName = _projectName;
        description = _description;
        owner = projectOwner;
        domain = _domain;
        DEADLINE = _deadline;
        isPublic = isPublic_;
        factory = _msgSender();

        _grantRole(DEFAULT_ADMIN_ROLE, projectOwner);
        _grantRole(CONFIRMER_ROLE, projectOwner);
        confirmers.push(projectOwner);

        for(uint i = 0; i < _confirmers.length; i++) {
            address nextConfirmer = _confirmers[i];
            require(nextConfirmer != address(0), ZeroAddressCannotHaveRole(CONFIRMER_ROLE));
            bool success = _grantRole(CONFIRMER_ROLE, nextConfirmer);
            require(success, AlreadyHaveRole(nextConfirmer, CONFIRMER_ROLE));
            confirmers.push(nextConfirmer);
        }

        if(!isPublic_) {
            for(uint i = 0; i < _students.length; i++) {
                address nextStudent = _students[i];
                require(nextStudent != address(0), ZeroAddressCannotHaveRole(STUDENT_ROLE));
                bool success = _grantRole(STUDENT_ROLE, nextStudent);
                require(success, AlreadyHaveRole(nextStudent, STUDENT_ROLE));
                students.push(nextStudent);
            }
        }
    }

    bool unlocked = true;
    modifier lock() {
        require(unlocked, Locked());
        unlocked = false;
        _;
        unlocked = true;
    }

    modifier isNotEnded() {
        require(block.timestamp < DEADLINE, ProjectFinished(DEADLINE));
        _;
    }

    modifier _isPublic() {
        require(isPublic, NotPublicProject());
        _;
    }

    modifier _isListAccess() {
        require(!isPublic, NotListAccessProject());
        _;
    }

    function safeMint(address to, string memory uri) internal {
        uint256 tokenId = _nextTokenId++;
        _safeMint(to, tokenId);
        _setTokenURI(tokenId, uri);
    }

    function addProjectToStudent(address _student) internal {
        (bool success, ) = factory.call{value: 0}(
            abi.encodeWithSignature("addProjectToStudent(address _student)", _student)
        );

        require(success, ErrorTryingAddProjectToStudent());
    }

    function changeBaseURI(string calldata newBaseURI) external onlyRole(DEFAULT_ADMIN_ROLE) {
        baseURI_ = newBaseURI;
    }

    // The following functions are overrides required by Solidity.

    function _baseURI() internal view override returns(string memory) {
        return baseURI_;
    }

    function tokenURI(uint256 tokenId)
        public
        view
        override(ERC721, ERC721URIStorage)
        returns (string memory)
    {
        return super.tokenURI(tokenId);
    }

    function supportsInterface(bytes4 interfaceId)
        public
        view
        override(ERC721, ERC721URIStorage, AccessControl)
        returns (bool)
    {
        return super.supportsInterface(interfaceId);
    }

    function changeDomain(string calldata newDomain) external isNotEnded onlyRole(DEFAULT_ADMIN_ROLE) {
        string memory oldDomain = domain;
        domain = newDomain;

        emit DomainChanged(oldDomain, newDomain);
    }

    // Public Access:

    function join() external _isPublic isNotEnded {
        address student = _msgSender();
        bool success = _grantRole(STUDENT_ROLE, student);
        require(success, AlreadyHaveRole(student, STUDENT_ROLE));
        students.push(student);
        addProjectToStudent(student);
    }

    // List Access:

    function addStudent(address _student) external _isListAccess isNotEnded onlyRole(DEFAULT_ADMIN_ROLE) {
        bool success = _grantRole(STUDENT_ROLE, _student);
        require(success, AlreadyHaveRole(_student, STUDENT_ROLE));
        students.push(_student);
        addProjectToStudent(_student);
    }

    // Tasks

    function getTasks() external view returns(bytes32[] memory) {
        return tasks;
    }

    function createTask(
        string calldata _name, 
        string calldata _description, 
        string calldata _uri,
        uint _deadline,
        uint _confirmDuration,
        uint _minConfirmations
    ) external isNotEnded onlyRole(DEFAULT_ADMIN_ROLE) returns(bytes32) {
        require(_deadline >= block.timestamp + MIN_TASK_DURATION, InvalidDeadline(_deadline)); // min task duration is 30 minutes
        require(_confirmDuration >= MIN_CONFIRM_DURATION, InvalidConfirmDuration(_confirmDuration)); // min allowed is 30 minutes
        require(_deadline + _confirmDuration <= DEADLINE, GoesBeyoundDeadline(_deadline + _confirmDuration)); // higher than project deadline
        require(_minConfirmations >= MIN_CONFIRMATION_REQUIRED, InvalidConfirmationsCount(_minConfirmations)); // minimum 1 confirmations should be set 
        
        bytes32 taskId = keccak256(abi.encode(
            _name,
            _description,
            _deadline,
            _confirmDuration,
            _minConfirmations
        ));

        require(!taskExists[taskId], TaskAlreadyExists(taskId));
        
        TaskInfo memory _taskInfo = TaskInfo({
            name: _name,
            description: _description,
            uri: _uri,
            deadline: _deadline,
            confirmDuration: _confirmDuration,
            minConfirmations: _minConfirmations
        });

        taskExists[taskId] = true;
        tasksInfo[taskId] = _taskInfo;
        tasks.push(taskId);

        emit TaskCreated(taskId, _name);
        return taskId;
    }

    // when student submits task - this means that project owner can find file at URL: "{domain}/projects/:projectAddress/tasks/:taskId/:studentAddress"

    function submitTask(bytes32 _taskId) external isNotEnded onlyRole(STUDENT_ROLE) {
        require(taskExists[_taskId], TaskNotFound(_taskId));

        TaskInfo memory currentTask = tasksInfo[_taskId];
        require(
            block.timestamp < currentTask.deadline, 
            TaskDeadlinePassed(_taskId, currentTask.deadline)
        );

        address currentStudent = _msgSender();

        SubmitInfo memory taskSubmitInfo = tasksSubmits[_taskId][currentStudent];
        require(!taskSubmitInfo.isSubmited, TaskAlreadySubmited(_taskId));

        taskSubmitInfo.isSubmited = true;
        tasksSubmits[_taskId][currentStudent] = taskSubmitInfo;

        emit TaskSubmited(_taskId, currentStudent);
    }

    function confirmTask(bytes32 _taskId, address _student) external isNotEnded lock onlyRole(CONFIRMER_ROLE) {
        require(taskExists[_taskId], TaskNotFound(_taskId));
        require(!tasksConfirmers[_taskId][_student][_msgSender()], TaskAlreadyConfirmedByYou(_taskId));

        TaskInfo memory currentTask = tasksInfo[_taskId];
        uint confirmDeadline = currentTask.deadline + currentTask.confirmDuration;
        require(
            block.timestamp < confirmDeadline, 
            TaskConfirmDurationPassed(_taskId, confirmDeadline)
        );

        SubmitInfo memory taskSubmitInfo = tasksSubmits[_taskId][_student];

        bool taskSubmited = taskSubmitInfo.isSubmited;
        require(taskSubmited, TaskNotSubmited(_taskId));
        bool taskConfirmed = taskSubmitInfo.isConfirmed;
        require(!taskConfirmed, TaskAlreadyConfirmed(_taskId));

        taskSubmitInfo.confirmations++;
        tasksConfirmers[_taskId][_student][_msgSender()] = true;

        emit TaskConfirmedByConfirmer(_taskId, _student, _msgSender());

        uint confirmationsRequired = currentTask.minConfirmations;

        if(taskSubmitInfo.confirmations == confirmationsRequired) {
            taskSubmitInfo.isConfirmed = true;
            tasksSubmits[_taskId][_student] = taskSubmitInfo;
            safeMint(_student, currentTask.uri); // student receives nft token when submition fully confirmed
            emit TaskConfirmed(_taskId, _student);
        } else {
            tasksSubmits[_taskId][_student] = taskSubmitInfo;
        }
    }

    function finishProject() external onlyRole(DEFAULT_ADMIN_ROLE) {
        require(!finished, ProjectFinished(DEADLINE));
        require(block.timestamp > DEADLINE, ProjectNotFinished(DEADLINE));

        finished = true;
    }

    function getStudents() external view returns(address[] memory) {
        return students;
    }

    function getConfirmers() external view returns(address[] memory) {
        return confirmers;
    }
}