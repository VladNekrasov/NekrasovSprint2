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
import org.nekrasov.exceptions.*


fun Route.userRoutes(authService: AuthService,
                     userService: UserService
){
    route("/api/v1/users") {
        get{
            val token = call.request.headers["X-Auth-Token"] ?: throw MissingHeaderException("Missing  X-Auth-Token header")
            if (!authService.checkToken(token))
                throw UnauthorizedException("User not authorized")
            val pageParameter = call.parameters["page"] ?: throw MissingQueryParameterException("Parameter page not specified in query")
            val page = pageParameter.toLongOrNull() ?: throw IncompatibleQueryParameterTypeException("Parameter page requires the Long type")
            val sizeParameter = call.parameters["size"] ?: throw MissingQueryParameterException("Parameter size not specified in query")
            val size = sizeParameter.toIntOrNull() ?: throw IncompatibleQueryParameterTypeException("Parameter size requires the Int type")
            if (size<=0 || page<=0)
                throw IncompatibleQueryParameterTypeException("Parameter size and page more than zero")

            val userList = userService.getAllUsers(page, size)
            call.respond(HttpStatusCode.OK, userList.map(::userToReadUserDto))
        }

        get("/{id}"){
            val token = call.request.headers["X-Auth-Token"] ?: throw MissingHeaderException("Missing  X-Auth-Token header")
            if (!authService.checkToken(token))
                throw UnauthorizedException("User not authorized")
            val id = call.parameters["id"] ?: throw MissingQueryParameterException("Parameter id not specified in query")
            val idUser = id.toLongOrNull() ?: throw IncompatibleQueryParameterTypeException("Parameter id requires the Long type")

            userService.getUser(idUser)?.let{
                call.respond(HttpStatusCode.Found, userToReadUserDto(it))
            } ?: call.respond(HttpStatusCode.NotFound, mapOf("status" to "User not found"))
        }

        patch{
            val token = call.request.headers["X-Auth-Token"] ?: throw MissingHeaderException("Missing  X-Auth-Token header")
            if (!authService.checkToken(token))
                throw UnauthorizedException("User not authorized")

            val updateUserDto = call.receive<UpdateUserDto>()
            if (!authService.checkUser(updateUserDto.id, token))
                throw ForbiddenException("The current user does not have access to this information")

            if (userService.updateUser(updateUserDto)){
                call.respond(HttpStatusCode.OK, mapOf("status" to "Ok"))
            } else {
                call.respond(HttpStatusCode.BadRequest, mapOf("status" to "Refusal to edit user"))
            }
        }

        delete("/{id}") {
            val token = call.request.headers["X-Auth-Token"] ?: throw MissingHeaderException("Missing  X-Auth-Token header")
            if (!authService.checkToken(token))
                throw UnauthorizedException("User not authorized")
            val id = call.parameters["id"] ?: throw MissingQueryParameterException("Parameter id not specified in query")
            val idUser = id.toLongOrNull() ?: throw IncompatibleQueryParameterTypeException("Parameter id requires the Long type")

            if (!authService.checkUser(idUser, token))
                throw ForbiddenException("The current user does not have access to this information")

            if (userService.deleteUser(idUser)){
                call.respond(HttpStatusCode.OK, mapOf("status" to "Ok"))
            } else {
                call.respond(HttpStatusCode.NotFound, mapOf("status" to "User not found"))
            }
        }

        get("/{id}/chats"){
            val token = call.request.headers["X-Auth-Token"] ?: throw MissingHeaderException("Missing  X-Auth-Token header")
            if (!authService.checkToken(token))
                throw UnauthorizedException("User not authorized")
            val id = call.parameters["id"] ?: throw MissingQueryParameterException("Parameter id not specified in query")
            val idUser = id.toLongOrNull() ?: throw IncompatibleQueryParameterTypeException("Parameter id requires the Long type")

            if (!authService.checkUser(idUser, token))
                throw ForbiddenException("The current user does not have access to this information")

            userService.getUser(idUser)?.let {
                call.respond(HttpStatusCode.OK, userService.getUserChats(idUser))
            } ?: call.respond(HttpStatusCode.NotFound, mapOf("status" to "User not found"))
        }
    }
}