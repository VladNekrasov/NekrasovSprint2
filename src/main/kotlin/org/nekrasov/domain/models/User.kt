package org.nekrasov.domain.models

import java.time.LocalDateTime

data class User(
    val id: Long,
    val username: String,
    val firstName: String,
    val lastName: String,
    val phone: String,
    val email: String,
    val photo: String,
    val bio: String?,
    val statusId: Int,
    val deleted: Boolean,
    val restricted: Boolean,
    val premium: Boolean,
    val registrationTime: LocalDateTime,
    val exitTime: LocalDateTime?
)
