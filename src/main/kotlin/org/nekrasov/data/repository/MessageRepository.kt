package org.nekrasov.data.repository

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.nekrasov.data.DatabaseFactory.dbQuery
import org.nekrasov.domain.models.Message
import org.nekrasov.domain.tabels.MessageTable

class MessageRepository{
    private fun resultRowToMessage(row: ResultRow) = Message(
        id = row[MessageTable.id].value,
        text = row[MessageTable.text],
        chat = row[MessageTable.chatId],
        fromId = row[MessageTable.fromId],
        createTime = row[MessageTable.createTime]
    )

    suspend fun create(message: Message): Message = dbQuery {
        val id = MessageTable.insertAndGetId {
            it[MessageTable.text] = message.text
            it[MessageTable.chatId] = message.chat
            it[MessageTable.fromId] = message.fromId
            it[MessageTable.createTime] = message.createTime
        }
        message.copy(id = id.value)
    }

    suspend fun read(id: Long): Message? = dbQuery {
        MessageTable
            .select { MessageTable.id eq id }
            .map(::resultRowToMessage)
            .singleOrNull()
    }

    suspend fun update(message: Message): Boolean = dbQuery {
        MessageTable.update({ MessageTable.id eq message.id }) {
            it[MessageTable.text] = message.text
            it[MessageTable.chatId] = message.chat
            it[MessageTable.fromId] = message.fromId
            it[MessageTable.createTime] = message.createTime
        } > 0
    }

    suspend fun delete(id: Long): Boolean = dbQuery {
        MessageTable.deleteWhere { MessageTable.id eq id } > 0
    }

    suspend fun allMessages(): List<Message> = dbQuery {
        MessageTable.selectAll().map(::resultRowToMessage)
    }
}
