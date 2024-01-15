package org.nekrasov.domain.models

data class ForwardMessage(
    val mainId: Long,
    val forwardId: Long,
    val answer: Boolean
)
