import { useState, useEffect, createContext } from 'react'
import { BrowserRouter as Router, Routes, Route } from "react-router-dom"
import './App.scss'
import Header from './components/header/Header'
import { Home, FullProject, CreateTask } from "./pages"
import Preloader from "./components/preloader/Preloader"

const initialState = {
  provider: null,
  factory: null,
  currentProject: null,
  selectedAccount: null,
  selectedRole: null,
  txBeingSent: null,
  networkError: null,
  transactionError: null,
  balance: BigInt(0),
};

export const Context = createContext()

function App() {
  const [isLoading, setIsLoading] = useState(true)
  const [globalState, setGlobalState] = useState(initialState)

  useEffect(() => {
    const startLoading = () => {
      setTimeout(() => {
        setIsLoading(false)
      }, 1000);
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