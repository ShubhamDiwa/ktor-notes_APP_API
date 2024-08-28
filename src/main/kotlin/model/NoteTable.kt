package model

import org.jetbrains.exposed.sql.Table
import table.UserTable

object NoteTable : Table() {
    val id = varchar("id", 512)
    val userEmail = varchar("userEmail", 512).references(UserTable.email)
    val noteTitle = varchar("noteTitle", 512)
    val description = varchar("description", 512)
    val date = long("date")

    override val primaryKey = PrimaryKey(id)
}