// SPDX-License-Identifier: UNLICENSED

pragma solidity ^0.8.28;

interface IProjectsFactory {

    event ProjectCreated(address indexed project, address indexed creator);

    error CallerIsNotProject(address caller);

    function create(
        address[] memory _confirmers, 
        address[] memory _students,
        string memory _projectName,
        string memory _description,
        string memory _domain,
        uint _deadline,
        string memory _tokenName,
        string memory _tokenSymbol,
        bool isPublic
    ) external returns(address);

    function addProjectToStudent(address _student) external;

    function getProjectsOfOwner(address _owner) external view returns(address[] memory);

    function getProjectsOfConfirmer(address _confirmer) external view returns(address[] memory);

    function getProjectsOfStudent(address _student) external view returns(address[] memory);
}