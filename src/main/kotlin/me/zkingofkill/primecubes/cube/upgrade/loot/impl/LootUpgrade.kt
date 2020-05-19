package me.zkingofkill.primecubes.cube.upgrade.loot.impl

import fr.minuskube.inv.content.SlotPos
import me.zkingofkill.primecubes.cube.UpgradeType
import me.zkingofkill.primecubes.cube.upgrade.IUpgrade
import me.zkingofkill.primecubes.cube.upgrade.IUpgradeLevel
import org.bukkit.inventory.ItemStack

class LootUpgrade(
                  override var name: String,
                  override var slotPos: SlotPos,
                  override var itemStack: ItemStack,
                  override var levels: ArrayList<LootLevel>,
                  override var levelMax: Int = levels.maxBy { it.level }!!.level,
                  override var upgradeType: UpgradeType) : IUpgrade<LootLevel> {
    override fun nextLevel(currentLevel: Int): IUpgradeLevel? {
        return levels.find { it.level == currentLevel + 1 }
    }
}