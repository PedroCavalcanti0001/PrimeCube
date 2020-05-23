package me.zkingofkill.primecubes.cube

import fr.minuskube.inv.content.SlotPos
import me.zkingofkill.primecubes.Main
import me.zkingofkill.primecubes.cube.upgrade.IUpgrade
import me.zkingofkill.primecubes.cube.upgrade.cyborgfortune.impl.CyborgFortuneLevel
import me.zkingofkill.primecubes.cube.upgrade.cyborgfortune.impl.CyborgFortuneUpgrade
import me.zkingofkill.primecubes.cube.upgrade.cyborglayers.impl.LayersLevel
import me.zkingofkill.primecubes.cube.upgrade.cyborglayers.impl.LayersUpgrade
import me.zkingofkill.primecubes.cube.upgrade.cyborgspeed.impl.CyborgSpeedLevel
import me.zkingofkill.primecubes.cube.upgrade.cyborgspeed.impl.CyborgSpeedUpgrade
import me.zkingofkill.primecubes.cube.upgrade.loot.impl.LootLevel
import me.zkingofkill.primecubes.cube.upgrade.loot.impl.LootUpgrade
import me.zkingofkill.primecubes.cube.upgrade.speed.impl.SpeedLevel
import me.zkingofkill.primecubes.cube.upgrade.speed.impl.SpeedUpgrade
import me.zkingofkill.primecubes.cube.upgrade.storage.impl.StorageLevel
import me.zkingofkill.primecubes.cube.upgrade.storage.impl.StorageUpgrade
import me.zkingofkill.primecubes.exception.CubePropsNotFoundException
import me.zkingofkill.primecubes.util.tag
import org.bukkit.Material
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack
import utils.CustomHead
import utils.ItemStackBuilder
import java.io.File

