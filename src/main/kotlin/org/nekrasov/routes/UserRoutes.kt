package org.nekrasov.routes

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import java.time.LocalDateTime

fun Route.userRoutes(){
    route("/users") {
        get{
            TODO()
        }
        post{
            TODO()
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