package org.nekrasov.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*
import org.nekrasov.domain.dto.request.CreateUserDto
import org.nekrasov.domain.dto.request.UpdateUserDto

fun Application.configureRequestValidation() {
    install(RequestValidation) {
        validate<CreateUserDto> { createUserDto ->
            if (createUserDto.username.length !in 4..20)
                ValidationResult.Invalid("Wrong size for username: ${createUserDto.username.length}. Minimum length: 4 characters. Maximum length: 20 characters")
            else if (createUserDto.firstName.length !in 2..50)
                ValidationResult.Invalid("Wrong size for firstName: ${createUserDto.firstName.length}. Minimum length: 2 characters. Maximum length: 50 characters")
            else if (createUserDto.lastName.length !in 2..50)
                ValidationResult.Invalid("Wrong size for lastName: ${createUserDto.lastName.length}. Minimum length: 2 characters. Maximum length: 50 characters")
            else if (createUserDto.password.length !in 8..20)
                ValidationResult.Invalid("Wrong size for password: ${createUserDto.password.length}. Minimum length: 8 characters. Maximum length: 20 characters")
            else
                ValidationResult.Valid
        }
        validate<UpdateUserDto> { updateUserDto ->
            if (updateUserDto.username.length !in 4..20)
                ValidationResult.Invalid("Wrong size for username: ${updateUserDto.username.length}. Minimum length: 4 characters. Maximum length: 20 characters")
            else if (updateUserDto.firstName.length !in 2..50)
                ValidationResult.Invalid("Wrong size for firstName: ${updateUserDto.firstName.length}. Minimum length: 2 characters. Maximum length: 50 characters")
            else if (updateUserDto.lastName.length !in 2..50)
                ValidationResult.Invalid("Wrong size for lastName: ${updateUserDto.lastName.length}. Minimum length: 2 characters. Maximum length: 50 characters")
            else
                ValidationResult.Valid
        }
    }
}