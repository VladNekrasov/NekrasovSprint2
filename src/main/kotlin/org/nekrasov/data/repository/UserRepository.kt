package org.nekrasov.data.repository

import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toKotlinInstant
import org.jetbrains.exposed.sql.*
import org.nekrasov.data.DatabaseFactory.dbQuery
import org.nekrasov.domain.models.User
import org.nekrasov.domain.tabels.UserTable


class UserRepository {
    private fun resultRowToUser(row: ResultRow) = User(
        id = row[UserTable.id].value,
        username = row[UserTable.username],
        firstName = row[UserTable.firstName],
        lastName = row[UserTable.lastName],
        password = row[UserTable.password],
        token = row[UserTable.token],
        registrationTime = row[UserTable.registrationTime].toKotlinInstant(),
        deleted = row[UserTable.deleted]
    )

    suspend fun create(user: User): User = dbQuery{
        val id = UserTable.insertAndGetId {
            it[username] = user.username
            it[firstName] = user.firstName
            it[lastName] = user.lastName
            it[password] = user.password
            it[token] = user.token
            it[registrationTime] = user.registrationTime.toJavaInstant()
            it[deleted] = user.deleted
        }
        user.copy(id = id.value)
    }

    suspend fun read(id: Long): User? = dbQuery {
        UserTable
            .select { UserTable.id eq id }
            .map(::resultRowToUser)
            .singleOrNull()
    }

    suspend fun readByUsername(username: String): User? = dbQuery {
        UserTable
            .select { UserTable.username eq username }
            .map(::resultRowToUser)
            .singleOrNull()
    }

    suspend fun readByToken(token: String): User? = dbQuery {
        UserTable
            .select { UserTable.token eq token }
            .map(::resultRowToUser)
            .singleOrNull()
    }

    suspend fun update(user: User): Boolean = dbQuery {
        UserTable.update({ UserTable.id eq user.id }) {
            it[username] = user.username
            it[firstName] = user.firstName
            it[lastName] = user.lastName
            it[password] = user.password
            it[token] = user.token
            it[registrationTime] = user.registrationTime.toJavaInstant()
            it[deleted] = user.deleted
        } > 0
    }

    suspend fun updateToken(id: Long, token: String): Boolean = dbQuery{
        UserTable.update({UserTable.id eq id}){
            it[UserTable.token] = token
        } > 0
    }

    suspend fun delete(id: Long): Boolean = dbQuery {
        UserTable.update({UserTable.id eq id}){
            it[deleted] = true
            it[token] = null
        } > 0
    }

    suspend fun deleteToken(token: String): Boolean = dbQuery{
        UserTable.update({UserTable.token eq token}){
            it[UserTable.token] = null
        } > 0
    }

    suspend fun allUsersPaginated(page: Long, size: Int): List<User> = dbQuery {
        val skip: Long = (page-1) * size
        UserTable.selectAll()
            .limit(n = size, offset = skip)
            .map(::resultRowToUser)
    }
}