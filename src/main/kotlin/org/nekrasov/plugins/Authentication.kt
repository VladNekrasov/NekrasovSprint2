package org.nekrasov.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*

fun Application.configureAuth() {
    install(Authentication) {
        basic(name = "auto-basic"){
            validate { credentials ->
                if (credentials.name == credentials.password){
                    UserIdPrincipal(credentials.name)
                } else {
                    null
                }
            }
        }
    }
}