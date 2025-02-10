// This setup uses Hardhat Ignition to manage smart contract deployments.
// Learn more about it at https://hardhat.org/ignition

import { buildModule } from "@nomicfoundation/hardhat-ignition/modules";

const ProjectsFactoryModule = buildModule("ProjectsFactoryModule", (m) => {
  const factory = m.contract("ProjectsFactory");

  return { factory };
});

export default ProjectsFactoryModule;