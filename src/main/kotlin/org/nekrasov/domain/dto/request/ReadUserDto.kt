package org.nekrasov.domain.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class ReadUserDto(
    val id: Long,
    val username: String? = null,
    val phone: String?  = null,
    val email: String?  = null,
    val password: String,
)
