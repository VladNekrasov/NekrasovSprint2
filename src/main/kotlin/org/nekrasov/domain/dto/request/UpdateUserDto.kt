package org.nekrasov.domain.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserDto (
    val id: Long,
    val username: String,
    val firstName: String,
    val lastName: String
)