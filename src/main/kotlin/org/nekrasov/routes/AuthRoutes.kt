package org.nekrasov.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.nekrasov.domain.dto.request.CreateUserDto
import org.nekrasov.domain.dto.request.ReadUserDto
import org.nekrasov.domain.service.AuthService

fun Route.authRoutes(authService: AuthService){
    route("/api/auth") {
        post("/reg"){
            val createUserDto = call.receive<CreateUserDto>()
            authService.createUser(createUserDto)
            call.respond(HttpStatusCode.Created)
        }
        post("/login"){
            val readUserDto = call.receive<ReadUserDto>()
            if (readUserDto.username!=null || readUserDto.email!=null || readUserDto.phone!=null){
                authService.loginUser(readUserDto)
                call.respond(HttpStatusCode.Created)
            }
            else{
                call.respond(HttpStatusCode.BadRequest)
            }
        }
        post("/logout"){
            TODO()
        }
        post("/check-token"){
            TODO()
        }
    }
}