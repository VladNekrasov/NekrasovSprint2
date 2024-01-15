package org.nekrasov.domain.models

import java.time.LocalDateTime

data class UserChat(
    val userId: Long,
    val chatId: Long,
    val status: String,
    val entryTime: LocalDateTime
)
