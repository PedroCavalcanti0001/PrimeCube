package me.zkingofkill.primecubes.cube.upgrade.cyborgspeed

import fr.minuskube.inv.content.SlotPos
import me.zkingofkill.primecubes.cube.upgrade.IUpgradeLevel

interface ICyborgLevel : IUpgradeLevel{
    override var level: Int
    override var price: Double
    override var slotPos: SlotPos
    var blocksPerSecond: Double
}