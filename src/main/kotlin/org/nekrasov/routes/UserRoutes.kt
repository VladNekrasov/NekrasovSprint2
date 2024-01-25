package org.nekrasov.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import org.nekrasov.domain.dto.request.CreateChatDto
import org.nekrasov.domain.service.UserService

fun Route.userRoutes(userService: UserService){
    route("/users") {
        get("/all"){
            val userList = userService.getAllUsers()
            if (userList.isEmpty())
                call.respond(HttpStatusCode.NotFound, mapOf("status" to "Users not found"))
            else
                call.respond(HttpStatusCode.Found, userList)
        }
        get{
            val id:Long? = call.request.queryParameters["id"]?.toLongOrNull()
            if (id == null)
                call.respond(HttpStatusCode.BadRequest, mapOf("status" to "Invalid parameter for id"))
            else {
                val user = userService.getUser(id)
                if (user == null)
                    call.respond(HttpStatusCode.NotFound, mapOf("status" to "User not found"))
                else
                    call.respond(HttpStatusCode.Found, user)
            }

//            try {
//                val id = call.parameters.getOrFail<Long>("id").toLong()
//                val user = userService.getUser(id)
//                if (user == null)
//                    call.respond(HttpStatusCode.NotFound, mapOf("status" to "User not found"))
//                call.respond(HttpStatusCode.Found, user!!)
//            } catch (e: MissingRequestParameterException) {
//                call.respond(HttpStatusCode.BadRequest, mapOf("status" to "Missing parameter for id"))
//            } catch (e: ParameterConversionException) {
//                call.respond(HttpStatusCode.BadRequest, mapOf("status" to "id requires Long"))
//            }
        }
    }
}