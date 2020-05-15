package me.zkingofkill.primecubes.cube.upgrade.speed.impl

import fr.minuskube.inv.content.SlotPos
import me.zkingofkill.primecubes.cube.UpgradeType
import me.zkingofkill.primecubes.cube.upgrade.IUpgrade
import org.bukkit.inventory.ItemStack

open class SpeedUpgrade(
                        override var name: String,
                        override var slotPos: SlotPos,
                        override var itemStack: ItemStack,
                        override var levels: ArrayList<SpeedLevel>,
                        override var currentLevel: Int = levels.first().level,
                        override var levelMax: Int = levels.maxBy { it.level }!!.level,
                        override var upgradeType: UpgradeType) : IUpgrade<SpeedLevel> {

}