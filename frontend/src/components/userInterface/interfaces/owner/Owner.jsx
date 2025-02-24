import styles from './Owner.module.scss';
import { useState } from 'react';
import CreateProject from '../../../projects/create/CreateProject'

function Owner() {
    const [mode, setMode] = useState("projects");

    function selectProjects() {
        setMode("projects");
    }

    function selectCreation() {
        setMode("create");
    }

    return (
        <div className={styles["dashboard"]}>
            <div className={styles["dashboard__container"]}>
                <div className={styles["dashboard__tabs"]}>
                    <button 
                        className={`${styles["select-projects-button"]} ${mode === "projects" ? styles["active"] : ""}`}
                        type="button" 
                        onClick={selectProjects}
                    >
                        My Projects
                    </button>
                    <button 
                        className={`${styles["select-create-button"]} ${mode === "create" ? styles["active"] : ""}`}
                        type="button" 
                        onClick={selectCreation}
                    >
                        Create
                    </button>
                </div>
                <div className={styles["dashboard__content"]}>
                    {mode === "projects" ? <div>Projects Content</div> : <CreateProject />}
                </div>
            </div>
        </div>
    );
}

export default Owner;