import styles from './Header.module.scss';
import logo from "../../assets/logo-black.svg"
import profileIcon from "../../assets/profile.svg"
import { useEffect, useContext } from 'react'
import { Link } from "react-router-dom"
import { Context } from "../../App"

function Header({ disconnect }) {
    const [globalState] = useContext(Context)

    return (
        <div className={styles["header"]}>
            <div className={styles["header__container"]}>
                <Link to="/" className={styles["header__logo"]}>
                    <img src={logo} alt="Logo" />
                </Link>
                <div className={styles["header-profile"]}>
                    <div className={styles["header-profile__icon"]}>
                        <img src={profileIcon} alt="Profile Icon" />
                    </div>
                    <div className={styles["header-profile__container"]}>
                        <div>
                            <div className={styles["header-profile__address"]}>{globalState.selectedAccount}</div>
                            <div className={styles["header-profile__role"]}>Role: {globalState.selectedRole}</div>
                        </div>
                        <button className={styles["header-profile__disconnect-button"]} type="button" onClick={disconnect}>
                            Disconnect
                        </button>
                    </div>
                </div>
            </div>
        </div>
    )
}

export default Header