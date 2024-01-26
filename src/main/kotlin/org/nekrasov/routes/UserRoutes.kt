package org.nekrasov.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.nekrasov.domain.dto.request.UpdateUserDto
import org.nekrasov.domain.dto.response.userToUserDto
import org.nekrasov.domain.service.AuthService
import org.nekrasov.domain.service.UserService

fun Route.userRoutes(authService: AuthService,
                     userService: UserService
){
    route("/users") {
        get{
            val token = call.request.headers["X-Auth-Token"]
            if (!authService.checkToken(token)){
                call.respond(HttpStatusCode.Unauthorized, mapOf("status" to "User not authorized"))
                return@get
            }

            val id = call.parameters["id"] ?: run{
                call.respond(HttpStatusCode.BadRequest, mapOf("status" to "Parameter id not specified in query"))
                return@get
            }

            val idUser = id.toLongOrNull() ?: run{
                call.respond(HttpStatusCode.BadRequest, mapOf("status" to "Parameter id requires the Long type"))
                return@get
            }

            userService.getUser(idUser)?.let{
                call.respond(HttpStatusCode.Found, userToUserDto(it))
            } ?: call.respond(HttpStatusCode.NotFound, mapOf("status" to "User not found"))
        }

        get("chats"){
            val token = call.request.headers["X-Auth-Token"]
            if (!authService.checkToken(token)){
                call.respond(HttpStatusCode.Unauthorized, mapOf("status" to "User not authorized"))
                return@get
            }

            val id = call.parameters["id"] ?: run{
                call.respond(HttpStatusCode.BadRequest, mapOf("status" to "Parameter id not specified in query"))
                return@get
            }

            val idUser = id.toLongOrNull() ?: run{
                call.respond(HttpStatusCode.BadRequest, mapOf("status" to "Parameter id requires the Long type"))
                return@get
            }
            call.respond(HttpStatusCode.OK, userService.getUserChats(idUser))
        }

        patch{
            val token = call.request.headers["X-Auth-Token"]
            if (!authService.checkToken(token)){
                call.respond(HttpStatusCode.Unauthorized, mapOf("status" to "User not authorized"))
                return@patch
            }

            val updateUserDto = call.receive<UpdateUserDto>()

            if (authService.checkUser(updateUserDto.id, token!!) && userService.updateUser(updateUserDto)){
                call.respond(HttpStatusCode.OK, mapOf("status" to "Ok"))
            } else {
                call.respond(HttpStatusCode.Conflict, mapOf("status" to "Refusal to edit user"))
            }
        }

        delete {
            val token = call.request.headers["X-Auth-Token"]
            if (!authService.checkToken(token)){
                call.respond(HttpStatusCode.Unauthorized, mapOf("status" to "User not authorized"))
                return@delete
            }

            val id = call.parameters["id"] ?: run{
                call.respond(HttpStatusCode.BadRequest, mapOf("status" to "Parameter id not specified in query"))
                return@delete
            }

            val idUser = id.toLongOrNull() ?: run{
                call.respond(HttpStatusCode.BadRequest, mapOf("status" to "Parameter id requires the Long type"))
                return@delete
            }

            if (authService.checkUser(idUser, token!!) && userService.deleteUser(idUser)){
                call.respond(HttpStatusCode.OK, mapOf("status" to "Ok"))
            } else {
                call.respond(HttpStatusCode.Conflict, mapOf("status" to "Refusal to delete user"))
            }
        }

        get("/all"){
            val token = call.request.headers["X-Auth-Token"]
            if (!authService.checkToken(token)){
                call.respond(HttpStatusCode.Unauthorized, mapOf("status" to "User not authorized"))
                return@get
            }

            val userList = userService.getAllUsers()
            if (userList.isEmpty()){
                call.respond(HttpStatusCode.NotFound, mapOf("status" to "Users not found"))
            } else {
                call.respond(HttpStatusCode.Found, userList.map(::userToUserDto))
            }
        }

    }
}