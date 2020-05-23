package me.zkingofkill.primecubes.database

import me.zkingofkill.primecubes.Main
import java.util.logging.Level

class Error {
    companion object {
        fun execute(plugin: Main, ex: Exception?) {
            plugin.logger.log(Level.SEVERE, "Couldn't execute MySQL statement: ", ex)
        }

        fun close(plugin: Main, ex: Exception?) {
            plugin.logger.log(Level.SEVERE, "Failed to close MySQL connection: ", ex)
        }
    }
}