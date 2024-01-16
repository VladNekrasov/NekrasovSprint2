package org.nekrasov.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Response(
    val status: String
)