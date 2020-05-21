package me.zkingofkill.primecubes.cube

import me.zkingofkill.primecubes.Main
import me.zkingofkill.primecubes.cube.upgrade.IUpgrade
import me.zkingofkill.primecubes.exception.CubePropsNotFoundException
import me.zkingofkill.primecubes.exception.UpgradeNotFoundException
import me.zkingofkill.primecubes.util.tag
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import utils.CustomHead
import utils.ItemStackBuilder

data class CubeProps(
        var typeId: Int,
        var cubeSize: CubeSize,
        internal var itemStack: ItemStack,
        var blocks: ArrayList<CubeBlock>,
        var maxLife: Double,
        var upgrades: ArrayList<UpgradeType>,
        var defaultSections: Int,
        var nextCube: Int?,
        var defaultStorage: Int,
        var defaultSpeed: Int,
        var activatedCyborg: Boolean) {


    fun itemStack(): ItemStack {
        var itemStack = itemStack.clone()
        val lore = if (itemStack.hasItemMeta() && itemStack.itemMeta.hasLore()) itemStack.itemMeta.lore else arrayListOf()


        val line = lore.find { it.contains("{upgradeLevel}") || it.contains("{UpgradeName}") }
        if (line != null) {
            var index = lore.indexOf(line)
            lore.remove(line)
            upgrades.forEach {
                val upgrade = IUpgrade.byType(it) ?: throw UpgradeNotFoundException(it.name)
                itemStack = itemStack.tag("{primecubes_level_$it}", 0.toString())
                lore.add(index, line.replace("{UpgradeName}", upgrade.name)
                        .replace("{upgradeLevel}", "0")
                        .replace("&", "§"))
                index++
            }
        }
        val itemStackBuilder = ItemStackBuilder(itemStack)
        itemStackBuilder.setLore(lore)

        return itemStackBuilder.build().tag("{primecubes_typeId}", typeId.toString())
    }


    companion object {
        fun list(): ArrayList<CubeProps> {
            val cubesFile = Main.singleton.cubesFile

            val list = arrayListOf<CubeProps>()
            for (id in cubesFile.getConfigurationSection("").getKeys(false)) {
                val name = cubesFile.getString("$id.name").replace("&", "§")
                val activatedCyborg = cubesFile.getBoolean("$id.activatedCyborg")
                val xz = cubesFile.getString("$id.size").split("-")[0].toDouble()
                val y = cubesFile.getString("$id.size").split("-")[1].toDouble()
                val cubeSize = CubeSize(xz, y)
                val maxLife = cubesFile.getDouble("$id.maxLife")
                val defaultSections = cubesFile.getInt("$id.defaultSections")
                val defaultStorage = cubesFile.getInt("$id.defaultStorage")
                val nextCube = if (cubesFile.getInt("$id.nextCube") == -1) null else cubesFile.getInt("$id.nextCube")
                val defaultSpeed = cubesFile.getInt("$id.defaultSpeed")
                val item = cubesFile.getString("$id.item").toUpperCase()
                val lore = cubesFile.getStringList("$id.lore")
                        .map { it.replace("&", "§") }
                        .map { it.replace("{size}", "${xz.toInt()}-${y.toInt()}") }
                val args = item.split(":")
                val itemId = if (args.size == 2) args[0] else item
                val itemDate = if (args.size == 2) args[1].toInt() else 0
                val itemStack = if (Material.getMaterial(itemId) != null) {
                    ItemStackBuilder(Material.getMaterial(itemId))
                            .setName(name)
                            .setLore(lore)
                            .setDurability(itemDate).build()
                } else {
                    ItemStackBuilder(CustomHead.itemFromUrl("http://textures.minecraft.net/texture/$itemId"))
                            .setName(name)
                            .setLore(lore).build()
                }
                val blocks = arrayListOf<CubeBlock>()
                val upgrades = cubesFile.getStringList("$id.upgrades").map { UpgradeType.valueOf(it.toUpperCase()) }
                        as ArrayList<UpgradeType>
                for (sec in cubesFile.getConfigurationSection("$id.blocks").getKeys(false)) {
                    val blockItem = cubesFile.getString("$id.blocks.$sec.item")
                    val blockChance = cubesFile.getDouble("$id.blocks.$sec.chance")
                    val unitPrice = cubesFile.getDouble("$id.blocks.$sec.unitPrice")
                    val itemArgs = blockItem.split(":")
                    val blockItemId = if (itemArgs.size == 2) itemArgs[0] else blockItem
                    val blockItemDate = if (itemArgs.size == 2) itemArgs[1].toInt() else 0
                    val blockItemStack = ItemStackBuilder(Material.getMaterial(blockItemId.toUpperCase()))
                            .setDurability(blockItemDate).build()
                    val cubeBlock = CubeBlock(sec.toInt(),
                            itemStack = blockItemStack,
                            chance = blockChance,
                            unitPrice = unitPrice)
                    blocks.add(cubeBlock)
                }
                val cube = CubeProps(typeId = id.toInt(),
                        cubeSize = cubeSize,
                        blocks = blocks,
                        itemStack = itemStack,
                        maxLife = maxLife,
                        upgrades = upgrades,
                        defaultSections = defaultSections,
                        defaultStorage = defaultStorage,
                        activatedCyborg = activatedCyborg,
                        nextCube = nextCube,
                        defaultSpeed = defaultSpeed)
                list.add(cube)
            }
            return list
        }

        fun byTypeId(typeId: Int, nonNull: Boolean = false): CubeProps? {
            val find = list().find {
                it.typeId == typeId
            }
            return if (nonNull && find == null) {
                throw CubePropsNotFoundException(typeId)
            } else find
        }
    }
}