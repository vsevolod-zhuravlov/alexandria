import styles from './SelectRole.module.scss'
import { useState, useContext } from "react"
import NetworkErrorMessage from '../networkError/NetworkErrorMessage'
import { Context } from "../../App"

function SelectRole({networkError, dismiss}) {
    const [globalState, setGlobalState] = useContext(Context)
    const [role, setRole] = useState(null)

    function setStudentRole() {
        setRole("Student")
    }

    function setConfirmerRole() {
        setRole("Confirmer")
    }

    function setOwnerRole() {
        setRole("Project Owner")
    }

    function confirmSelection() {
        setGlobalState({
            ...globalState,
            selectedRole: role
        })

        localStorage.setItem("selectedRole", role)
    }

    return (
        <div className={styles["select-role__container"]}>
            <div>
                {networkError && (
                    <NetworkErrorMessage
                        message={networkError}
                        dismiss={dismiss}
                    />
                )}
            </div>
            <div className={styles["select-role__wrapper"]}>
                <div className={styles["select-role__content"]}>
                    <h1 className={styles["select-role__title"]}>
                        Select your role<br></br>
                        in this session
                    </h1>
                    <div className={styles["select-role__list"]}>
                        <button className={styles["select-role__select-button"]} type="button" onClick={setOwnerRole}>Owner</button>
                        <button className={styles["select-role__select-button"]} type="button" onClick={setStudentRole}>Student</button>
                        <button className={styles["select-role__select-button"]} type="button" onClick={setConfirmerRole}>Confirmer</button>
                    </div>
                    <button className={styles["select-role__confirm-button"]} type='button' onClick={confirmSelection}>
                        Confrim Selection
                    </button>
                </div>
            </div>
        </div>
    )
}

export default SelectRole