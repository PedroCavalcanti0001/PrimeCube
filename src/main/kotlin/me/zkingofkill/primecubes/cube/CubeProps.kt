package me.zkingofkill.primecubes.cube

import ItemStackBuilder
import me.zkingofkill.primecubes.Main
import me.zkingofkill.primecubes.cube.upgrades.IUpgrade
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class CubeProps(
        var id: Int,
        var cubeSize: CubeSize,
        var itemStack: ItemStack,
        var blocks: ArrayList<CubeBlock>,
        var maxLife: Double,
        var upgrades: ArrayList<IUpgrade<Any>>) {

    companion object {
        fun list(): ArrayList<CubeProps> {
            val cubesFile = Main.singleton.cubesFile

            val list = arrayListOf<CubeProps>()
            for (id in cubesFile.getConfigurationSection("").getKeys(false)) {
                val name = cubesFile.getString("$id.name").replace("&", "§")
                val x = cubesFile.getString("$id.size").split("-")[0].toInt()
                val y = cubesFile.getString("$id.size").split("-")[1].toInt()
                val z = cubesFile.getString("$id.size").split("-")[2].toInt()
                val cubeSize = CubeSize(x, y, z)
                val maxLife = cubesFile.getDouble("$id.maxLife")
                val item = cubesFile.getString("$id.item")
                val lore = cubesFile.getStringList("$id.item")
                        .map { it.replace("&", "§") }
                val args = item.split(":")
                val itemId = if (args.size == 2) args[0].toInt() else item.toInt()
                val itemDate = if (args.size == 2) args[1].toInt() else 0
                val itemStack = ItemStackBuilder(Material.getMaterial(itemId))
                        .setName(name)
                        .setLore(lore)
                        .setDurability(itemDate).build()
                val blocks = arrayListOf<CubeBlock>()
                for (sec in cubesFile.getConfigurationSection("").getKeys(false)) {
                    val blockItem = cubesFile.getString("$id.blocks.$sec.item")
                    val blockChance = cubesFile.getDouble("$id.blocks.$sec.chance")
                    val itemArgs = blockItem.split(":")
                    val blockItemId = if (itemArgs.size == 2) itemArgs[0].toInt() else blockItem.toInt()
                    val blockItemDate = if (itemArgs.size == 2) itemArgs[1].toInt() else 0
                    val blockItemStack = ItemStackBuilder(Material.getMaterial(blockItemId))
                            .setName(blockItem)
                            .setDurability(blockItemDate).build()
                    val cubeBlock = CubeBlock(sec.toInt(), blockItemStack, blockChance)
                    blocks.add(cubeBlock)
                }
            }
            return list
        }
    }
}