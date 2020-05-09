package me.zkingofkill.primecubes.cube.upgrades.loot

import fr.minuskube.inv.content.SlotPos
import me.zkingofkill.primecubes.cube.upgrades.IUpgradeLevel

interface ILootLevel : IUpgradeLevel{
    override var level: Int
    override var price: Double
    override var slotPos: SlotPos
    var increasePercentage: Int
}