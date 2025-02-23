import styles from './NetworkErrorMessage.module.scss';

function NetworkErrorMessage({message, dismiss}) {
  return (
      <div className={styles["network-error"]}>
        <p className={styles["network-error__message"]}>{message}</p>
        <button className={styles["network-error__button"]} type="button" onClick={dismiss}>
            <span aria-hidden="true">Cancel</span>
        </button>
      </div>
  )
}

export default NetworkErrorMessage