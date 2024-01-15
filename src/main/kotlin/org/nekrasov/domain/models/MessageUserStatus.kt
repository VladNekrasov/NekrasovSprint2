package org.nekrasov.domain.models

data class MessageUserStatus (
    val messageId: Long,
    val userId: Long,
    val statusId: Int
)