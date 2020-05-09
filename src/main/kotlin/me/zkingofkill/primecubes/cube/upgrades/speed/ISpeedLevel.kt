package me.zkingofkill.primecubes.cube.upgrades.speed

import fr.minuskube.inv.content.SlotPos
import me.zkingofkill.primecubes.cube.upgrades.IUpgradeLevel

interface ISpeedLevel : IUpgradeLevel{
    override var level: Int
    override var price: Double
    override var slotPos: SlotPos
    var timeToRegenerate: Int
}