package org.nekrasov.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.nekrasov.domain.dto.request.UpdateUserDto
import org.nekrasov.domain.dto.response.userToReadUserDto
import org.nekrasov.domain.service.AuthService
import org.nekrasov.domain.service.UserService
import org.nekrasov.exceptions.IncompatibleQueryParameterTypeException
import org.nekrasov.exceptions.MissingHeaderException
import org.nekrasov.exceptions.MissingQueryParameterException
import org.nekrasov.exceptions.UnauthorizedException


fun Route.userRoutes(authService: AuthService,
                     userService: UserService
){
    route("/users") {
        get{
            val token = call.request.headers["X-Auth-Token"] ?: throw MissingHeaderException("Missing  X-Auth-Token header")
            if (!authService.checkToken(token))
                throw UnauthorizedException("User not authorized")
            val id = call.parameters["id"] ?: throw MissingQueryParameterException("Parameter id not specified in query")
            val idUser = id.toLongOrNull() ?: throw IncompatibleQueryParameterTypeException("Parameter id requires the Long type")

            userService.getUser(idUser)?.let{
                call.respond(HttpStatusCode.Found, userToReadUserDto(it))
            } ?: call.respond(HttpStatusCode.NotFound, mapOf("status" to "User not found"))
        }

        get("chats"){
            val token = call.request.headers["X-Auth-Token"] ?: throw MissingHeaderException("Missing  X-Auth-Token header")
            if (!authService.checkToken(token))
                throw UnauthorizedException("User not authorized")
            val id = call.parameters["id"] ?: throw MissingQueryParameterException("Parameter id not specified in query")
            val idUser = id.toLongOrNull() ?: throw IncompatibleQueryParameterTypeException("Parameter id requires the Long type")

            userService.getUser(idUser)?.let {
                call.respond(HttpStatusCode.OK, userService.getUserChats(idUser))
            } ?: call.respond(HttpStatusCode.NotFound, mapOf("status" to "User not found"))
        }

        patch{
            val token = call.request.headers["X-Auth-Token"] ?: throw MissingHeaderException("Missing  X-Auth-Token header")
            if (!authService.checkToken(token))
                throw UnauthorizedException("User not authorized")

            val updateUserDto = call.receive<UpdateUserDto>()

            if (userService.updateUser(updateUserDto)){
                call.respond(HttpStatusCode.OK, mapOf("status" to "Ok"))
            } else {
                call.respond(HttpStatusCode.BadRequest, mapOf("status" to "Refusal to edit user"))
            }
        }

        delete {
            val token = call.request.headers["X-Auth-Token"] ?: throw MissingHeaderException("Missing  X-Auth-Token header")
            if (!authService.checkToken(token))
                throw UnauthorizedException("User not authorized")
            val id = call.parameters["id"] ?: throw MissingQueryParameterException("Parameter id not specified in query")
            val idUser = id.toLongOrNull() ?: throw IncompatibleQueryParameterTypeException("Parameter id requires the Long type")

            if (userService.deleteUser(idUser)){
                call.respond(HttpStatusCode.OK, mapOf("status" to "Ok"))
            } else {
                call.respond(HttpStatusCode.NotFound, mapOf("status" to "User not found"))
            }
        }

        get("/all"){
            val token = call.request.headers["X-Auth-Token"] ?: throw MissingHeaderException("Missing  X-Auth-Token header")
            if (!authService.checkToken(token))
                throw UnauthorizedException("User not authorized")

            val userList = userService.getAllUsers()
            call.respond(HttpStatusCode.OK, userList.map(::userToReadUserDto))
        }

    }
}