package org.nekrasov.domain.models

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class UserChat(
    val userId: Long,
    val chatId: Long,
    val entryTime: Instant
)