package me.zkingofkill.primecubes.cube.upgrade.cyborgspeed.impl

import fr.minuskube.inv.content.SlotPos
import me.zkingofkill.primecubes.cube.upgrade.cyborgspeed.ICyborgLevel

class CyborgSpeedLevel(override var level: Int,
                       override var price: Double,
                       override var slotPos: SlotPos,
                       override var blocksPerSecond: Double) : ICyborgLevel {
}