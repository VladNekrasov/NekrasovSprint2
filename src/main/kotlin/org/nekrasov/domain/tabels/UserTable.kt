package org.nekrasov.domain.tabels

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.javatime.datetime
import org.nekrasov.tabels.status.PGEnum
import org.nekrasov.tabels.status.UserStatus
import java.time.LocalDateTime

object UserTable : LongIdTable("user") {
    val username: Column<String> = varchar("username", 50).uniqueIndex()
    val firstName: Column<String> = varchar("first_name",50)
    val lastName: Column<String> = varchar("last_name",50)
    val phone: Column<String> = varchar("phone", 20)
    val email: Column<String> = varchar("email", 255)
    val photo: Column<String> = varchar("photo", 100)
    val bio: Column<String?> = varchar("bio", 200).nullable()
    val status = customEnumeration("status","UserStatusEnum",{value -> UserStatus.valueOf(value as String)}, { PGEnum("UserStatusEnum", it) })
    val deleted: Column<Boolean> = bool("deleted")
    val restricted: Column<Boolean> = bool("restricted")
    val premium: Column<Boolean> = bool("premium")
    val registrationTime: Column<LocalDateTime> = datetime("registration_time")
    val exitTime: Column<LocalDateTime> = datetime("exit_time")
}