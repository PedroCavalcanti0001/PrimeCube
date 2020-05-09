package me.zkingofkill.primecubes.cube.upgrades.speed.impl

import fr.minuskube.inv.content.SlotPos
import me.zkingofkill.primecubes.cube.upgrades.IUpgrade
import org.bukkit.inventory.ItemStack

open class SpeedUpgrade(override var id: Int,
                   override var name: String,
                   override var slotPos: SlotPos,
                   override var itemStack: ItemStack,
                   override var levels: ArrayList<SpeedLevel>,
                   override var currentLevel: Int = levels.first().level) : IUpgrade<SpeedLevel> {

}