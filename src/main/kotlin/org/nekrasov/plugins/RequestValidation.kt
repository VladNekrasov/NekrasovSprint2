package org.nekrasov.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*
import org.nekrasov.domain.dto.request.UpdateUserDto
import org.nekrasov.domain.service.UserService

fun Application.configureRequestValidation(userService: UserService) {
    install(RequestValidation) {
//        validate<UpdateUserDto> { updateUserDto ->
//            val userFind = userRepository.readByUsername(updateUserDto.username)
//            if (userService.getUser(updateUserDto.id) == null)
//                ValidationResult.Invalid("User not found")
//            else
//                ValidationResult.Valid
//        }
    }
}