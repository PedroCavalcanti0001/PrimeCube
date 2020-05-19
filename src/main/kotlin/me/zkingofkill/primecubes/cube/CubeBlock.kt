package me.zkingofkill.primecubes.cube

import org.bukkit.inventory.ItemStack
/*
    Bloco da mina
 */
data
class CubeBlock(var id: Int,
                var itemStack: ItemStack,
                var chance: Double,
                var unitPrice: Double){

}