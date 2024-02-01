package org.nekrasov.data.repository

import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toKotlinInstant
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.nekrasov.data.DatabaseFactory.dbQuery
import org.nekrasov.domain.models.UserChat
import org.nekrasov.domain.tabels.UserChatTable

class UserChatRepository {
    private fun resultRowToUserChat(row: ResultRow) = UserChat(
        userId = row[UserChatTable.userId],
        chatId = row[UserChatTable.chatId],
        entryTime = row[UserChatTable.entryTime].toKotlinInstant()
    )

    suspend fun create(userChat: UserChat): Boolean = dbQuery {
        transaction {
            if ( UserChatTable.select{ (UserChatTable.userId eq userChat.userId) and (UserChatTable.chatId eq userChat.chatId) }.count() <= 0) {
                UserChatTable.insert {
                    it[userId] = userChat.userId
                    it[chatId] = userChat.chatId
                    it[entryTime] = userChat.entryTime.toJavaInstant()
                }
                return@transaction true
            } else {
                return@transaction false
            }
        }
    }

    suspend fun deleteUser(id: Long): Boolean = dbQuery {
        UserChatTable.deleteWhere { userId eq id } > 0
    }

    suspend fun deleteChat(id: Long): Boolean = dbQuery {
        UserChatTable.deleteWhere { chatId eq id } > 0
    }

    suspend fun delete(userId: Long, chatId: Long): Boolean = dbQuery {
        UserChatTable.deleteWhere { (UserChatTable.userId eq userId) and (UserChatTable.chatId eq chatId) } > 0
    }

    suspend fun getUsersId(chatId: Long): List<Long> = dbQuery {
        UserChatTable.select{UserChatTable.chatId eq chatId}
            .map {it[UserChatTable.userId]}
    }

    suspend fun getChatsId(userId: Long): List<Long> = dbQuery {
        UserChatTable.select{UserChatTable.userId eq userId}
            .map {it[UserChatTable.chatId]}
    }
}