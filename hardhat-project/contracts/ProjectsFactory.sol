// SPDX-License-Identifier: UNLICENSED

pragma solidity ^0.8.28;

import {IProjectsFactory} from "./interfaces/IProjectsFactory.sol";
import {Project} from "./Project.sol";

contract ProjectsFactory is IProjectsFactory {
    mapping(address => address[]) projectsOfOwner;
    mapping(address => address[]) projectsOfConfirmer;

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
    ) external returns(address) {
        address projectOwner = msg.sender;

        address newProjectAddress = address(new Project(
            projectOwner,
            _confirmers,
            _students,
            _projectName,
            _description,
            _domain,
            _deadline,
            _tokenName,
            _tokenSymbol,
            isPublic
        ));

        projectsOfOwner[projectOwner].push(newProjectAddress);

        for(uint i = 0; i < _confirmers.length; i++) {
            projectsOfConfirmer[_confirmers[i]].push(newProjectAddress);
        }
        
        return newProjectAddress;
    }

    function getProjectsOfOwner(address _owner) external view returns(address[] memory) {
        return projectsOfOwner[_owner];
    }

    function getProjectOfConfirmer(address _confirmer) external view returns(address[] memory) {
        return projectsOfConfirmer[_confirmer];
    }
}