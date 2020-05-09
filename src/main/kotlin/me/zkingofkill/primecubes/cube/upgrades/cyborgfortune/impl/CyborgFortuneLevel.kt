package me.zkingofkill.primecubes.cube.upgrades.cyborgfortune.impl

import fr.minuskube.inv.content.SlotPos
import me.zkingofkill.primecubes.cube.upgrades.cyborgfortune.ICyborgFortuneLevel

class CyborgFortuneLevel(override var level: Int,
                         override var price: Double,
                         override var slotPos: SlotPos,
                         override var fortune: Int) : ICyborgFortuneLevel {
}