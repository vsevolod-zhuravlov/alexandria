import styles from './Project.module.scss'
import { useContext, useEffect, useState } from 'react'
import { Link } from "react-router-dom"
import { ethers } from "ethers"
import { Context } from "../../../App"
import projectContract from "../../../contracts/project.json"

function Project({ projectAddress }) {
    const [globalState] = useContext(Context)
    const provider = globalState.provider

    const [name, setName] = useState("")
    const [description, setDescription] = useState("")
    const [deadline, setDeadline] = useState(0)

    useEffect(() => {
        const loadProjectData = async () => {
            try {
                const signer = await provider.getSigner()
                const project = new ethers.Contract(
                    projectAddress,
                    projectContract.abi,
                    signer
                )
            
                const projectName = await project.projectName()
                const projectDescription = await project.description()
                const projectDeadline = await project.DEADLINE()

                setName(projectName)
                setDescription(projectDescription)
                setDeadline(projectDeadline)
            } catch (error) {
                console.error("Error loading project data:", error)
            }
        }

        loadProjectData()
    }, [projectAddress, provider])

    return (
        <Link to={`/projects/${projectAddress}`} className={styles["project"]}>
            <div className={styles["project__name"]}>{name}</div>
            <div className={styles["project__description"]}>{description}</div>
            <div className={styles["project__deadline"]}>Deadline: {deadline.toString()}</div>
        </Link>
    )
}

export default Project