package me.zkingofkill.primecubes.cube.upgrade.layers

import fr.minuskube.inv.content.SlotPos
import me.zkingofkill.primecubes.cube.upgrade.IUpgradeLevel

interface ILayersLevel : IUpgradeLevel{
    override var level: Int
    override var price: Double
    override var slotPos: SlotPos
    var sections: Int
}