package org.nekrasov.data.repository

import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toKotlinInstant
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.nekrasov.data.DatabaseFactory.dbQuery
import org.nekrasov.domain.models.Chat
import org.nekrasov.domain.tabels.ChatTable
import org.nekrasov.domain.tabels.UserChatTable

class ChatRepository {
    private fun resultRowToChat(row: ResultRow) = Chat(
        id = row[ChatTable.id].value,
        title = row[ChatTable.title],
        creatorId = row[ChatTable.creatorId],
        creationTime = row[ChatTable.creationTime].toKotlinInstant(),
        deleted = row[ChatTable.deleted]
    )

    suspend fun create(chat: Chat): Chat = dbQuery {
        val id = ChatTable.insertAndGetId {
            it[title] = chat.title
            it[creatorId] = chat.creatorId
            it[creationTime] = chat.creationTime.toJavaInstant()
            it[deleted] = chat.deleted
        }
        chat.copy(id = id.value)
    }

    suspend fun read(id: Long): Chat? = dbQuery {
        ChatTable
            .select { ChatTable.id eq id }
            .map(::resultRowToChat)
            .singleOrNull()
    }

    suspend fun update(chat: Chat): Boolean = dbQuery {
        ChatTable.update({ ChatTable.id eq chat.id }) {
            it[title] = chat.title
            it[creatorId] = chat.creatorId
            it[creationTime] = chat.creationTime.toJavaInstant()
            it[deleted] = chat.deleted
        } > 0
    }

    suspend fun delete(id: Long): Boolean = dbQuery {
        transaction{
            UserChatTable.deleteWhere { chatId eq id }
            ChatTable.update({ ChatTable.id eq id }) {
                it[deleted] = true
            } > 0
        }
    }

    suspend fun allChats(page: Long, size: Int): List<Chat> = dbQuery {
        val skip: Long = (page-1) * size
        ChatTable.selectAll()
            .limit(n = size, offset = skip)
            .map(::resultRowToChat)
    }
}