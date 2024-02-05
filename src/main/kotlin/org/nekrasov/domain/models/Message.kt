package org.nekrasov.domain.models

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val id: Long = 0,
    val text: String,
    val chat: Long,
    val fromId: Long,
    val createTime: Instant,
    val deleted: Boolean
)
