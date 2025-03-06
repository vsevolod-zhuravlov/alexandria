# ALEXANDRIA: Decentralized Platform for Student Achievements Verification

ALEXANDRIA is a blockchain-based platform designed to verify and certify student achievements in a decentralized manner. It allows students to complete tasks, receive confirmations from verifiers, and earn NFT-based certificates upon successful validation.

## Project Contract Overview

The `Project` contract is the core of ALEXANDRIA, handling project creation, student participation, task submissions, and confirmations. It is built using Solidity and leverages OpenZeppelin libraries for security and functionality.

### Key Features:
- **ERC721-based NFT Certification**: Students receive NFT certificates upon successful task completion.
- **Role-Based Access Control**: Project owners, confirmers, and students have distinct permissions.
- **Task Management**: Tasks have deadlines, required confirmations, and associated metadata.
- **Decentralized Confirmation Process**: Tasks must be verified by multiple confirmers before being accepted.
- **Public and Private Projects**: Projects can be open to all students or limited to a predefined list.

### Contract Functionalities:
- **Project Creation**: Admins define the project, set deadlines, and manage participants.
- **Task Submission**: Students submit tasks, which are stored off-chain with a reference in the smart contract.
- **Task Confirmation**: Confirmers validate submissions; once a task reaches the required confirmations, the student is rewarded with an NFT.
- **Project Finalization**: A project can be marked as finished after the deadline.

## Projects Factory Contract Overview

The `ProjectsFactory` contract is responsible for creating and managing multiple projects on the ALEXANDRIA platform.

### Key Functionalities:
- **Project Deployment**: Allows users to deploy new `Project` contracts with predefined settings.
- **Role-Based Project Organization**: Tracks projects based on ownership, confirmation roles, and student participation.
- **Decentralized Student Enrollment**: Public projects allow students to join independently, while private projects require manual addition.
- **Efficient Lookup**: Users can query projects based on their role (owner, confirmer, or student).

### Factory Contract Methods:
- `create(...)` - Deploys a new `Project` contract with specified parameters and assigns roles.
- `addProjectToStudent(address _student)` - Registers a project for a student when they join.
- `getProjectsOfOwner(address _owner)` - Returns all projects created by a specific owner.
- `getProjectsOfConfirmer(address _confirmer)` - Returns all projects where the user acts as a confirmer.
- `getProjectsOfStudent(address _student)` - Returns all projects where the user participates as a student.

## Backend file server

On the backend, you can upload a file and link it to a specific project, task and student. After a task is uploaded to the platform, the project owner and verifiers realize that the file can be found by a special url