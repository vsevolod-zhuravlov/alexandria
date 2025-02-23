import { useState, useEffect, createContext } from 'react'
import './App.scss'
import Home from "./pages/Home"
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

  return (
    <>
      {isLoading ? <Preloader /> : 

        <Context.Provider value={[globalState, setGlobalState]}>
          <Home />
        </Context.Provider>
      }
    </>
  )
}

export default App