package org.nekrasov.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.nekrasov.domain.dto.request.CreateUserDto
import org.nekrasov.domain.service.AuthService

fun Route.authRoutes(authService: AuthService){
    route("/api/auth") {
        post("/reg"){
            val createUserDto = call.receive<CreateUserDto>()
            authService.createUser(createUserDto)
            call.respond(HttpStatusCode.Created)
        }
        post("/login"){
            TODO()
        }
        post("/logout"){
            TODO()
        }
        post("/check-token"){
            TODO()
        }
    }
}