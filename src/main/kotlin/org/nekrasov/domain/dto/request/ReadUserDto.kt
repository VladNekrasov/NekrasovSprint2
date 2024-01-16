package org.nekrasov.domain.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class ReadUserDto(
    val username: String,
    val password: String
)
