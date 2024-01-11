package org.nekrasov.data

import io.ktor.server.config.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.nekrasov.domain.tabels.*

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
            exec("do \$\$\n" +
                    "    begin\n" +
                    "        if not exists (select 1 from pg_type where typname = 'messagestatusenum') then\n" +
                    "            CREATE TYPE MessageStatusEnum AS ENUM ('Read', 'Unread', 'Deleted');\n" +
                    "        end if;\n" +
                    "    end \$\$;")
            exec("do \$\$\n" +
                    "    begin\n" +
                    "        if not exists (select 1 from pg_type where typname = 'userchatstatusenum') then\n" +
                    "            CREATE TYPE UserChatStatusEnum AS ENUM ('Admin', 'Restricted', 'Ban', 'Standard', 'Left');\n" +
                    "        end if;\n" +
                    "    end \$\$;")
            exec("do \$\$\n" +
                    "    begin\n" +
                    "        if not exists (select 1 from pg_type where typname = 'userstatusenum') then\n" +
                    "            CREATE TYPE UserStatusEnum AS ENUM ('Empty', 'Online', 'Offline', 'Restricted', 'Ban', 'Deleted');\n" +
                    "        end if;\n" +
                    "    end \$\$;")
            SchemaUtils.create(ChatTable)
            SchemaUtils.create(ForwardMessageTable)
            SchemaUtils.create(MessageTable)
            SchemaUtils.create(MessageUserStatusTable)
            SchemaUtils.create(UserChatTable)
            SchemaUtils.create(UserTable)
        }
    }
    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}