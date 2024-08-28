package model

import io.ktor.server.auth.*

data class UserDto(
    val email: String,
    val hasPassword: String,
    val userName: String

) :Principal{
}