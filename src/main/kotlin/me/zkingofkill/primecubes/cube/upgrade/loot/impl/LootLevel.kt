package me.zkingofkill.primecubes.cube.upgrade.loot.impl

import fr.minuskube.inv.content.SlotPos
import me.zkingofkill.primecubes.cube.upgrade.loot.ILootLevel

class LootLevel(override var level: Int,
                override var price: Double,
                override var slotPos: SlotPos,
                override var increasePercentage: Int) : ILootLevel {
}