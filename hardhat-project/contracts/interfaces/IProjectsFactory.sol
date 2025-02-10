// SPDX-License-Identifier: UNLICENSED

pragma solidity ^0.8.28;

interface IProjectsFactory {
    function create(
        address[] memory _confirmers, 
        string memory _projectName,
        string memory _description,
        string memory _domain,
        uint _deadline,
        string memory _tokenName,
        string memory _tokenSymbol,
        bool isPublic
    ) external returns(address);

    function getProjectsOfOwner(address _owner) external view returns(address[] memory);
}