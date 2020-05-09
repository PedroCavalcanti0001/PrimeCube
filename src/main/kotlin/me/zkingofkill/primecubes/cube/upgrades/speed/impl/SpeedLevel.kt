package me.zkingofkill.primecubes.cube.upgrades.speed.impl

import fr.minuskube.inv.content.SlotPos
import me.zkingofkill.primecubes.cube.upgrades.cyborgfortune.ICyborgFortuneLevel
import me.zkingofkill.primecubes.cube.upgrades.speed.ISpeedLevel

class SpeedLevel(override var level: Int,
                 override var price: Double,
                 override var slotPos: SlotPos,
                 override var timeToRegenerate: Int) : ISpeedLevel {
}