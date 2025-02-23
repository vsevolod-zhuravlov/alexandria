import NetworkErrorMessage from "../networkError/NetworkErrorMessage"
import styles from './Welcome.module.scss';
import heroDecor from "../../assets/decor.svg"

function Welcome({connectWallet, networkError, dismiss}) {
  return (
    <div className={styles["welcome__container"]}>
        <div>
            {networkError && (
                <NetworkErrorMessage
                    message={networkError}
                    dismiss={dismiss}
                />
            )}
        </div>
        <div className={styles["welcome__wrapper"]}>
            <div className={styles["welcome__content"]}>
                <h1 className={styles["welcome__title"]}>
                    Welcome to <br></br>
                    <span className={styles["welcome__title--big"]}>ALEXANDRIA</span>
                </h1>
                <p className={styles["welcome__text"]}>Please, connect your wallet in Ethereum Holesky</p>
                <button className={styles["welcome__connect-button"]} type='button' onClick={connectWallet}>
                    Connect Wallet
                </button>
            </div>
            <div className={styles["welcome__image"]}>
                <img src={heroDecor} alt="Image" />
            </div>
        </div>
    </div>
  )
}

export default Welcome