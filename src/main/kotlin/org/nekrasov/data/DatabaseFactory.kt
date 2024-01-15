package org.nekrasov.data

import io.ktor.server.config.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.nekrasov.domain.tabels.*
import org.nekrasov.domain.tabels.status.MessageStatusTable
import org.nekrasov.domain.tabels.status.UserChatStatusTable
import org.nekrasov.domain.tabels.status.UserStatusTable

object DatabaseFactory {
    fun init(config: ApplicationConfig) {
        val driverClassName = config.property("storage.driverClassName").getString()
        val jdbcURL = config.property("storage.jdbcURL").getString()
        val user = config.property("storage.user").getString()
        val password = config.property("storage.password").getString()
        val database = Database.connect(
            url = jdbcURL,
            driver = driverClassName,
            user = user,
            password = password
        )
        transaction(database) {
            SchemaUtils.create(
                MessageStatusTable,
                UserChatStatusTable,
                UserStatusTable,
                ChatTable,
                ForwardMessageTable,
                MessageTable,
                MessageUserStatusTable,
                UserChatTable,
                UserTable
            )
        }
    }
    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}