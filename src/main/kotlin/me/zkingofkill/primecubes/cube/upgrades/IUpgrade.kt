package me.zkingofkill.primecubes.cube.upgrades

import ItemStackBuilder
import fr.minuskube.inv.content.SlotPos
import me.zkingofkill.primecubes.Main
import me.zkingofkill.primecubes.cube.upgrades.cyborgfortune.impl.CyborgFortuneUpgrade
import me.zkingofkill.primecubes.cube.upgrades.cyborgfortune.impl.CyborgFortuneLevel
import me.zkingofkill.primecubes.cube.upgrades.cyborgspeed.impl.CyborgSpeedLevel
import me.zkingofkill.primecubes.cube.upgrades.cyborgspeed.impl.CyborgSpeedUpgrade
import me.zkingofkill.primecubes.cube.upgrades.layers.impl.LayersLevel
import me.zkingofkill.primecubes.cube.upgrades.layers.impl.LayersUpgrade
import me.zkingofkill.primecubes.cube.upgrades.loot.impl.LootLevel
import me.zkingofkill.primecubes.cube.upgrades.loot.impl.LootUpgrade
import me.zkingofkill.primecubes.cube.upgrades.speed.impl.SpeedLevel
import me.zkingofkill.primecubes.cube.upgrades.speed.impl.SpeedUpgrade
import me.zkingofkill.primecubes.cube.upgrades.storage.impl.StorageLevel
import me.zkingofkill.primecubes.cube.upgrades.storage.impl.StorageUpgrade
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

interface IUpgrade<ImplLevel> {
    var id: Int
    var name: String
    var slotPos: SlotPos
    var itemStack: ItemStack
    var currentLevel: Int
    var levels: ArrayList<ImplLevel>


