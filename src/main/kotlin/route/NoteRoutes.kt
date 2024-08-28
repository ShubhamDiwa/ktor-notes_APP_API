package route

import databaseFactory.UserRepo
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.locations.*
import io.ktor.server.locations.post
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import model.Note
import model.SimpleResponse
import model.UserDto
import route.UserRoute.Companion.API_VERSION


const val NOTES = "$API_VERSION/notes"
const val CREATE_NOTES = "$NOTES/create"
const val UPDATE_NOTES = "$NOTES/update"
const val DELETE_NOTES = "$NOTES/delete"

@Location(CREATE_NOTES)
class NoteCreateRoute

@Location(NOTES)
class NoteGetRoute

@Location(UPDATE_NOTES)
class NoteUpdateRoute

@Location(DELETE_NOTES)
class NoteDeleteRoute


fun Route.NoteRoutes(
    db: UserRepo,
    hashFunction: (String) -> String
) {

    authenticate("jwt") {

        post<NoteCreateRoute> {

            val note = try {
                call.receive<Note>()
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "Failed!!","Missing Fields"))
                return@post
            }

            try {

                val email = call.principal<UserDto>()!!.email
                db.addNote(note, email)
                call.respond(HttpStatusCode.OK, SimpleResponse(true, "Success!!","Note Added Successfully!"))

            } catch (e: Exception) {

                call.respond(HttpStatusCode.Conflict, SimpleResponse(false,  "failed",e.message ?:"Some Problem Occurred!"))
            }

        }


        get<NoteGetRoute> {

            try {
                val email = call.principal<UserDto>()!!.email
                val notes = db.getAllNotes(email)
                call.respond(HttpStatusCode.OK, notes)
            } catch (e: Exception) {

                call.respond(HttpStatusCode.Conflict, emptyList<Note>())
            }
        }



        post<NoteUpdateRoute> {

            val note = try {
                call.receive<Note>()
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "failed","Missing Fields"))
                return@post
            }

            try {

                val email = call.principal<UserDto>()!!.email
                db.updateNote(note, email)
                call.respond(HttpStatusCode.OK, SimpleResponse(true, "Success","Note Updated Successfully!"))

            } catch (e: Exception) {
                call.respond(HttpStatusCode.Conflict, SimpleResponse(false, "",e.message ?: "Some Problem Occurred!"))
            }

        }


        delete<NoteDeleteRoute> {

            val noteId = try {
                call.request.queryParameters["id"]!!
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "failed!!","QueryParameter:id is not present"))
                return@delete
            }


            try {

                val email = call.principal<UserDto>()!!.email
                db.deleteNote(noteId, email)
                call.respond(HttpStatusCode.OK, SimpleResponse(true, "Success!!","Note Deleted Successfully!"))

            } catch (e: Exception) {
                call.respond(HttpStatusCode.Conflict, SimpleResponse(false, "failed!!",e.message ?: "Some problem Occurred!"))
            }

        }


    }
}