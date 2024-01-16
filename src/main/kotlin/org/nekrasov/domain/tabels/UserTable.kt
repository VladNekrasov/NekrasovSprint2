package org.nekrasov.domain.tabels

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.datetime
import org.nekrasov.domain.tabels.status.UserStatusTable
import java.time.LocalDateTime

object UserTable : LongIdTable("user") {
    val username: Column<String> = varchar("username", 50).uniqueIndex()
    val firstName: Column<String> = varchar("first_name",50)
    val lastName: Column<String> = varchar("last_name",50)
    val phone: Column<String> = varchar("phone", 20)
    val email: Column<String> = varchar("email", 255)
    val photo: Column<String> = varchar("photo", 100)
    val bio: Column<String?> = varchar("bio", 200).nullable()
    val statusId: Column<Int> = integer("status_id")
        .references(UserStatusTable.id, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE)
    val deleted: Column<Boolean> = bool("deleted")
    val restricted: Column<Boolean> = bool("restricted")
    val premium: Column<Boolean> = bool("premium")
    val password: Column<String> = varchar("password", 50)
    val registrationTime: Column<LocalDateTime> = datetime("registration_time")
    val exitTime: Column<LocalDateTime?> = datetime("exit_time").nullable()
}