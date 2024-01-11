package org.nekrasov.domain.tabels

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.nekrasov.tabels.status.MessageStatus
import org.nekrasov.tabels.status.PGEnum

object MessageUserStatusTable : Table("message_user_status") {
    val messageId: Column<Long> = long("message_id").references(MessageTable.id, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE)
    val userId: Column<Long> = long("user_id").references(UserTable.id, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE)
    val status = customEnumeration("status","MessageStatusEnum",{value -> MessageStatus.valueOf(value as String)}, { PGEnum("MessageStatusEnum", it) })
    override val primaryKey = PrimaryKey(messageId, userId)
}