package me.zkingofkill.primecubes.cube.upgrade.storage

import fr.minuskube.inv.content.SlotPos
import me.zkingofkill.primecubes.cube.upgrade.IUpgradeLevel

interface IStorageLevel : IUpgradeLevel{
    override var level: Int
    override var price: Double
    override var slotPos: SlotPos
    var totalAmountPerDrop: Int
}