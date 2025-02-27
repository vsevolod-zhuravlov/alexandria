import styles from './CreateTask.module.scss'
import { useState, useContext } from 'react'
import { Context } from "../../App"
import { ethers } from "ethers"
import { Link, useParams } from 'react-router-dom'
import projectContract from "../../contracts/project.json"

export function CreateTask() {
    const [globalState] = useContext(Context)
    const [name, setName] = useState("");
    const [uri, setUri] = useState("")
    const [description, setDescription] = useState("")
    const [deadline, setDeadline] = useState("")
    const [confirmDuration, setConfirmDuration] = useState("")
    const [minConfirmations, setMinConfirmations] = useState("")
    const [error, setError] = useState("")
    const [status, setStatus] = useState("start")
    const [newTaskId, setNewTaskId] = useState("")

    const { projectAddress } = useParams()

    function handleFormValidation() {
        if (
            name === "" ||
            uri === "" ||
            description === "" ||
            deadline === "" ||
            confirmDuration === "" ||
            minConfirmations === ""
        ) {
            setError("All fields are required")
            return false
        }        

        // try {
        //     const deadline = await project.DEADLINE()

        //     if() {
        //         setError("")
        //         return false
        //     }
        // } catch (err) {
        //     setError("Invalid input format!")
        //     return false
        // }

        return true
    }

    async function createTask(event) {
        event.preventDefault()

        if(handleFormValidation()) {
            const provider = globalState.provider
            const signer = await provider.getSigner();
            const project = new ethers.Contract(
                projectAddress,
                projectContract.abi,
                signer
            )

            const _deadline = BigInt(deadline)
            const _confirmDuration = BigInt(confirmDuration)
            const _minConfirmations = BigInt(minConfirmations)
            
            const tx = await project.createTask(
                name,
                description,
                uri,
                _deadline,
                _confirmDuration,
                _minConfirmations
            )

            setStatus("creating")

            const receipt = await tx.wait()
            const taskId = receipt.logs[0].topics[1]

            setNewTaskId(taskId)
            setStatus("created")
        } else {
            console.log(error)
        }
    }

    return (
        <>
            {status === "start" ? (
                <div className={styles["create-task"]}>
                    <div className={styles["create-task__container"]}>
                        <div className={styles["create-task__header"]}>
                            <Link to={`/projects/${projectAddress}`}>{"<="}</Link>
                            <div className={styles["create-task__title"]}>Create New Task</div>
                        </div>
                        <form>
                            <div className={styles["create-task__group"]}>
                                <label className={styles["create-task__label"]}>task Name</label>
                                <input onChange={(e) => setName(e.target.value)} type="text" name="taskName" placeholder="Enter name" required className={styles["create-task__input"]} />
                            </div>
                            <div className={styles["create-task__group"]}>
                                <label className={styles["create-task__label"]}>Description</label>
                                <input onChange={(e) => setDescription(e.target.value)} type="text" name="description" placeholder="Enter description" required className={styles["create-task__input"]} />
                            </div>
                            <div className={styles["create-task__group"]}>
                                <label className={styles["create-task__label"]}>URI</label>
                                <input onChange={(e) => setUri(e.target.value)} type="text" name="uri" placeholder="Enter URI for  task NFT certificate" required className={styles["create-task__input"]} />
                            </div>
                            <div className={styles["create-task__group"]}>
                                <label className={styles["create-task__label"]}>Deadline</label>
                                <input onChange={(e) => setDeadline(e.target.value)} type="text" name="deadline" placeholder="Enter deadline" required className={styles["create-task__input"]} />
                            </div>
                            <div className={styles["create-task__group"]}>
                                <label className={styles["create-task__label"]}>Task confirmation duration</label>
                                <input onChange={(e) => setConfirmDuration(e.target.value)} type="text" name="tokenName" placeholder="Enter confirmation duration" required className={styles["create-task__input"]} />
                            </div>
                            <div className={styles["create-task__group"]}>
                                <label className={styles["create-task__label"]}>Min confirmations required</label>
                                <input onChange={(e) => setMinConfirmations(e.target.value)} type="text" name="tokenSymbol" placeholder="Enter min confirmations" required className={styles["create-task__input"]} />
                            </div>
                            <button type="button" onClick={createTask} className={styles["create-task__button"]}>
                                Create
                            </button>
                        </form>
                    </div>
                </div>
            ) : status === "creating" ? (
                <div>Creating...</div>
            ) : (
                <div>{newTaskId}</div>
            )}
        </>
    )
}