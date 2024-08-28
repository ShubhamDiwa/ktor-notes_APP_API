package route

import authentication.JWTServices
import databaseFactory.UserRepo
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.locations.*
import io.ktor.server.locations.post
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import model.LoginRequest
import model.RegisterRequest
import model.SimpleResponse
import model.UserDto
import route.UserRoute.UserLoginRoute
import route.UserRoute.UserRegisterRoute

fun Route.UserRoutes(
    db: UserRepo,
    jwtService: JWTServices,
    hashFunction: (String) -> String
) {

    post<UserRegisterRoute> {
        val registerRequest = try {
            call.receive<RegisterRequest>()
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "failed", "Missing Some Fields"))
            return@post
        }

        try {
            val user = UserDto(registerRequest.email, hashFunction(registerRequest.password), registerRequest.name)
            db.addUser(user)
            call.respond(HttpStatusCode.OK, SimpleResponse(true, "Success!!", jwtService.generateToken(user)))
        } catch (e: Exception) {
            call.respond(
                HttpStatusCode.Conflict,
                SimpleResponse(false, "Failed!!", e.message ?: "Some Problem Occurred!")
            )
        }
    }

    post<UserLoginRoute> {
        val loginRequest = try {
            call.receive<LoginRequest>()
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "Failed!!", "Missing Some Fields"))
            return@post
        }

        try {
            val user = db.findUserByEmail(loginRequest.email)

            if (user == null) {
                call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "Failed!!", "Wrong Email Id"))
            } else {

                if (user.hasPassword == hashFunction(loginRequest.password)) {
                    call.respond(HttpStatusCode.OK, SimpleResponse(true, "Success!!", jwtService.generateToken(user)))
                } else {
                    call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "Failed!!", "Password Incorrect!"))
                }
            }
        } catch (e: Exception) {
            call.respond(
                HttpStatusCode.Conflict,
                SimpleResponse(false, "Failed!!", e.message ?: "Some Problem Occurred!")
            )
        }
    }

}

class UserRoute {

    companion object {
        const val API_VERSION = "/v1"
        const val USERS = "$API_VERSION/users"
        const val REGISTER_REQUEST = "$USERS/register"
        const val LOGIN_REQUEST = "$USERS/login"
    }

    /* const val API_VERSION = "/v1"
     const val USERS = "$API_VERSION/users"
     const val REGISTER_REQUEST = "$USERS/register"
     const val LOGIN_REQUEST = "$USERS/login"*/

    @Location(REGISTER_REQUEST)
    class UserRegisterRoute

    @Location(LOGIN_REQUEST)
    class UserLoginRoute


}