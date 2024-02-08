package org.nekrasov.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.nekrasov.domain.dto.request.CreateUserDto
import org.nekrasov.domain.dto.request.LoginUserDto
import org.nekrasov.domain.dto.request.TokenDto
import org.nekrasov.domain.service.AuthService
import org.nekrasov.exceptions.MissingHeaderException
import org.nekrasov.utils.ErrorCode
import org.nekrasov.utils.ServiceResult
import org.nekrasov.utils.respondErrorCode

fun Route.authRoutes(authService: AuthService){
    route("/api/v1/auth") {

        post("/reg"){
            val createUserDto = call.receive<CreateUserDto>()

            when(val result = authService.createUser(createUserDto)){
                is ServiceResult.Success -> {
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Ok"))
                }
                is ServiceResult.Error -> {
                    call.respondErrorCode(result.error)
                }
            }
        }

        post("/login"){
            val loginUserDto = call.receive<LoginUserDto>()

            when(val result = authService.loginUser(loginUserDto)){
                is ServiceResult.Success -> {
                    call.respond(HttpStatusCode.OK, result.data)
                }
                is ServiceResult.Error -> {
                    call.respondErrorCode(result.error)
                }
            }
        }

        post("/logout"){
            val token: String = call.request.headers["X-Auth-Token"] ?: throw MissingHeaderException("Missing  X-Auth-Token header")

            if (authService.logoutUser(token))
                call.respond(HttpStatusCode.Found, mapOf("message" to "Ok"))
            else
                call.respondErrorCode(ErrorCode.USER_NOT_AUTHORIZED)
        }

        post("/check-token"){
            val tokenDto = call.receive<TokenDto>()

            if (authService.checkToken(tokenDto.token))
                call.respond(HttpStatusCode.Found, mapOf("message" to "Ok"))
            else {
                call.respondErrorCode(ErrorCode.USER_NOT_AUTHORIZED)
            }
        }
    }
}