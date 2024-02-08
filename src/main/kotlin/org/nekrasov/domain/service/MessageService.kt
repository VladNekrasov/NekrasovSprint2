package org.nekrasov.domain.service

import org.nekrasov.data.repository.MessageRepository
import org.nekrasov.domain.models.Message

class MessageService(private val messageRepository: MessageRepository) {
    suspend fun getAllMessages(page: Long, size: Int, chatId: Long): List<Message>? {
        return if (size > 0 && page > 0)
            messageRepository.allMessagesPaginated(page, size, chatId)
        else
            null
    }
}