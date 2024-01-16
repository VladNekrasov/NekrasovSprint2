package org.nekrasov.data.repository

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insertAndGetId
import org.nekrasov.data.DatabaseFactory.dbQuery
import org.nekrasov.domain.models.User
import org.nekrasov.domain.tabels.UserTable


class UserRepository {
    private fun resultRowToUser(row: ResultRow) = User(
        id = row[UserTable.id].value,
        username = row[UserTable.username],
        firstName = row[UserTable.firstName],
        lastName = row[UserTable.lastName],
        phone = row[UserTable.phone],
        email = row[UserTable.email],
        photo = row[UserTable.photo],
        bio = row[UserTable.bio],
        statusId = row[UserTable.statusId],
        deleted = row[UserTable.deleted],
        restricted = row[UserTable.restricted],
        premium = row[UserTable.premium],
        registrationTime = row[UserTable.registrationTime],
        exitTime = row[UserTable.exitTime]
    )

    suspend fun create(user: User): User = dbQuery{
        val id = UserTable.insertAndGetId {
            it[username] = user.username
            it[firstName] = user.firstName
            it[lastName] = user.lastName
            it[phone] = user.phone
            it[email] = user.email
            it[photo] = user.photo
            it[bio] = user.bio
            it[statusId] = user.statusId
            it[deleted] = user.deleted
            it[restricted] = user.restricted
            it[premium] = user.premium
            it[registrationTime] = user.registrationTime
            it[exitTime] = user.exitTime
        }
        user.copy(id = id.value)
    }
}