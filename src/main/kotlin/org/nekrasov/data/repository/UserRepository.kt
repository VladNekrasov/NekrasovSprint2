package org.nekrasov.data.repository

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.nekrasov.data.DatabaseFactory.dbQuery
import org.nekrasov.domain.models.User
import org.nekrasov.domain.tabels.UserTable


class UserRepository {
    private fun resultRowToUser(row: ResultRow) = User(
        id = row[UserTable.id].value,
        username = row[UserTable.username],
        firstName = row[UserTable.firstName],
        lastName = row[UserTable.lastName],
        photo = row[UserTable.photo],
        bio = row[UserTable.bio],
        online = row[UserTable.online],
        deleted = row[UserTable.deleted],
        restricted = row[UserTable.restricted],
        premium = row[UserTable.premium],
        password = row[UserTable.password],
        registrationTime = row[UserTable.registrationTime],
        exitTime = row[UserTable.exitTime]
    )

    suspend fun create(user: User): User = dbQuery{
        val id = UserTable.insertAndGetId {
            it[username] = user.username
            it[firstName] = user.firstName
            it[lastName] = user.lastName
            it[photo] = user.photo
            it[bio] = user.bio
            it[online] = user.online
            it[deleted] = user.deleted
            it[restricted] = user.restricted
            it[premium] = user.premium
            it[password] = user.password
            it[registrationTime] = user.registrationTime
            it[exitTime] = user.exitTime
        }
        user.copy(id = id.value)
    }
    suspend fun readByUsername(username: String): User? = dbQuery {
        UserTable
            .select { UserTable.username eq username }
            .map(::resultRowToUser)
            .singleOrNull()
    }
}