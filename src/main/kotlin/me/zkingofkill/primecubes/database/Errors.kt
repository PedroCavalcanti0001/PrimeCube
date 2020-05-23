package me.zkingofkill.primecubes.database

public class Errors {
    companion object {
        fun sqlConnectionExecute(): String {
            return "Couldn't execute MySQL statement: "
        }

        fun sqlConnectionClose(): String {
            return "Failed to close MySQL connection: "
        }

        fun noSQLConnection(): String {
            return "Unable to retreive MYSQL connection: "
        }

        fun noTableFound(): String {
            return "Database Error: No Table Found"
        }
    }
}