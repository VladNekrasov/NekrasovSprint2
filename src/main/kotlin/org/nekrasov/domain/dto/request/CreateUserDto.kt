package org.nekrasov.domain.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class CreateUserDto(
    val username: String,
    val firstName: String,
    val lastName: String,
    val password: String,
)
