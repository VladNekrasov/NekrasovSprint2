package org.nekrasov.domain.models

import kotlinx.serialization.Serializable
import kotlinx.datetime.*

@Serializable
data class Test(
    val id: Long = 0,
    val text: String?,
    val chat: Long,
    val fromId: Long,
    val createTime: Instant
)