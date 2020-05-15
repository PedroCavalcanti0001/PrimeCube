package me.zkingofkill.primecubes.cube.upgrade.storage.impl

import fr.minuskube.inv.content.SlotPos
import me.zkingofkill.primecubes.cube.upgrade.storage.IStorageLevel

class StorageLevel(override var level: Int,
                   override var price: Double,
                   override var slotPos: SlotPos,
                   override var totalAmountPerDrop: Int) : IStorageLevel {
}