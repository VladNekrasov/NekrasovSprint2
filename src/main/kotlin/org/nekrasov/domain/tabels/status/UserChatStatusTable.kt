package org.nekrasov.domain.tabels.status

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column

object UserChatStatusTable : IntIdTable("user_chat_status") {
    val status: Column<String> = varchar("status", 30)
}