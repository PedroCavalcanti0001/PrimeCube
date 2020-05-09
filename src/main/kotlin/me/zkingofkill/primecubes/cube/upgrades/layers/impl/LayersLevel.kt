package me.zkingofkill.primecubes.cube.upgrades.layers.impl

import fr.minuskube.inv.content.SlotPos
import me.zkingofkill.primecubes.cube.upgrades.layers.ILayersLevel

class LayersLevel(override var level: Int,
                  override var price: Double,
                  override var slotPos: SlotPos,
                  override var parts: Int) : ILayersLevel {
}