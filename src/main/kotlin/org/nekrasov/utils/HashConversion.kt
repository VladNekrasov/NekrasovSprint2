package org.nekrasov.utils
import org.mindrot.jbcrypt.BCrypt

const val rounds: String = "12"
const val lengthInfoString: Int = 7

fun hashPassword(password: String): String {
    return BCrypt.hashpw(password, BCrypt.gensalt(rounds.toInt())).substring(lengthInfoString)
}
fun checkPassword(password: String, hashedPassword: String): Boolean {
    return BCrypt.checkpw(password, "$2a$$rounds$$hashedPassword")
}