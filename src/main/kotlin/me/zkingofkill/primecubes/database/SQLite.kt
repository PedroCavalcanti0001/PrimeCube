package me.zkingofkill.primecubes.database

import me.zkingofkill.primecubes.Main
import java.io.File
import java.io.IOException
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.util.logging.Level


class SQLite(instance: Main) : Database(instance) {
    private var dbname: String = "cubes"
    private var createTable = "CREATE TABLE IF NOT EXISTS $table" +
            "(`typeId` INTEGER, " +
            "`uniqueId` INTEGER, " +
            "`owner` VARCHAR(25), " +
            "`storage` LONGTEXT NOT NULL," +
            "`upgrades` LONGTEXT NOT NULL," +
            "`location` VARCHAR(400), " +
            "PRIMARY KEY (`uniqueId`));"

    override val sQLConnection: Connection?
        get() {
            val dataFolder = File(plugin.dataFolder, "$dbname.db")
            if (!dataFolder.exists()) {
                try {
                    dataFolder.createNewFile()
                } catch (e: IOException) {
                    plugin.logger.log(Level.SEVERE, "File write error: $dbname.db")
                }
            }
            try {
                if (connection != null && !connection!!.isClosed) {
                    return connection
                }
                Class.forName("org.sqlite.JDBC")
                connection = DriverManager.getConnection("jdbc:sqlite:$dataFolder")
                return connection
            } catch (ex: SQLException) {
                plugin.logger.log(Level.SEVERE, " ")
                plugin.logger.log(Level.SEVERE, "SQLite exception on initialize", ex)
                plugin.logger.log(Level.SEVERE, " ")
            } catch (ex: ClassNotFoundException) {
                plugin.logger.log(Level.SEVERE, " ")
                plugin.logger.log(Level.SEVERE, "You need the SQLite JBDC library. Google it. Put it in /lib folder.")
                plugin.logger.log(Level.SEVERE, " ")
            }
            return null
        }

    override fun load() {
        connection = sQLConnection
        try {
            val s = connection!!.createStatement()
            s.executeUpdate(createTable)
            s.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    init {
        // Set the table name here e.g player_kills
    }
}
