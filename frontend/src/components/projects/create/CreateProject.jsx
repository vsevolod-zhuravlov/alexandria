import styles from './CreateProject.module.scss'
import { useState, useContext } from 'react'
import { Context } from "../../../App"
import { ethers } from "ethers"

function CreateProject() {
    const [globalState, setGlobalState] = useContext(Context)
    const [name, setName] = useState("");
    const [description, setDescription] = useState("")
    const [deadline, setDeadline] = useState("")
    const [domain, setDomain] = useState("")
    const [tokenName, setTokenName] = useState("")
    const [tokenSymbol, setTokenSymbol] = useState("")
    const [isPublic, setIsPublic] = useState("")
    const [confirmers, setConfirmers] = useState("")
    const [students, setStudents] = useState("")
    const [error, setError] = useState("")
    const [status, setStatus] = useState("start")
    const [newProjectAddress, setProjectAddress] = useState("")

    function handleFormValidation() {
        if (
            name === "" ||
            description === "" ||
            deadline === "" ||
            domain === "" ||
            tokenName === "" ||
            tokenSymbol === "" ||
            isPublic === "" ||
            confirmers === "" ||
            students === ""
        ) {
            setError("All fields are required")
            return false
        }        

        try {
            const _confirmers = JSON.parse(confirmers)
            if(!Array.isArray(_confirmers)) {
                setError("Confirmers should be array")
                return false
            }

            const _students = JSON.parse(students)
            if(!Array.isArray(_students)) {
                setError("Students should be array")
                return false
            }

            if(!(isPublic === "true" || isPublic === "false")) {
                setError("Is public should be boolean value")
                return false
            }
        } catch (err) {
            setError("Invalid input format!")
            return false
        }

        return true;
    }

    async function createProject(event) {
        event.preventDefault()

        if(handleFormValidation()) {
            const _confirmers = JSON.parse(confirmers)
            const _students = JSON.parse(students)
            const _deadline = BigInt(deadline)
            const _isPublic = isPublic === "true"

            const projectsFactory = globalState.factory
            
            const tx = await projectsFactory.create(
                _confirmers,
                _students,
                name,
                description,
                domain,
                _deadline,
                tokenName,
                tokenSymbol,
                _isPublic
            )

            setStatus("creating")

            const receipt = await tx.wait()
            const rawAddress = receipt.logs[8].topics[1]
            const projectAddress = ethers.getAddress("0x" + rawAddress.slice(-40))

            setProjectAddress(projectAddress)
            setStatus("created")
        } else {
            console.log(error)
        }
    }

    return (
        <>
            {status === "start" ? (
                <div className={styles["create-project"]}>
                    <form>
                        <div className={styles["create-project__group"]}>
                            <label className={styles["create-project__label"]}>Project Name</label>
                            <input onChange={(e) => setName(e.target.value)} type="text" name="projectName" placeholder="Enter name" required className={styles["create-project__input"]} />
                        </div>
                        <div className={styles["create-project__group"]}>
                            <label className={styles["create-project__label"]}>Description</label>
                            <input onChange={(e) => setDescription(e.target.value)} type="text" name="description" placeholder="Enter description" required className={styles["create-project__input"]} />
                        </div>
                        <div className={styles["create-project__group"]}>
                            <label className={styles["create-project__label"]}>Deadline</label>
                            <input onChange={(e) => setDeadline(e.target.value)} type="text" name="deadline" placeholder="Enter deadline" required className={styles["create-project__input"]} />
                        </div>
                        <div className={styles["create-project__group"]}>
                            <label className={styles["create-project__label"]}>Files Domain</label>
                            <input onChange={(e) => setDomain(e.target.value)} type="text" name="filesDomain" placeholder="Enter files domain" required className={styles["create-project__input"]} />
                        </div>
                        <div className={styles["create-project__group"]}>
                            <label className={styles["create-project__label"]}>Token Name</label>
                            <input onChange={(e) => setTokenName(e.target.value)} type="text" name="tokenName" placeholder="Enter token name" required className={styles["create-project__input"]} />
                        </div>
                        <div className={styles["create-project__group"]}>
                            <label className={styles["create-project__label"]}>Token Symbol</label>
                            <input onChange={(e) => setTokenSymbol(e.target.value)} type="text" name="tokenSymbol" placeholder="Enter token symbol" required className={styles["create-project__input"]} />
                        </div>
                        <div className={styles["create-project__group"]}>
                            <label className={styles["create-project__label"]}>Is Public</label>
                            <input onChange={(e) => setIsPublic(e.target.value)} type="text" name="isPublic" placeholder="Enter true or false" className={styles["create-project__input"]} />
                        </div>
                        <div className={styles["create-project__group"]}>
                            <label className={styles["create-project__label"]}>Confirmers</label>
                            <input onChange={(e) => setConfirmers(e.target.value)} type="text" name="confirmers" placeholder="Enter confirmers array" required className={styles["create-project__input"]} />
                        </div>
                        <div className={styles["create-project__group"]}>
                            <label className={styles["create-project__label"]}>Students</label>
                            <input onChange={(e) => setStudents(e.target.value)} type="text" name="students" placeholder="Enter students array" required className={styles["create-project__input"]} />
                        </div>
                        <button type="button" onClick={createProject} className={styles["create-project__button"]}>
                            Create Project
                        </button>
                    </form>
                </div>
            ) : status === "creating" ? (
                <div>Creating...</div>
            ) : (
                <div>{newProjectAddress}</div>
            )}
        </>
    );
    
}

export default CreateProject