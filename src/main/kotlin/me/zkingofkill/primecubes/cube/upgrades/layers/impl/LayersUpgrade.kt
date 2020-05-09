package me.zkingofkill.primecubes.cube.upgrades.layers.impl

import fr.minuskube.inv.content.SlotPos
import me.zkingofkill.primecubes.cube.upgrades.IUpgrade
import me.zkingofkill.primecubes.cube.upgrades.layers.ILayersLevel
import org.bukkit.inventory.ItemStack

class LayersUpgrade(override var id: Int,
                    override var name: String,
                    override var slotPos: SlotPos,
                    override var itemStack: ItemStack,
                    override var levels: ArrayList<LayersLevel>,
                    override var currentLevel: Int = levels.first().level) : IUpgrade<LayersLevel> {

}