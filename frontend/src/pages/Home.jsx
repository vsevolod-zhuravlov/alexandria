import { useContext } from "react"
import { ethers } from "ethers"
import Welcome from "../components/welcome/Welcome"
import SelectRole from "../components/selectRole/SelectRole"
import NetworkErrorMessage from "../components/networkError/NetworkErrorMessage"
import UserInterface from "../components/userInterface/UserInterface"
import { Context } from "../App"
import factoryContract from "../contracts/factory.json"

const HOLESKY_NETWORK_ID = "17000"
const HARDHAT_NETWORK_ID = "31337"
// const ERROR_CODE_TX_REJECTED_BY_USER = 4001

export function Home() {
    const [globalState, setGlobalState] = useContext(Context)
    
    function _checkNetwork() {
        console.log(window.ethereum.networkVersion)
        if (window.ethereum.networkVersion === HOLESKY_NETWORK_ID) { return true }

        console.error("Wrong network. Connect to Ethereum Holesky")

        setGlobalState({
            ...globalState, 
            networkError: "Wrong network. Connect to Ethereum Holesky"
        })

        return false
    }

    async function updateBalance(provider, account) {
        try {
            const newBalance = await provider.getBalance(account);
            setGlobalState({
                ...globalState, 
                balance: newBalance
            })
        } catch (error) {
            console.error("Error getting balance: ", error);
        }
    }

    async function _initialize(selectedAddress) {
        const provider = new ethers.BrowserProvider(window.ethereum)
        
        const projectsFactory = new ethers.Contract(
            factoryContract.address,
            factoryContract.abi,
            await provider.getSigner()
        );

        setGlobalState({
            ...globalState,
            provider: provider,
            factory: projectsFactory,
            selectedAccount: selectedAddress,
        }, await updateBalance(provider, selectedAddress))
    }

    function _resetState() {
        setGlobalState({
            publicProvider: null,
            provider: null,
            factory: null,
            currentProject: null,
            selectedAccount: null,
            selectedRole: null,
            txBeingSent: null,
            networkError: null,
            transactionError: null,
            balance: BigInt(0),
        })
    }

    const _connectWallet = async () => {
        if(window.ethereum === undefined) {
            setGlobalState({
                ...globalState, 
                networkError: "Please, install MetaMask!"
            })
            return
        }

        const [selectedAddress] = await window.ethereum.request({
            method: "eth_requestAccounts"
        })

        if(!_checkNetwork()) {
            console.error("Wrong network")
            return
        }

        _initialize(selectedAddress)

        window.ethereum.on("accountsChanged", ([newAddress]) => {
            if(newAddress === undefined) {
                return _resetState()
            }

            _initialize(newAddress)
        })

        window.ethereum.on("chainChanged", () => {
            _resetState()
        })
    }

    const _dismissNetworkError = () => {
        setGlobalState({
            ...globalState, 
            networkError: null
        })
    }

    return (
        <> 
            { 
                globalState.selectedAccount === null || globalState.selectedAccount === undefined ? (
                    <Welcome 
                        connectWallet={_connectWallet} 
                        networkError={globalState.networkError} 
                        dismiss={_dismissNetworkError} 
                    />
                ) : (
                    <>
                        {globalState.networkError ? 
                            <NetworkErrorMessage  
                                message={globalState.networkError} 
                                dismiss={_dismissNetworkError} 
                            /> : 
                            
                            <>
                                {globalState.selectedRole === null || globalState.selectedRole === undefined ? 
                                    <SelectRole 
                                        networkError={globalState.networkError}
                                        dismiss={_dismissNetworkError}
                                    /> :
                                    <UserInterface role={globalState.selectedRole}/>
                                }
                            </>
                        }
                    </>
                )
            }
        </>
    )
}