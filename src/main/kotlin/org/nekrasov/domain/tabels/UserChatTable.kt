package org.nekrasov.domain.tabels

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import org.nekrasov.domain.tabels.status.UserChatStatusTable
import java.time.LocalDateTime

object UserChatTable : Table("user_chat") {
    val userId: Column<Long> = long("user_id").references(UserTable.id, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE)
    val chatId: Column<Long> = long("chat_id").references(ChatTable.id, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE)
    val statusId: Column<Int> = integer("status_id").references(UserChatStatusTable.id, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE)
    val entryTime: Column<LocalDateTime> = datetime("entry_time")
    override val primaryKey = PrimaryKey(userId, chatId)
}