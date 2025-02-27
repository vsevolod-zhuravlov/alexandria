import styles from './AllTasks.module.scss'
import { useState, useContext, useEffect } from 'react'
import Task from "../task/Task"

function AllTasks({ tasks, project }) {
    const [allTasks, setAllTasks] = useState([])

    useEffect(() => {
        const fetchTasks = async () => {
            try {
                const fetchedTasks = []

                for(let i = 0; i < tasks.length; i++) {
                    const nextTask = await project.tasksInfo(tasks[i])
                    fetchedTasks.push(nextTask)
                }

                setAllTasks(fetchedTasks);
            } catch (error) {
                console.error("Error fetching tasks:", error)
            }
        };
    
        fetchTasks()
    }, [tasks, project])    

    return (
        <>{allTasks.map((task, index) => <div key={index}>{task[0]}</div>)}</>
    )
}

export default AllTasks