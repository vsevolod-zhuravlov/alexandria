import Student from "./interfaces/student/Student"
import Owner from "./interfaces/owner/Owner"
import Confirmer from "./interfaces/confirmer/Confirmer"

function UserInterface({ role }) {
    if (role == "Project Owner") {
        return (
            <Owner/>
        )
    } else if (role == "Confirmer") {
        return (
            <Confirmer/>
        )
    } else if (role == "Student") {
        return (
            <Student/>
        )
    } else {
        return (
            <h1>Undefined Role</h1>
        )
    }
}

export default UserInterface