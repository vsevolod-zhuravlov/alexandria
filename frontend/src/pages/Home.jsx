import { ethers } from "ethers"

import Header from "../components/header/Header"
import Welcome from "../components/welcome/Welcome"
import SelectRole from "../components/selectRole/SelectRole"
import NetworkErrorMessage from "../components/networkError/NetworkErrorMessage"
import factoryContract from "../contracts/factory.json"
import UserInterface from "../components/userInterface/UserInterface"
// import projectContract from "../contracts/project.json"

import { useContext } from "react"
import { Context } from "../App"

const HOLESKY_NETWORK_ID = "17000"
// const ERROR_CODE_TX_REJECTED_BY_USER = 4001

export default function Home() {
    const [globalState, setGlobalState] = useContext(Context)
    
    function _checkNetwork() {
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

        // try {
        //     const projects = await projectsFactory.getProjectsOfOwner(selectedAddress)
        //     console.log(projects[0])

        //     const project = new ethers.Contract(
        //         "0xBeB30ef04228DfC1fFDBa6c57c5fB4aD701b26e7",
        //         projectContract.abi,
        //         await provider.getSigner()
        //     )

        //     const taskId = "0x2882d409365a50c684fae709d3db405f6fd025cd4d815228377ccc14efd8b57c"
        //     const isConfirmed = await project.tasksConfirmers(taskId, selectedAddress, selectedAddress)

        //     console.log(isConfirmed)
        // } catch (err) {
        //     console.log(err)
        // }

        setGlobalState({
            ...globalState, 
            selectedAccount: selectedAddress,
            provider: provider,
            factory: projectsFactory
        }, await updateBalance(provider, selectedAddress))
    }

    function _resetState() {
        setGlobalState({
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

        console.log(globalState)
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

                                    <>
                                        <Header disconnect={_resetState} />
                                        <UserInterface role={globalState.selectedRole}/>
                                    </>
                                }
                            </>
                        }
                    </>
                )
            }
        </>
    )
}