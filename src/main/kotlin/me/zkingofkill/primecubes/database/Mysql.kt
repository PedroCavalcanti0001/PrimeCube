package me.zkingofkill.primecubes.database

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import me.zkingofkill.primecubes.Main.Companion.singleton
import me.zkingofkill.primecubes.cube.Cube
import me.zkingofkill.primecubes.cube.CubeDrop
import me.zkingofkill.primecubes.cube.UpgradeType
import me.zkingofkill.primecubes.util.locDeserializer
import me.zkingofkill.primecubes.util.locSerializer
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.SQLException

class Mysql {
    private var table: String = singleton.config.getString("mysql.table")
    private var con: Connection? = openCon()

    private fun openCon(): Connection? {
        if (con != null && !con!!.isClosed) return con
        try {
            val password = singleton.config.getString("mysql.password")
            val user = singleton.config.getString("mysql.user")
            val host = singleton.config.getString("mysql.host")
            val port = singleton.config.getInt("mysql.port")
            val database = singleton.config.getString("mysql.database")
            val type = "jdbc:mysql://"
            val url = "$type$host:$port/$database"
            return DriverManager.getConnection(url, user, password)

        } catch (e: Exception) {
            e.printStackTrace()
            println("  ")
            println("  ")
            println("[PrimeCubes] O plugin não se conectou ao mysql por favor verifique sua configuração.")
            println("  ")
            println("  ")
            singleton.pluginLoader.disablePlugin(singleton)
        }
        return null
    }

    fun init() {
        con = openCon()
        val createTable: PreparedStatement
        try {
            createTable =
                    con!!.prepareStatement(
                            "CREATE TABLE IF NOT EXISTS $table" +
                                    "(`typeId` INTEGER, " +
                                    "`uniqueId` INTEGER, " +
                                    "`owner` VARCHAR(25), " +
                                    "`storage` LONGTEXT NOT NULL," +
                                    "`upgrades` LONGTEXT NOT NULL," +
                                    "`location` VARCHAR(400), " +
                                    "PRIMARY KEY (`uniqueId`));"
                    )

            createTable.execute()
            createTable.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            con!!.close()
        }

    }

    fun loadCubes(): ArrayList<Cube> {
        con = openCon()
        val results = arrayListOf<Cube>()
        val ps =
                con!!.prepareStatement("SELECT * FROM $table")
        val rs = ps.executeQuery()
        while (rs.next()) {
            val type = rs.getInt("typeId")
            val uniqueId = rs.getInt("uniqueId")
            val location = rs.getString("location").locDeserializer()!!
            val gson = GsonBuilder().create()
            val storage = gson.fromJson(rs.getString("storage"), Array<CubeDrop>::class.java)
                    .toMutableList()
            val typeOfHashMap = object : TypeToken<HashMap<UpgradeType, Int>>() {}.type
            val upgrades: Map<UpgradeType, Int> = gson.fromJson(rs.getString("upgrades"), typeOfHashMap)
            val owner = rs.getString("owner")

            val cube = Cube(typeId = type,
                    owner = owner,
                    uniqueId = uniqueId,
                    location = location,
                    storage = storage as ArrayList<CubeDrop>,
                    upgrades = upgrades as HashMap<UpgradeType, Int>,
                    placeTime = -1)
            results.add(cube)
        }
        return results
    }

    fun upsert(cube: Cube) {
        try {
            con = openCon()
            val insert =
                    con!!.prepareStatement(
                            "INSERT INTO $table(" +
                                    "typeId," +
                                    "uniqueId," +
                                    " owner," +
                                    " storage," +
                                    " location," +
                                    " upgrades) VALUES (?,?,?,?,?,?) " +
                                    "ON DUPLICATE KEY UPDATE typeId = ?,uniqueId= ?, owner= ?, storage= ?, location= ?, upgrades= ?;"
                    )
            insert.setInt(1, cube.typeId)
            insert.setInt(2, cube.uniqueId)
            insert.setString(3, cube.owner)
            insert.setString(4, Gson().toJson(cube.storage))
            insert.setString(5, cube.location.locSerializer())
            insert.setString(6, Gson().toJson(cube.upgrades))

            insert.setInt(7, cube.typeId)
            insert.setInt(8, cube.uniqueId)
            insert.setString(9, cube.owner)
            insert.setString(10, Gson().toJson(cube.storage))
            insert.setString(11, cube.location.locSerializer())
            insert.setString(12, Gson().toJson(cube.upgrades))

            insert.execute()
            con!!.close()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun delete(cube: Cube) {
        try {
            con = openCon()
            val delete = con!!.prepareStatement("DELETE FROM $table WHERE uniqueId = ?;")
            delete.setInt(1, cube.uniqueId)
            delete.execute()
            delete.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            con!!.close()
        }
    }
}