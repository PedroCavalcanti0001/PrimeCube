package me.zkingofkill.primecubes.utils


import net.minecraft.server.v1_12_R1.NBTTagCompound
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack

import org.bukkit.inventory.ItemStack


fun ItemStack.tag(key: String, value: String): CraftItemStack {
    val stack = CraftItemStack.asNMSCopy(this)
    val tag = if (stack.tag != null) stack.tag else NBTTagCompound()
    tag!!.setString(key, value)
    stack.tag = tag
    return CraftItemStack.asCraftMirror(stack)
}


fun ItemStack.tag(key: String): String? {
    val stack = CraftItemStack.asNMSCopy(this)
    return if (stack != null && stack.tag != null && stack.tag!!.hasKey(key)) {
        stack.tag!!.getString(key)
    } else null
}