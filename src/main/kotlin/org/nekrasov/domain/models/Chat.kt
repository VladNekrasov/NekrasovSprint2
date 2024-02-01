package org.nekrasov.domain.models

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Chat(
    val id: Long = 0,
    val title: String,
    val creatorId: Long,
    val creationTime: Instant,
    val deleted: Boolean
)