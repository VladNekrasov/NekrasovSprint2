package org.nekrasov

import io.ktor.server.application.*
import org.nekrasov.data.DatabaseFactory
import org.nekrasov.plugins.configureAuth
import org.nekrasov.plugins.configureSerialization
import org.nekrasov.plugins.configureWebSockets
import org.nekrasov.plugins.usersModule

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    DatabaseFactory.init(environment.config)
    configureSerialization()
    configureAuth()
    configureWebSockets()
    usersModule()
}