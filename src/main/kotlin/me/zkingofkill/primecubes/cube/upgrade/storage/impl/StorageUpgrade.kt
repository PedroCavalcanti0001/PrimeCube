package me.zkingofkill.primecubes.cube.upgrade.storage.impl

import fr.minuskube.inv.content.SlotPos
import me.zkingofkill.primecubes.cube.UpgradeType
import me.zkingofkill.primecubes.cube.upgrade.IUpgrade
import me.zkingofkill.primecubes.cube.upgrade.IUpgradeLevel
import org.bukkit.inventory.ItemStack

class StorageUpgrade(
                     override var name: String,
                     override var slotPos: SlotPos,
                     override var itemStack: ItemStack,
                     override var levels: ArrayList<StorageLevel>,
                     override var levelMax: Int = levels.maxBy { it.level }!!.level,
                     override var upgradeType: UpgradeType) : IUpgrade<StorageLevel> {
    override fun nextLevel(currentLevel: Int): IUpgradeLevel? {
        return levels.find { it.level == currentLevel + 1 }
    }
}