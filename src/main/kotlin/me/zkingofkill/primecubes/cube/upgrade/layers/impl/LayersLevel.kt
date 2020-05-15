package me.zkingofkill.primecubes.cube.upgrade.layers.impl

import fr.minuskube.inv.content.SlotPos
import me.zkingofkill.primecubes.cube.upgrade.layers.ILayersLevel

class LayersLevel(override var level: Int,
                  override var price: Double,
                  override var slotPos: SlotPos,
                  override var sections: Int) : ILayersLevel {
}