data class CubeProps(
        var typeId: Int,
        var cubeSize: CubeSize,
        internal var itemStack: ItemStack,
        var blocks: ArrayList<CubeBlock>,
        var maxLife: Double,
        var upgrades: ArrayList<IUpgrade<Any>>,
        var defaultSections: Int,
        var nextCube: Int?,
        var defaultStorage: Int,
        var defaultSpeed: Int,
        var timeToRemove: Int,
        var activatedCyborg: Boolean,
        var folderName: String) {

    fun containsUpgrade(upgradeType: UpgradeType): Boolean {
        return this.upgrades.find { it.upgradeType == upgradeType } != null
    }

    fun itemStack(): ItemStack {
        var itemStack = itemStack.clone()
        val lore = if (itemStack.hasItemMeta() && itemStack.itemMeta.hasLore()) itemStack.itemMeta.lore else arrayListOf()


        val line = lore.find { it.contains("{upgradeLevel}") || it.contains("{UpgradeName}") }
        if (line != null) {
            var index = lore.indexOf(line)
            lore.remove(line)
            upgrades.forEach {
                itemStack = itemStack.tag("{primecubes_level_$it}", 0.toString())
                lore.add(index, line.replace("{UpgradeName}", it.name)
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
        val list = arrayListOf<CubeProps>()

        fun init() {
            list.clear()
            val cubesFolder = File(Main.singleton.dataFolder, "cubes")
            for (cubeFolder in cubesFolder.listFiles().filter { it.isDirectory }) {
                val blocks = arrayListOf<CubeBlock>()
                val upgrades = arrayListOf<IUpgrade<Any>>()
                for (file in cubeFolder.listFiles()) {
                    val configFile = YamlConfiguration.loadConfiguration(file)
                    when (file.nameWithoutExtension) {
                        "blocks" -> {
                            for (sec in configFile.getConfigurationSection("").getKeys(false)) {
                                val blockItem = configFile.getString("$sec.item")
                                val blockChance = configFile.getDouble("$sec.chance")
                                val unitPrice = configFile.getDouble("$sec.unitPrice")
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
                        }
                        "upgrades" -> {
                            for (sec in configFile.getConfigurationSection("").getKeys(false)) {
                                val upgradeType = UpgradeType.valueOf(sec.toUpperCase())
                                val upgradeName = configFile.getString("$sec.name").replace("&", "§")
                                val upgradeItem = configFile.getString("$sec.item")
                                val upgradeLore = configFile.getStringList("$sec.lore")
                                        .map { it.replace("&", "§") }
                                val upgradeArgs = upgradeItem.split(":")
                                val upgradeId = if (upgradeArgs.size == 2) upgradeArgs[0].toInt() else upgradeItem.toInt()
                                val upgradeDate = if (upgradeArgs.size == 2) upgradeArgs[1].toInt() else 0
                                val itemStack = ItemStackBuilder(Material.getMaterial(upgradeId))
                                        .setName(upgradeName).setLore(upgradeLore).setDurability(upgradeDate).build()
                                val row = configFile.getInt("$sec.position.row")
                                val collunm = configFile.getInt("$sec.position.collunm")
                                val slotPos = SlotPos(row, collunm)
                                when (upgradeType) {
                                    UpgradeType.SPEED -> {
                                        val levels = arrayListOf<SpeedLevel>()
                                        for (levelSec in configFile.getConfigurationSection("$sec.levels").getKeys(false)) {
                                            val price = configFile.getDouble("$sec.levels.$levelSec.price")
                                            val time = configFile.getInt("$sec.levels.$levelSec.time")
                                            val level = SpeedLevel(levelSec.toInt(), price, time)
                                            levels.add(level)
                                        }
                                        val upgrade = SpeedUpgrade(upgradeName, slotPos, itemStack, levels, upgradeType = upgradeType) as IUpgrade<Any>
                                        upgrades.add(upgrade)
                                    }
                                    UpgradeType.STORAGE -> {
                                        val levels = arrayListOf<StorageLevel>()
                                        for (levelSec in configFile.getConfigurationSection("$sec.levels").getKeys(false)) {
                                            val price = configFile.getDouble("$sec.levels.$levelSec.price")
                                            val totalAmountPerDrop = configFile.getInt("$sec.levels.$levelSec.totalAmountPerDrop")
                                            val level = StorageLevel(levelSec.toInt(), price, totalAmountPerDrop)
                                            levels.add(level)
                                        }
                                        val upgrade = StorageUpgrade(upgradeName, slotPos, itemStack, levels, upgradeType = upgradeType) as IUpgrade<Any>
                                        upgrades.add(upgrade)
                                    }
                                    UpgradeType.CYBORGSPEED -> {
                                        val levels = arrayListOf<CyborgSpeedLevel>()
                                        for (levelSec in configFile.getConfigurationSection("$sec.levels").getKeys(false)) {
                                            val price = configFile.getDouble("$sec.levels.$levelSec.price")
                                            val time = configFile.getInt("$sec.levels.$levelSec.time")
                                            val level = CyborgSpeedLevel(levelSec.toInt(), price, time)
                                            levels.add(level)
                                        }
                                        val upgrade = CyborgSpeedUpgrade(upgradeName, slotPos, itemStack, levels, upgradeType = upgradeType) as IUpgrade<Any>
                                        upgrades.add(upgrade)
                                    }
                                    UpgradeType.CYBORGFORTUNE -> {
                                        val levels = arrayListOf<CyborgFortuneLevel>()
                                        for (levelSec in configFile.getConfigurationSection("$sec.levels").getKeys(false)) {
                                            val price = configFile.getDouble("$sec.levels.$levelSec.price")
                                            val multiply = configFile.getInt("$sec.levels.$levelSec.multiply")
                                            val chance = configFile.getDouble("$sec.levels.$levelSec.chance")
                                            val level = CyborgFortuneLevel(levelSec.toInt(), price, multiply, chance)
                                            levels.add(level)
                                        }
                                        val upgrade = CyborgFortuneUpgrade(upgradeName, slotPos, itemStack, levels, upgradeType = upgradeType) as IUpgrade<Any>
                                        upgrades.add(upgrade)
                                    }
                                    UpgradeType.CYBORGLAYERS -> {
                                        val levels = arrayListOf<LayersLevel>()
                                        for (levelSec in configFile.getConfigurationSection("$sec.levels").getKeys(false)) {
                                            val price = configFile.getDouble("$sec.levels.$levelSec.price")
                                            val sections = configFile.getInt("$sec.levels.$levelSec.sections")
                                            val level = LayersLevel(levelSec.toInt(), price, sections)
                                            levels.add(level)
                                        }
                                        val upgrade = LayersUpgrade(upgradeName, slotPos, itemStack, levels, upgradeType = upgradeType) as IUpgrade<Any>
                                        upgrades.add(upgrade)
                                    }
                                    UpgradeType.LOOT -> {
                                        val levels = arrayListOf<LootLevel>()
                                        for (levelSec in configFile.getConfigurationSection("$sec.levels").getKeys(false)) {
                                            val price = configFile.getDouble("$sec.levels.$levelSec.price")
                                            val increasePercentage = configFile.getInt("$sec.levels.$levelSec.increasePercentage")
                                            val level = LootLevel(levelSec.toInt(), price, increasePercentage)
                                            levels.add(level)
                                        }
                                        val upgrade = LootUpgrade(upgradeName, slotPos, itemStack, levels, upgradeType = upgradeType) as IUpgrade<Any>
                                        upgrades.add(upgrade)
                                    }
                                }

                            }
                        }
                        "props" -> {
                            val id = configFile.getInt("id")
                            val name = configFile.getString("name").replace("&", "§")
                            val activatedCyborg = configFile.getBoolean("activatedCyborg")
                            val xz = configFile.getString("size").split("-")[0].toDouble()
                            val y = configFile.getString("size").split("-")[1].toDouble()
                            val cubeSize = CubeSize(xz, y)
                            val maxLife = configFile.getDouble("maxLife")
                            val defaultSections = configFile.getInt("defaultSections")
                            val defaultStorage = configFile.getInt("defaultStorage")
                            val timeToRemove = configFile.getInt("timeToRemove")
                            val nextCube = if (configFile.getInt("nextCube") == -1) null else configFile.getInt("nextCube")
                            val defaultSpeed = configFile.getInt("defaultSpeed")
                            val item = configFile.getString("item").toUpperCase()
                            val lore = configFile.getStringList("lore")
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
                            val cube = CubeProps(typeId = id,
                                    cubeSize = cubeSize,
                                    blocks = blocks,
                                    itemStack = itemStack,
                                    maxLife = maxLife,
                                    upgrades = upgrades,
                                    defaultSections = defaultSections,
                                    defaultStorage = defaultStorage,
                                    timeToRemove = timeToRemove,
                                    activatedCyborg = activatedCyborg,
                                    nextCube = nextCube,
                                    defaultSpeed = defaultSpeed,
                                    folderName = cubeFolder.name)
                            list.add(cube)
                        }
                    }
                }
            }
            list.forEach {
                val activatedCyborg = it.activatedCyborg
                it.upgrades.removeIf {
                   !activatedCyborg &&
                            (it.upgradeType == UpgradeType.CYBORGLAYERS ||
                                    it.upgradeType == UpgradeType.CYBORGFORTUNE ||
                                    it.upgradeType == UpgradeType.CYBORGSPEED)
                }
            }
        }

        fun byTypeId(typeId: Int, nonNull: Boolean = false): CubeProps? {
            val find = list.find {
                it.typeId == typeId
            }
            return if (nonNull && find == null) {
                throw CubePropsNotFoundException(typeId)
            } else find
        }
    }
}