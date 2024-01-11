package org.nekrasov.domain.tabels

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object ChatTable : LongIdTable("chat") {
    val title: Column<String> = varchar("title", 100)
    val photo: Column<String> = varchar("photo", 100)
    val creatorId: Column<Long> = long("creator_id").references(UserTable.id)
    val creationTime: Column<LocalDateTime> = datetime("creation_time")
    val deleted: Column<Boolean> = bool("deleted")
}