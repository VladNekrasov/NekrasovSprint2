package org.nekrasov.domain.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class ResponseLoginUserDto(
    val token: String,
    val id: Long
)
