package me.zkingofkill.primecubes.utils

import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

fun Inventory.freeSlots(itemStack: ItemStack): Int {
    var slots = 0
    val contents = this.contents

    for (item in contents) {
        slots += if (item != null && (item.hasItemMeta() && itemStack.hasItemMeta() && item.itemMeta == itemStack.itemMeta || !item.hasItemMeta() && !itemStack.hasItemMeta())
            && item.typeId == itemStack.typeId && itemStack.durability == item.durability
        ) {
            item.maxStackSize - item.amount
        } else {
            if (item == null) {
                itemStack.maxStackSize
            } else {
                0
            }
        }
    }

    return slots
}

fun Inventory.itemsAmount(vararg search: ItemStack): Int {
    val itens = listOf(*search)
    var slots = 0
    val contents = this.contents
    for (item in contents) {
        if (item != null) {
            slots += itens.stream()
                .filter { itemStack -> (item.hasItemMeta() && itemStack.hasItemMeta() && item.itemMeta == itemStack.itemMeta || !item.hasItemMeta() && !itemStack.hasItemMeta()) && item.typeId == itemStack.typeId && itemStack.durability == item.durability }
                .mapToInt { item.amount }.sum()
        }
    }
    return slots
}

fun Player.amountInHand(vararg search: ItemStack): Int {
    val itens = listOf(*search)
    var slots = 0
    var item = this.itemInHand
    if (item != null) {
        slots += itens.stream()
            .filter { itemStack -> (item.hasItemMeta() && itemStack.hasItemMeta() && item.itemMeta == itemStack.itemMeta || !item.hasItemMeta() && !itemStack.hasItemMeta()) && item.typeId == itemStack.typeId && itemStack.durability == item.durability }
            .mapToInt { item.amount }.sum()
    }
    return slots
}

fun Inventory.removeItems(item: ItemStack, amount: Int) {
    val q = intArrayOf(0)
    for (itemStack in this) {
        if (itemStack != null) {
            if ((item.hasItemMeta() && itemStack.hasItemMeta() && item.itemMeta == itemStack.itemMeta || !item.hasItemMeta() && !itemStack.hasItemMeta()) && item.typeId == itemStack.typeId && itemStack.durability == item.durability) {
                if (q[0] == amount) {
                    break
                }
                if (itemStack.amount <= amount) {
                    q[0] += itemStack.amount
                    this.removeItem(itemStack)
                } else {
                    itemStack.amount = itemStack.amount - (amount - q[0])
                    q[0] += amount - q[0]
                    return
                }

            }

        }
    }
}

fun Inventory.removeAll(itemStack: ItemStack) {
    this.forEach {
        if (it != null) {
            if ((it.hasItemMeta() && itemStack.hasItemMeta() && it.itemMeta == itemStack.itemMeta || !it.hasItemMeta() && !itemStack.hasItemMeta()) && it.typeId == itemStack.typeId && itemStack.durability == it.durability) {
                this.remove(it)
            }
        }
    }
}

fun Inventory.removeAll(itemStack: ItemStack, amount: Int) {
    var q = 0
    this.forEach {
        if (it != null) {
            if ((it.hasItemMeta() && itemStack.hasItemMeta() && it.itemMeta == itemStack.itemMeta || !it.hasItemMeta() && !itemStack.hasItemMeta()) && it.typeId == itemStack.typeId && itemStack.durability == it.durability) {
                if (it.amount + q < amount) {
                    q += it.amount
                    this.remove(it)
                } else {
                    it.amount -= amount - q
                    return@forEach
                }
            }
        }
    }
}
