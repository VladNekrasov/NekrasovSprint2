package org.nekrasov.domain.service

import org.nekrasov.data.repository.MessageRepository
import org.nekrasov.domain.dto.response.ResponseMessageDto
import org.nekrasov.domain.dto.response.messageToResponseMessageDto
import org.nekrasov.domain.models.Message

class MessageService(private val messageRepository: MessageRepository) {
    suspend fun getAllMessages(page: Long, size: Int, chatId: Long): List<ResponseMessageDto> {
        return messageRepository.allMessagesPaginated(page, size, chatId).map(::messageToResponseMessageDto)
    }
}