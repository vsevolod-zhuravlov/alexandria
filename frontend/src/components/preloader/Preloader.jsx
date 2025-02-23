import styles from './Preloader.module.scss';
import logo from "../../assets/logo.svg"

function Preloader() {
    return (
      <>
          <div className={styles["loader"]}>
              <div className={styles["image-wrapper"]}>
                  <img className={styles["image"]} src={logo} alt="Preloader" />
              </div>
          </div>
      </>
    );
}  

export default Preloader