    companion object {
        fun list(): ArrayList<IUpgrade<Any>> {
            val upgradesFile = Main.singleton.upgradesFile
            val list = arrayListOf<IUpgrade<Any>>()
            for (sec in upgradesFile.getConfigurationSection("").getKeys(false)) {
                val upgradeName = upgradesFile.getString("$sec.name").replace("&", "§")
                val upgradeItem = upgradesFile.getString("$sec.item")
                val upgradeLore = upgradesFile.getStringList("$sec.lore")
                        .map { it.replace("&", "§") }
                val upgradeArgs = upgradeItem.split(":")
                val upgradeId = if (upgradeArgs.size == 2) upgradeArgs[0].toInt() else upgradeItem.toInt()
                val upgradeDate = if (upgradeArgs.size == 2) upgradeArgs[1].toInt() else 0
                val itemStack = ItemStackBuilder(Material.getMaterial(upgradeId))
                        .setName(upgradeName).setLore(upgradeLore).setDurability(upgradeDate).build()
                val row = upgradesFile.getInt("$sec.position.row")
                val collunm = upgradesFile.getInt("$sec.position.collunm")
                val slotPos = SlotPos(row, collunm)
                when (sec) {
                    "0" -> {
                        val levels = arrayListOf<SpeedLevel>()
                        for (levelSec in upgradesFile.getConfigurationSection("$sec.levels").getKeys(false)) {
                            val levelRow = upgradesFile.getInt("$sec.levels.$levelSec.position.row")
                            val levelCollunm = upgradesFile.getInt("$sec.levels.$levelSec.position.collunm")
                            val levelSlotPos = SlotPos(levelRow, levelCollunm)
                            val price = upgradesFile.getDouble("$sec.levels.$levelSec.price")
                            val time = upgradesFile.getInt("$sec.levels.$levelSec.time")
                            val level = SpeedLevel(levelSec.toInt(), price, levelSlotPos, time)
                            levels.add(level)
                        }
                        val upgrade = SpeedUpgrade(sec.toInt(), upgradeName, slotPos, itemStack, levels) as IUpgrade<Any>
                        list.add(upgrade)
                    }
                    "1" -> {
                        val levels = arrayListOf<StorageLevel>()
                        for (levelSec in upgradesFile.getConfigurationSection("$sec.levels").getKeys(false)) {
                            val levelRow = upgradesFile.getInt("$sec.levels.$levelSec.position.row")
                            val levelCollunm = upgradesFile.getInt("$sec.levels.$levelSec.position.collunm")
                            val levelSlotPos = SlotPos(levelRow, levelCollunm)
                            val price = upgradesFile.getDouble("$sec.levels.$levelSec.price")
                            val pages = upgradesFile.getInt("$sec.levels.$levelSec.pages")
                            val level = StorageLevel(levelSec.toInt(), price, levelSlotPos, pages)
                            levels.add(level)
                        }
                        val upgrade = StorageUpgrade(sec.toInt(), upgradeName, slotPos, itemStack, levels) as IUpgrade<Any>
                        list.add(upgrade)
                    }
                    "2" -> {
                        val levels = arrayListOf<CyborgSpeedLevel>()
                        for (levelSec in upgradesFile.getConfigurationSection("$sec.levels").getKeys(false)) {
                            val levelRow = upgradesFile.getInt("$sec.levels.$levelSec.position.row")
                            val levelCollunm = upgradesFile.getInt("$sec.levels.$levelSec.position.collunm")
                            val levelSlotPos = SlotPos(levelRow, levelCollunm)
                            val price = upgradesFile.getDouble("$sec.levels.$levelSec.price")
                            val blocksPerSecounds = upgradesFile.getDouble("$sec.levels.$levelSec.blocksPerSecond")
                            val level = CyborgSpeedLevel(levelSec.toInt(), price, levelSlotPos, blocksPerSecounds)
                            levels.add(level)
                        }
                        val upgrade = CyborgSpeedUpgrade(sec.toInt(), upgradeName, slotPos, itemStack, levels) as IUpgrade<Any>
                        list.add(upgrade)
                    }
                    "3" -> {
                        val levels = arrayListOf<CyborgFortuneLevel>()
                        for (levelSec in upgradesFile.getConfigurationSection("$sec.levels").getKeys(false)) {
                            val levelRow = upgradesFile.getInt("$sec.levels.$levelSec.position.row")
                            val levelCollunm = upgradesFile.getInt("$sec.levels.$levelSec.position.collunm")
                            val levelSlotPos = SlotPos(levelRow, levelCollunm)
                            val price = upgradesFile.getDouble("$sec.levels.$levelSec.price")
                            val blocksPerSecounds = upgradesFile.getInt("$sec.levels.$levelSec.blocksPerSecond")
                            val level = CyborgFortuneLevel(levelSec.toInt(), price, levelSlotPos, blocksPerSecounds)
                            levels.add(level)
                        }
                        val upgrade = CyborgFortuneUpgrade(sec.toInt(), upgradeName, slotPos, itemStack, levels) as IUpgrade<Any>
                        list.add(upgrade)
                    }
                    "4" -> {
                        val levels = arrayListOf<LayersLevel>()
                        for (levelSec in upgradesFile.getConfigurationSection("$sec.levels").getKeys(false)) {
                            val levelRow = upgradesFile.getInt("$sec.levels.$levelSec.position.row")
                            val levelCollunm = upgradesFile.getInt("$sec.levels.$levelSec.position.collunm")
                            val levelSlotPos = SlotPos(levelRow, levelCollunm)
                            val price = upgradesFile.getDouble("$sec.levels.$levelSec.price")
                            val parts = upgradesFile.getInt("$sec.levels.$levelSec.parts")
                            val level = LayersLevel(levelSec.toInt(), price, levelSlotPos, parts)
                            levels.add(level)
                        }
                        val upgrade = LayersUpgrade(sec.toInt(), upgradeName, slotPos, itemStack, levels) as IUpgrade<Any>
                        list.add(upgrade)
                    }
                    "5" -> {
                        val levels = arrayListOf<LootLevel>()
                        for (levelSec in upgradesFile.getConfigurationSection("$sec.levels").getKeys(false)) {
                            val levelRow = upgradesFile.getInt("$sec.levels.$levelSec.position.row")
                            val levelCollunm = upgradesFile.getInt("$sec.levels.$levelSec.position.collunm")
                            val levelSlotPos = SlotPos(levelRow, levelCollunm)
                            val price = upgradesFile.getDouble("$sec.levels.$levelSec.price")
                            val increasePercentage = upgradesFile.getInt("$sec.levels.$levelSec.increasePercentage")
                            val level = LootLevel(levelSec.toInt(), price, levelSlotPos, increasePercentage)
                            levels.add(level)
                        }
                        val upgrade = LootUpgrade(sec.toInt(), upgradeName, slotPos, itemStack, levels) as IUpgrade<Any>
                        list.add(upgrade)
                    }
                }

            }
            return list
        }
    }
}