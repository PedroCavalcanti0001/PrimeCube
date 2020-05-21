package me.zkingofkill.primecubes.util

import org.bukkit.Bukkit
import org.bukkit.Location

//Location Utils
fun Location.locSerializer(): String {
    return this.world.name + ":" + this.x + ":" + this.y + ":" + this.z + ":" + this.yaw + ":" + this.pitch
}

//Location Utils
fun String.locDeserializer(): Location? {
    val parts = this.split(":")
    if (parts.size == 6) {
        if(Bukkit.getServer().getWorld(parts[0]) != null) {
            val w = Bukkit.getServer().getWorld(parts[0])
            val x = java.lang.Double.parseDouble(parts[1])
            val y = java.lang.Double.parseDouble(parts[2])
            val z = java.lang.Double.parseDouble(parts[3])
            val yaw = java.lang.Float.parseFloat(parts[4])
            val pitch = java.lang.Float.parseFloat(parts[5])
            return Location(w, x, y, z, yaw, pitch)
        }
    }
    return null
}
