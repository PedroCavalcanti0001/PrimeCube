package me.zkingofkill.primecubes.database

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import me.zkingofkill.primecubes.Main
import me.zkingofkill.primecubes.cube.Cube
import me.zkingofkill.primecubes.cube.CubeDrop
import me.zkingofkill.primecubes.cube.UpgradeType
import me.zkingofkill.primecubes.util.locDeserializer
import me.zkingofkill.primecubes.util.locSerializer
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.util.logging.Level

abstract class Database(var plugin: Main) {
    var connection: Connection? = null

    var table = "cubes"
    var tokens = 0
    abstract val sQLConnection: Connection?

    abstract fun load()

    fun loadCubes(): ArrayList<Cube> {
        val con = sQLConnection
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
        val con = sQLConnection
        try {

            val insert =
                    con!!.prepareStatement(
                            "INSERT OR REPLACE INTO $table(" +
                                    "typeId," +
                                    "uniqueId," +
                                    " owner," +
                                    " storage," +
                                    " location," +
                                    " upgrades) VALUES (?,?,?,?,?,?);"
                    )
            insert.setInt(1, cube.typeId)
            insert.setInt(2, cube.uniqueId)
            insert.setString(3, cube.owner)
            insert.setString(4, Gson().toJson(cube.storage))
            insert.setString(5, cube.location.locSerializer())
            insert.setString(6, Gson().toJson(cube.upgrades))
            insert.execute()
            insert.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            con?.close()
        }
    }

    fun delete(cube: Cube) {
        val con = sQLConnection
        try {

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

    fun close(ps: PreparedStatement?, rs: ResultSet?) {
        try {
            ps?.close()
            rs?.close()
        } catch (ex: SQLException) {
            Error.close(plugin, ex);
        }
    }

}