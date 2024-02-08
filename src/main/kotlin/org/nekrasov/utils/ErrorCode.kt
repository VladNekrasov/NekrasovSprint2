package org.nekrasov.utils

import io.ktor.http.*

enum class ErrorCode(val message: String, val code: HttpStatusCode) {
    USER_NOT_AUTHORIZED("User not authorized", HttpStatusCode.Unauthorized),
    USER_NOT_FOUND("User not found", HttpStatusCode.NotFound),
    USER_DELETED("User deleted", HttpStatusCode.BadRequest),
    DUPLICATE_USERNAME("Duplicate username", HttpStatusCode.BadRequest),
    INCORRECT_PASSWORD("Incorrect password", HttpStatusCode.Conflict),
    X_AUTH_TOKEN_NOT_EXIST("X-Auth-Token not exist", HttpStatusCode.BadRequest),
    INCORRECT_PAGE_SIZE("Parameter size and page more than zero", HttpStatusCode.BadRequest),
    DUPLICATE_USER_IN_CHAT("Duplicate user in chat", HttpStatusCode.Conflict),
    CHAT_NOT_FOUND("Chat not found", HttpStatusCode.NotFound),
    CREATOR_LEAVE_CHAT("Creator can't leave chat", HttpStatusCode.Conflict),
    USER_NOT_CREATOR("User is not the creator", HttpStatusCode.Conflict),
    USER_NOT_PARTICIPANT("The current user is not a participant", HttpStatusCode.Forbidden),
    CHAT_DELETED("Chat deleted", HttpStatusCode.BadRequest)
}