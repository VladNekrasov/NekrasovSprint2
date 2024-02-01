package org.nekrasov.domain.models

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Long = 0,
    val username: String,
    val firstName: String,
    val lastName: String,
    val password: String,
    val token: String?,
    val registrationTime: Instant,
    val deleted: Boolean
)
