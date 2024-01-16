package org.nekrasov.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.nekrasov.domain.models.User
import org.nekrasov.domain.service.UserService

fun Route.userRoutes(userService: UserService){
    route("/users") {
        get{
            TODO()
        }
        post{
            val user = call.receive<User>()
            userService.createUser(user)
            call.respond(HttpStatusCode.OK)
        }
        get("{id}") {
            TODO()
        }
        delete("{id}"){
            TODO()
        }
        put("{id}"){
            TODO()
        }
    }
}