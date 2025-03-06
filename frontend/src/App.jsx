import { useState, useEffect, createContext } from 'react'
import { BrowserRouter as Router, Routes, Route } from "react-router-dom"
import { ethers } from "ethers"
import './App.scss'
import Header from './components/header/Header'
import { Home, FullProject, CreateTask } from "./pages"
import Preloader from "./components/preloader/Preloader"
import factoryContract from "./contracts/factory.json"

const _selectedAccount = localStorage.getItem("selectedAccount")
const _selectedRole = localStorage.getItem("selectedRole")

const initialState = {
  publicProvider: new ethers.JsonRpcProvider("https://ethereum-holesky-rpc.publicnode.com"),
  provider: null,
  factory: null,
  selectedAccount: _selectedAccount,
  selectedRole: _selectedRole,
  networkError: null,
  balance: BigInt(0)
};

export const Context = createContext()

function App() {
  const [isLoading, setIsLoading] = useState(true)
  const [globalState, setGlobalState] = useState(initialState)

  useEffect(() => {
    const startLoading = async () => {
      setTimeout(() => {
        setIsLoading(false)
      }, 1000);

      const provider = new ethers.BrowserProvider(window.ethereum)
      const projectsFactory = new ethers.Contract(
        factoryContract.address,
        factoryContract.abi,
        await provider.getSigner()
      )

      setGlobalState({
        ...globalState,
        provider: provider,
        factory: projectsFactory
      })
    }

    startLoading()
  }, [])

  function _resetState() {
    setGlobalState(initialState)
  }

  const isAuthorized = globalState.selectedAccount && globalState.selectedRole

  return (
    <Router>
      {isLoading ? <Preloader /> : 
        <Context.Provider value={[globalState, setGlobalState]}>
          {isAuthorized ? <Header disconnect={_resetState} /> : <></>}
          <Routes>
            <Route path="/" element={<Home />}/>
            <Route path="/projects/:projectAddress" element={<FullProject />}/>
            <Route path="/projects/:projectAddress/create-task" element={<CreateTask />}/>
          </Routes>
        </Context.Provider>
      }
    </Router>
  )
}

export default App