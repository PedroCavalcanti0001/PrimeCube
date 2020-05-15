package me.zkingofkill.primecubes.cube.upgrade.cyborgfortune

import fr.minuskube.inv.content.SlotPos
import me.zkingofkill.primecubes.cube.upgrade.IUpgradeLevel

interface ICyborgFortuneLevel : IUpgradeLevel{
    override var level: Int
    override var price: Double
    override var slotPos: SlotPos
    var fortune: Int
}