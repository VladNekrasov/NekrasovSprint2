package org.nekrasov.data.repository

import org.jetbrains.exposed.sql.*
import org.nekrasov.data.DatabaseFactory
import org.nekrasov.domain.models.Chat
import org.nekrasov.domain.tabels.ChatTable

class ChatRepository {
    private fun resultRowToChat(row: ResultRow) = Chat(
        id = row[ChatTable.id].value,
        title = row[ChatTable.title],
        photo = row[ChatTable.photo],
        creatorId = row[ChatTable.creatorId],
        creationTime = row[ChatTable.creationTime],
        deleted = row[ChatTable.deleted]
    )

    suspend fun create(chat: Chat): Chat = DatabaseFactory.dbQuery {
        val id = ChatTable.insertAndGetId {
            it[title] = chat.title
            it[photo] = chat.photo
            it[creatorId] = chat.creatorId
            it[creationTime] = chat.creationTime
            it[deleted] = chat.deleted
        }
        chat.copy(id = id.value)
    }
}