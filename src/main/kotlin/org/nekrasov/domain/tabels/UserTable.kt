package org.nekrasov.domain.tabels

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant

object UserTable : LongIdTable("user") {
    val username: Column<String> = varchar("username", 50).uniqueIndex()
    val firstName: Column<String> = varchar("first_name",50)
    val lastName: Column<String> = varchar("last_name",50)
    val password: Column<String> = varchar("password", 100)
    val token: Column<String?> = varchar("token", 100).nullable()
    val registrationTime: Column<Instant> = timestamp("registration_time")
    val deleted: Column<Boolean> = bool("deleted")
}