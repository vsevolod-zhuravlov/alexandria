import styles from './Task.module.scss'
import { useState, useContext, useEffect } from 'react'
import { Context } from "../../../App"

function Task({ info }) {
    // const [globalState] = useContext(Context)
    // const provider = globalState.provider
    // const [allTasks, setAllTasks] = useState()

    // useEffect(() => {
    //     const fetchTasks = async () => {
    //         try {
    //             const fetchedTasks = []

    //             for(let i = 0; i < tasks.length; i++) {
    //                 const nextTask = project.taskInfo(tasks[i])
    //                 fetchedTasks.push(nextTask)
    //             }
    
    //             setAllTasks(fetchedTasks);
    //         } catch (error) {
    //             console.error("Error fetching tasks:", error)
    //         }
    //     };
    
    //     fetchTasks()
    // }, [])    

    return (
        <>Hello</>
    )
}

export default Task