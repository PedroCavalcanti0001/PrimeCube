package me.zkingofkill.primecubes.cube

import org.bukkit.inventory.ItemStack
/*
    Bloco da mina
 */
class CubeBlock(var id: Int,
                var itemStack: ItemStack,
                var chance: Double,
                var lastBreak: Long = 0)