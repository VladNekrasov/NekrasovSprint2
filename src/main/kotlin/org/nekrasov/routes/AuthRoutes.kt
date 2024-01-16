package org.nekrasov.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.nekrasov.domain.dto.request.CreateUserDto
import org.nekrasov.domain.dto.request.ReadUserDto
import org.nekrasov.domain.dto.request.TokenDto
import org.nekrasov.domain.service.AuthService

fun Route.authRoutes(authService: AuthService){
    route("/api/auth") {
        post("/reg"){
            val createUserDto = call.receive<CreateUserDto>()
            if (authService.createUser(createUserDto))
                call.respond(HttpStatusCode.Created, mapOf("status" to "Ok"))
            else
                call.respond(HttpStatusCode.UnprocessableEntity, mapOf("status" to "Duplicate username"))
        }
        post("/login"){
            val readUserDto = call.receive<ReadUserDto>()
            val token = authService.loginUser(readUserDto)
            if (token != null)
                call.respond(HttpStatusCode.Found, mapOf("token" to token, "status" to "Ok"))
            else
                call.respond(HttpStatusCode.NotFound, mapOf("status" to "User not registered, please check your username and password"))
        }
        post("/logout"){
            val token: String? = call.request.headers["X-Auth-Token"]
            if (token != null && authService.logoutUser(token))
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