// SPDX-License-Identifier: UNLICENSED

pragma solidity ^0.8.28;

interface IProject  {
    // Events

    event TaskCreated(bytes32 indexed taskId, string indexed name);

    event TaskSubmited(bytes32 indexed taskId, address indexed submiter);

    event TaskConfirmed(bytes32 indexed taskId, address indexed submiter);

    event DomainChanged(string indexed oldDomain, string indexed newDomain);

    // Errors

    error NotPublicProject();

    error InPublicStudentsJoinOnTheirOwn();

    error NotListAccessProject();

    error Locked();

    error ProjectFinished(uint deadline);

    error InvalidDeadline(uint deadline);

    error AlreadyHaveRole(address account, bytes32 role);

    error ZeroAddressCannotHaveRole(bytes32 role);

    error MinTwoConfirmersAllowed(uint count);

    error InvalidConfirmDuration(uint duration);

    error InvalidConfirmationsCount(uint confirmations);

    error GoesBeyoundDeadline(uint timeValue);

    error TaskAlreadyExists(bytes32 taskId);

    error TaskNotFound(bytes32 taskId);

    error TaskDeadlinePassed(bytes32 taskId, uint deadline);
    
    error TaskConfirmDurationPassed(bytes32 taskId, uint deadline);

    error TaskAlreadySubmited(bytes32 taskId);

    error TaskNotSubmited(bytes32 taskId);

    error TaskAlreadyConfirmed(bytes32 taskId);

    error TaskAlreadyConfirmedByYou(bytes32 taskId);

    function getTasks() external view returns(bytes32[] memory);

    function createTask(
        string calldata _name, 
        string calldata _description, 
        string calldata _uri, 
        uint _deadline, 
        uint _confirmDuration, 
        uint _minConfirmations
    ) external returns(bytes32);

    function confirmTask(bytes32 _taskId, address _student) external;

    function submitTask(bytes32 _taskId) external;
}