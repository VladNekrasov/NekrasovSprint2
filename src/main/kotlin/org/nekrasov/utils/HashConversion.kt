package org.nekrasov.utils
import org.mindrot.jbcrypt.BCrypt


fun hashPassword(password: String): String {
    return BCrypt.hashpw(password, BCrypt.gensalt(12)).substring(7)
}
fun checkPassword(password: String, hashedPassword: String): Boolean {
    return BCrypt.checkpw(password, "$2a$12$$hashedPassword")
}