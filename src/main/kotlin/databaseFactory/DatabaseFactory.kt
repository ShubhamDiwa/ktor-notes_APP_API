package databaseFactory

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import table.NoteTable
import table.UserTable

object DatabaseFactory {
    fun init(environment: ApplicationEnvironment) {
        Database.connect(hikari(environment))
        transaction {
            SchemaUtils.create(UserTable)
            SchemaUtils.create(NoteTable)
        }
    }


    fun hikari(environment: ApplicationEnvironment): HikariDataSource {
        val config = HikariConfig().apply {
            /* driverClassName = environment.config.property("database.driver").getString()
             jdbcUrl = environment.config.property("database.jdbcUrl").getString()
             username = environment.config.property("database.user").getString()
             password = environment.config.property("database.password").getString()
             maximumPoolSize = environment.config.property("database.maximumPoolSize").getString().toInt()
             transactionIsolation = environment.config.property("database.transactionIsolation").getString()
             isAutoCommit = false*/

            driverClassName = "org.postgresql.Driver"
            jdbcUrl = "jdbc:postgresql://localhost:5432/NoteDb"
            username = "postgres"
            password = "123456"
            maximumPoolSize = 10
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        }
        return HikariDataSource(config)
    }


    suspend fun <T> dbQuery(block: () -> T): T =
        withContext(Dispatchers.IO) {
            transaction { block() }
        }
}