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
import org.nekrasov.utils.ServiceResult
import org.nekrasov.utils.getQueryParameter
import org.nekrasov.utils.respondErrorCode


fun Route.userRoutes(authService: AuthService,
                     userService: UserService
){
    route("/api/v1/users") {
        get{
            val token = call.request.headers["X-Auth-Token"] ?: throw MissingHeaderException("Missing  X-Auth-Token header")
            if (!authService.checkToken(token))
                throw UnauthorizedException("User not authorized")

            val page = call.getQueryParameter<Long>("page")
            val size = call.getQueryParameter<Int>("size")

            userService.getAllUsers(page, size)?.let{
                call.respond(HttpStatusCode.OK, it.map(::userToReadUserDto))
            } ?: call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Parameter size and page more than zero"))
        }

        get("/{id}"){
            val token = call.request.headers["X-Auth-Token"] ?: throw MissingHeaderException("Missing  X-Auth-Token header")
            if (!authService.checkToken(token))
                throw UnauthorizedException("User not authorized")

            val id = call.getQueryParameter<Long>("id")

            userService.getUser(id)?.let{
                call.respond(HttpStatusCode.Found, userToReadUserDto(it))
            } ?: call.respond(HttpStatusCode.NotFound, mapOf("message" to "User not found"))
        }

        patch{
            val token = call.request.headers["X-Auth-Token"] ?: throw MissingHeaderException("Missing  X-Auth-Token header")
            if (!authService.checkToken(token))
                throw UnauthorizedException("User not authorized")

            val updateUserDto = call.receive<UpdateUserDto>()
            if (!authService.checkUser(updateUserDto.id, token))
                throw ForbiddenException("The current user does not have access to update operation")


            when(val result = userService.updateUser(updateUserDto)){
                is ServiceResult.Success -> {
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Ok"))
                }
                is ServiceResult.Error -> {
                    call.respondErrorCode(result.error)
                }
            }
        }

        delete("/{id}") {
            val token = call.request.headers["X-Auth-Token"] ?: throw MissingHeaderException("Missing  X-Auth-Token header")
            if (!authService.checkToken(token))
                throw UnauthorizedException("User not authorized")

            val id = call.getQueryParameter<Long>("id")

            if (!authService.checkUser(id, token))
                throw ForbiddenException("The current user does not have access to delete operation")

            if (userService.deleteUser(id)){
                call.respond(HttpStatusCode.OK, mapOf("message" to "Ok"))
            } else {
                call.respond(HttpStatusCode.NotFound, mapOf("message" to "User not found"))
            }
        }

        get("/{id}/chats"){
            val token = call.request.headers["X-Auth-Token"] ?: throw MissingHeaderException("Missing  X-Auth-Token header")
            if (!authService.checkToken(token))
                throw UnauthorizedException("User not authorized")

            val id = call.getQueryParameter<Long>("id")
            val page = call.getQueryParameter<Long>("page")
            val size = call.getQueryParameter<Int>("size")

            if (!authService.checkUser(id, token))
                throw ForbiddenException("The current user does not have access to this information")

            when(val result = userService.getUserChats(id, page, size)){
                is ServiceResult.Success -> {
                    call.respond(HttpStatusCode.OK, result)
                }
                is ServiceResult.Error -> {
                    call.respondErrorCode(result.error)
                }
            }
        }
    }
}