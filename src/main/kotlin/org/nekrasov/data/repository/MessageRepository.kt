package org.nekrasov.data.repository

import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toKotlinInstant
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
        createTime = row[MessageTable.createTime].toKotlinInstant(),
        deleted = row[MessageTable.deleted]
    )

    suspend fun create(message: Message): Message = dbQuery {
        val id = MessageTable.insertAndGetId {
            it[text] = message.text
            it[chatId] = message.chat
            it[fromId] = message.fromId
            it[createTime] = message.createTime.toJavaInstant()
            it[deleted] = message.deleted
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
            it[text] = message.text
            it[chatId] = message.chat
            it[fromId] = message.fromId
            it[createTime] = message.createTime.toJavaInstant()
            it[deleted] = message.deleted
        } > 0
    }

    suspend fun delete(id: Long): Boolean = dbQuery {
        MessageTable.deleteWhere { MessageTable.id eq id } > 0
        MessageTable.update({ MessageTable.id eq id }) {
            it[deleted] = true
        } > 0
    }

    suspend fun allMessagesPaginated(page: Long, size: Int, chatId: Long): List<Message> = dbQuery {
        val skip: Long = (page-1) * size
        MessageTable.select{MessageTable.chatId eq chatId}
            .orderBy(MessageTable.createTime, SortOrder.DESC)
            .limit(n = size, offset = skip)
            .map(::resultRowToMessage)
    }
}
