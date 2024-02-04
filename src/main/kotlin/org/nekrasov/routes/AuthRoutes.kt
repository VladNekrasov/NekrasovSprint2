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
import org.nekrasov.exceptions.UnauthorizedException

fun Route.authRoutes(authService: AuthService){
    route("/api/v1/auth") {

        post("/reg"){
            val createUserDto = call.receive<CreateUserDto>()
            if (authService.createUser(createUserDto))
                call.respond(HttpStatusCode.Created, mapOf("status" to "Ok"))
            else
                call.respond(HttpStatusCode.Conflict, mapOf("status" to "Duplicate username"))
        }

        post("/login"){
            val loginUserDto = call.receive<LoginUserDto>()
            authService.loginUser(loginUserDto)?.let{
                call.respond(HttpStatusCode.Found, it)
            } ?: call.respond(HttpStatusCode.NotFound, mapOf("status" to "User not found"))
        }

        post("/logout"){
            val token: String = call.request.headers["X-Auth-Token"] ?: throw MissingHeaderException("Missing  X-Auth-Token header")
            if (!authService.checkToken(token))
                throw UnauthorizedException("User not authorized")
            if (authService.logoutUser(token))
                call.respond(HttpStatusCode.OK, mapOf("status" to "Ok"))
            else
                call.respond(HttpStatusCode.BadRequest, mapOf("status" to "X-Auth-Token not exist"))
        }

        post("/check-token"){
            val tokenDto = call.receive<TokenDto>()
            if (authService.checkToken(tokenDto.token))
                call.respond(HttpStatusCode.Found, mapOf("status" to "Ok"))
            else
                call.respond(HttpStatusCode.NotFound, mapOf("status" to "Invalid X-Auth-Token"))
        }
    }
}