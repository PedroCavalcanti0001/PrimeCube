package me.zkingofkill.primecubes.util


import de.tr7zw.changeme.nbtapi.NBTItem
import org.bukkit.inventory.ItemStack


fun ItemStack.tag(key: String, value: String): ItemStack {
    val nbtItem = NBTItem(this)
    nbtItem.setString(key, value)

    return nbtItem.item
}


fun ItemStack.tag(key: String): String? {
    val nbtItem = NBTItem(this)

    return if (nbtItem.hasKey(key)) nbtItem.getString(key) else null
}