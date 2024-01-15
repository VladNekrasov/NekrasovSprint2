package org.nekrasov.domain.models

import java.time.LocalDateTime

data class Chat(
    val id: Long,
    val title: String,
    val photo: String,
    val creatorId: Long,
    val creationTime: LocalDateTime,
    val deleted: Boolean
)
