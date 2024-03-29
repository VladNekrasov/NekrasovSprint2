package org.nekrasov.domain.tabels

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant

object ChatTable : LongIdTable("chat") {
    val title: Column<String> = varchar("title", 100)
    val creatorId: Column<Long> = long("creator_id").references(UserTable.id, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE)
    val creationTime: Column<Instant> = timestamp("creation_time")
    val deleted: Column<Boolean> = bool("deleted")
}