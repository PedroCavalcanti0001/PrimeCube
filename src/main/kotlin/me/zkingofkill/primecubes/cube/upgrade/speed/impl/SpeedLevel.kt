package me.zkingofkill.primecubes.cube.upgrade.speed.impl

import fr.minuskube.inv.content.SlotPos
import me.zkingofkill.primecubes.cube.upgrade.speed.ISpeedLevel

class SpeedLevel(override var level: Int,
                 override var price: Double,
                 override var timeToRegenerate: Int) : ISpeedLevel {
}