package me.zkingofkill.primecubes.cube.upgrades.storage.impl

import fr.minuskube.inv.content.SlotPos
import me.zkingofkill.primecubes.cube.upgrades.storage.IStorageLevel

class StorageLevel(override var level: Int,
                   override var price: Double,
                   override var slotPos: SlotPos,
                   override var pages: Int) : IStorageLevel {
}