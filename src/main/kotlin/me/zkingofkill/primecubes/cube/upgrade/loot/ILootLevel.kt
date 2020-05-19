package me.zkingofkill.primecubes.cube.upgrade.loot

import fr.minuskube.inv.content.SlotPos
import me.zkingofkill.primecubes.cube.upgrade.IUpgradeLevel

interface ILootLevel : IUpgradeLevel{
    override var level: Int
    override var price: Double

    var increasePercentage: Int
}