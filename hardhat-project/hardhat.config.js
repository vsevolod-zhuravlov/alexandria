"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
require("@nomicfoundation/hardhat-toolbox");
const dotenv_1 = __importDefault(require("dotenv"));
dotenv_1.default.config();
const PRIVATE_KEY = process.env.PRIVATE_KEY;
const API_KEY = process.env.API_KEY;
const ETHERSCAN_API_KEY = process.env.ETHERSCAN_API_KEY;
const config = {
    defaultNetwork: "sepolia",
    networks: {
        hardhat: {},
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
exports.default = config;
