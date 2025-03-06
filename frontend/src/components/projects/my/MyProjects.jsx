import styles from './MyProjects.module.scss'
import { useState, useContext, useEffect } from 'react'
import { Context } from "../../../App"
import Project from '../project/Project'

function MyProjects() {
    const [globalState, setGlobalState] = useContext(Context)
    const [projects, setProjects] = useState([])
    const projectsFactory = globalState.factory
    const selectedAccount = globalState.selectedAccount
    const role = globalState.selectedRole

    useEffect(() => {
        const fetchProjects = async () => {
            try {
                let fetchedProjects = []
    
                switch (role) {
                    case "Project Owner":
                        fetchedProjects = await projectsFactory.getProjectsOfOwner(selectedAccount)
                        break
                    case "Confirmer":
                        fetchedProjects = await projectsFactory.getProjectsOfConfirmer(selectedAccount)
                        break
                    case "Student":
                        fetchedProjects = await projectsFactory.getProjectsOfStudent(selectedAccount)
                        break
                    default:
                        console.error(`Role ${role} not recognized`)
                        return
                }
    
                setProjects(fetchedProjects);
            } catch (error) {
                console.error("Error fetching projects:", error)
            }
        };
    
        if (role && selectedAccount) {
            fetchProjects()
        }
    }, [role, selectedAccount])    

    return (
        <>{projects.map((address, index) => <Project key={index} projectAddress={address} />)}</>
    )
}

export default MyProjects