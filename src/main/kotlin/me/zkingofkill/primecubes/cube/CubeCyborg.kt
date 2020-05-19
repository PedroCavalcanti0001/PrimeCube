package me.zkingofkill.primecubes.cube

import me.zkingofkill.primecubes.cube.upgrade.cyborgspeed.impl.CyborgSpeedLevel

class CubeCyborg(var cube: Cube, var lastBreak: Long = System.currentTimeMillis()) {

    fun isToBreak(): Boolean {
        val iupgrade = cube.iUpgrade(UpgradeType.CYBORGSPEED)
        val level = cube.level(UpgradeType.CYBORGSPEED)
        val levels = iupgrade.levels as ArrayList<CyborgSpeedLevel>
        var f = -1.0
        val now = System.currentTimeMillis()
        val totalTime = levels.find { it.level == level }!!.time
        val r = (now - lastBreak) / 1000
        f = ((r - totalTime) * -1).toDouble()
        return if (f <= 0) {
            lastBreak = System.currentTimeMillis()
            true
        } else false
    }
}