package me.zkingofkill.primecubes.cube.upgrades.storage

import fr.minuskube.inv.content.SlotPos
import me.zkingofkill.primecubes.cube.upgrades.IUpgradeLevel

interface IStorageLevel : IUpgradeLevel{
    override var level: Int
    override var price: Double
    override var slotPos: SlotPos
    var pages: Int
}