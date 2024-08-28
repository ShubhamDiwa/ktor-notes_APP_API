package databaseFactory

import databaseFactory.DatabaseFactory.dbQuery
import model.Note
import model.NoteTable
import model.UserDto
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import table.UserTable

class UserRepo {
    suspend fun addUser(userDto: UserDto) {
        dbQuery {
            UserTable.insert { ut ->
                ut[UserTable.email] = userDto.email
                ut[UserTable.name] = userDto.userName
                ut[UserTable.hashPassword] = userDto.hasPassword
            }


        }

    }

    /* suspend fun getAllNotes(email: String): List<Note> = dbQuery {

         NoteTable.select {
             NoteTable.userEmail.eq(email)
         }.mapNotNull { rowToNote(it) }

     }*/


    suspend fun findUserByEmail(email: String) = dbQuery {
        UserTable.select(UserTable.email eq email).map {
            rowToUser(it)
        }.singleOrNull()
    }

    private fun rowToUser(row: ResultRow): UserDto? {
        if (row == null)
            return null
        return UserDto(
            email = row[UserTable.email],
            hasPassword = row[UserTable.hashPassword],
            userName = row[UserTable.name]
        )

    }


    //    ============== NOTES ==============


    suspend fun addNote(note: Note, email: String) {
        dbQuery {
            NoteTable.insert { nt ->
                nt[NoteTable.id] = note.id
                nt[NoteTable.userEmail] = email
                nt[NoteTable.noteTitle] = note.noteTitle
                nt[NoteTable.description] = note.description
                nt[NoteTable.date] = note.date
            }

        }

    }


    private fun rowToNote(row: ResultRow): Note? {
        if (row == null)
            return null
        return Note(
            id = row[NoteTable.id],
            noteTitle = row[NoteTable.noteTitle],
            description = row[NoteTable.description],
            date = row[NoteTable.date]
        )

    }

    suspend fun getAllNotes(email: String): List<Note> = dbQuery {
        NoteTable
            .select { NoteTable.userEmail eq email }
            .mapNotNull { rowToNote(it) }
    }


    suspend fun updateNote(note: Note, email: String) {
        dbQuery {
            NoteTable.update(
                where = {
                    NoteTable.userEmail.eq(email) and NoteTable.id.eq(note.id)
                }
            ) { nt ->
                nt[NoteTable.noteTitle] = note.noteTitle
                nt[NoteTable.description] = note.description
                nt[NoteTable.date] = note.date
            }

        }

    }

    suspend fun deleteNote(id: String, email: String) {
        dbQuery {
            NoteTable.deleteWhere { NoteTable.userEmail.eq(email) and NoteTable.id.eq(id) }
        }
    }

}