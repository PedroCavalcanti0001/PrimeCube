package me.zkingofkill.primecubes.cube.upgrade.cyborgspeed.impl

import fr.minuskube.inv.content.SlotPos
import me.zkingofkill.primecubes.cube.UpgradeType
import me.zkingofkill.primecubes.cube.upgrade.IUpgrade
import org.bukkit.inventory.ItemStack

class CyborgSpeedUpgrade(
                         override var name: String,
                         override var slotPos: SlotPos,
                         override var itemStack: ItemStack,
                         override var levels: ArrayList<CyborgSpeedLevel>,
                         override var currentLevel: Int = levels.first().level,
                         override var levelMax: Int = levels.maxBy { it.level }!!.level,
                         override var upgradeType: UpgradeType) : IUpgrade<CyborgSpeedLevel> {

}