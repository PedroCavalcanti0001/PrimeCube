package me.zkingofkill.primecubes.cube.upgrade.cyborgfortune.impl

import fr.minuskube.inv.content.SlotPos
import me.zkingofkill.primecubes.cube.upgrade.cyborgfortune.ICyborgFortuneLevel

class CyborgFortuneLevel(override var level: Int,
                         override var price: Double,
                         override var slotPos: SlotPos,
                         override var fortune: Int) : ICyborgFortuneLevel {
}