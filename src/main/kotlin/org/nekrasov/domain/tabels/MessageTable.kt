package org.nekrasov.domain.tabels

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object MessageTable : LongIdTable("message") {
    val chatId: Column<Long> = long("chat_id").references(ChatTable.id, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE)
    val text: Column<String?> = varchar("text", 400).nullable()
    val fromId: Column<Long> = long("from_id").references(UserTable.id,  onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE)
    val createTime: Column<LocalDateTime> = datetime("create_time")
}