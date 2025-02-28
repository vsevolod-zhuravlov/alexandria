import styles from './FullProject.module.scss'
import { useContext, useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { ethers } from "ethers"
import { Context } from "../../App"
import AllTasks from "../../components/tasks/allTasks/AllTasks"
import projectContract from "../../contracts/project.json"

export function FullProject() {
    const [globalState] = useContext(Context)
    const provider = globalState.provider

    const { projectAddress } = useParams()

    const [currentProject, setCurrentProject] = useState({})
    const [name, setName] = useState("")
    const [description, setDescription] = useState("")
    const [deadline, setDeadline] = useState(0)
    const [tasks, setTasks] = useState([])

    const isConnected = globalState.selectedAccount

    useEffect(() => {
        const loadFullProjectData = async () => {
            try {
                const publicProvider = globalState.publicProvider
                console.log(publicProvider)
                const signerOrProvider = isConnected ? await provider.getSigner() : publicProvider

                const fullProject = new ethers.Contract(
                    projectAddress,
                    projectContract.abi,
                    signerOrProvider
                )

                setCurrentProject(fullProject)
            
                const projectName = await fullProject.projectName()
                const projectDescription = await fullProject.description()
                const projectDeadline = await fullProject.DEADLINE()
                const projectTasks = await fullProject.getTasks()

                setName(projectName)
                setDescription(projectDescription)
                setDeadline(projectDeadline)
                setTasks(projectTasks)
            } catch (error) {
                console.error("Error loading full project data:", error)
            }
        }

        loadFullProjectData()
    }, [projectAddress, provider])

    const tasksExists = tasks.length > 0

    return (
        <>
            <div className={styles["full-project"]}>
                <div className={styles["full-project__container"]}>
                    <div className={styles["full-project__info"]}>
                        <div className={styles["full-project__name"]}>{name}</div>
                        <div className={styles["full-project__description"]}>{description}</div>
                        <div className={styles["full-project__deadline"]}>Deadline: {deadline.toString()}</div>
                    </div>
                    <div className={`${styles["full-project__tasks"]} ${styles["tasks"]}`}>
                        <div className={styles["tasks__header"]}>
                            <div className={styles["tasks__title"]}>Tasks:</div>
                            {isConnected ? <Link to={`/projects/${projectAddress}/create-task`} className={styles["tasks__create-button"]}>Create New</Link> : <></>}
                        </div>
                        <div className={styles["tasks__content"]}>
                            {
                                tasksExists ? 
                                <AllTasks tasks={tasks} project={currentProject} /> : 
                                <div className={styles["tasks__not-found"]}>There have been no tasks created for this project yet. Create your first one now!</div>
                            }
                        </div>
                    </div>
                </div>
            </div>
        </>
    )
}