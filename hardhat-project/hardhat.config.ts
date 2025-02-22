import { HardhatUserConfig } from "hardhat/config";
import "@nomicfoundation/hardhat-toolbox";
import dotenv from "dotenv"

dotenv.config()

const PRIVATE_KEY : string = process.env.PRIVATE_KEY as string
const API_KEY : string = process.env.API_KEY as string
const ETHERSCAN_API_KEY : string = process.env.ETHERSCAN_API_KEY as string

const config: HardhatUserConfig = {
  defaultNetwork: "sepolia",
  networks: {
    hardhat: {
    },
    sepolia: {
      url: `https://sepolia.infura.io/v3/${API_KEY}`,
      accounts: [PRIVATE_KEY]
    },
    holesky: {
      url: `https://holesky.infura.io/v3/${API_KEY}`,
      accounts: [PRIVATE_KEY]
    }
  },
  etherscan: {
    apiKey: `${ETHERSCAN_API_KEY}`
  },
  solidity: {
    version: "0.8.28",
    settings: {
      optimizer: {
        enabled: true,
        runs: 200
      }
    }
  }
};

export default config;