package org.nekrasov.domain.tabels

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant

object MessageTable : LongIdTable("message") {
    val chatId: Column<Long> = long("chat_id").references(ChatTable.id, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE)
    val text: Column<String> = varchar("text", 400)
    val fromId: Column<Long> = long("from_id").references(UserTable.id,  onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE)
    val createTime: Column<Instant> = timestamp("create_time")
    val deleted: Column<Boolean> = bool("deleted")
}