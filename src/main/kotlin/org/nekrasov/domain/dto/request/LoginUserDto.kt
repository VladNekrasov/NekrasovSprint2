package org.nekrasov.domain.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class LoginUserDto(
    val username: String,
    val password: String
)
