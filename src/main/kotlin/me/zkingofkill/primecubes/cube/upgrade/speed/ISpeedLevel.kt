package me.zkingofkill.primecubes.cube.upgrade.speed

import fr.minuskube.inv.content.SlotPos
import me.zkingofkill.primecubes.cube.upgrade.IUpgradeLevel

interface ISpeedLevel : IUpgradeLevel{
    override var level: Int
    override var price: Double

    var timeToRegenerate: Int
}