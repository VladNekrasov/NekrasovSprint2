package org.nekrasov.domain.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class TokenDto (
    val token: String
)