package com.example

import authentication.JWTServices
import com.example.plugins.configureDatabases
import com.example.plugins.configureRouting
import com.example.plugins.configureSecurity
import com.example.plugins.configureSerialization
import databaseFactory.DatabaseFactory
import databaseFactory.UserRepo
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.engine.*
import io.ktor.server.locations.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module).start(wait = true)
}

fun Application.module() {
    DatabaseFactory.init(environment)
    val db = UserRepo()
    install(Locations)

    install(Authentication) {
        jwt("jwt") {
            verifier(JWTServices.varifier)
            realm = "Note Server"
            validate {
                val payload = it.payload
                val email = payload.getClaim("email").asString()
                val user = db.findUserByEmail(email)
                user
            }
        }
    }

    configureSerialization()
    configureSecurity()
    configureDatabases()
    configureRouting()
}
