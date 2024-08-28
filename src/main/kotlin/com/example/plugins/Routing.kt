package com.example.plugins

import authentication.JWTServices
import databaseFactory.UserRepo
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import model.UserDto
import route.NoteRoutes
import route.UserRoutes

fun Application.configureRouting() {

    val userRepo = UserRepo()  // Instantiate UserRepo
    val jwtService = JWTServices
    val hashFunction: (String) -> String = { it }

    routing {
        UserRoutes(userRepo, jwtService, hashFunction)
        NoteRoutes(userRepo, hashFunction)


        /*get("/") {
            call.respondText("Hello World!")
        }*/

        get("/token") {
            val email = call.request.queryParameters["email"]
            val password = call.request.queryParameters["password"]
            val username = call.request.queryParameters["username"]

            if (email == null || password == null || username == null) {
                call.respond(HttpStatusCode.BadRequest, "Missing query parameters")
                return@get
            }

            try {
                val user = UserDto(email, password, username)
                val token = JWTServices.generateToken(user)
                call.respondText(token)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Error generating token: ${e.message}")
            }
        }


    }
}
