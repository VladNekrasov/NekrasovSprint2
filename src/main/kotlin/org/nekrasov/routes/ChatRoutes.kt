package org.nekrasov.routes

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.chatRoutes(){
    route("/chat") {
        get {
            call.respondText("Chat")
        }
    }
}