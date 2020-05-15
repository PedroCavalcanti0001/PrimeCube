package me.zkingofkill.primecubes.cube

import org.bukkit.Location

class CubeBlockLocation(var location:Location, var lastBreak:Long){

    fun timeToRefil(cube: Cube): Double {
        var f = -1.0
        val now = System.currentTimeMillis()
        val totalTime = cube.timeToRegenerate()
        val r = (now - lastBreak) / 1000
        f = (((r - totalTime) * -1).toDouble())
        return f
    }
}