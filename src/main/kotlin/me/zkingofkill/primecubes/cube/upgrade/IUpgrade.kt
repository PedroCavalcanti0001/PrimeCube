package me.zkingofkill.primecubes.cube.upgrade

import fr.minuskube.inv.content.SlotPos
import me.zkingofkill.primecubes.Main
import me.zkingofkill.primecubes.cube.UpgradeType
import me.zkingofkill.primecubes.cube.upgrade.cyborgfortune.impl.CyborgFortuneLevel
import me.zkingofkill.primecubes.cube.upgrade.cyborgfortune.impl.CyborgFortuneUpgrade
import me.zkingofkill.primecubes.cube.upgrade.cyborgspeed.impl.CyborgSpeedLevel
import me.zkingofkill.primecubes.cube.upgrade.cyborgspeed.impl.CyborgSpeedUpgrade
import me.zkingofkill.primecubes.cube.upgrade.layers.impl.LayersLevel
import me.zkingofkill.primecubes.cube.upgrade.layers.impl.LayersUpgrade
import me.zkingofkill.primecubes.cube.upgrade.loot.impl.LootLevel
import me.zkingofkill.primecubes.cube.upgrade.loot.impl.LootUpgrade
import me.zkingofkill.primecubes.cube.upgrade.speed.impl.SpeedLevel
import me.zkingofkill.primecubes.cube.upgrade.speed.impl.SpeedUpgrade
import me.zkingofkill.primecubes.cube.upgrade.storage.impl.StorageLevel
import me.zkingofkill.primecubes.cube.upgrade.storage.impl.StorageUpgrade
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import utils.ItemStackBuilder

interface IUpgrade<ImplLevel> {
    var name: String
    var slotPos: SlotPos
    var itemStack: ItemStack
    var currentLevel: Int
    var levels: ArrayList<ImplLevel>
    var levelMax: Int
    var upgradeType: UpgradeType

    companion object {
        fun upgradeList(): ArrayList<IUpgrade<Any>> {
            val upgradesFile = Main.singleton.upgradesFile
            val list = arrayListOf<IUpgrade<Any>>()
            for (sec in upgradesFile.getConfigurationSection("").getKeys(false)) {
                val upgradeType = UpgradeType.valueOf(sec.toUpperCase())
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
                when (upgradeType) {
                    UpgradeType.SPEED -> {
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
                        val upgrade = SpeedUpgrade(upgradeName, slotPos, itemStack, levels, upgradeType = upgradeType) as IUpgrade<Any>
                        list.add(upgrade)
                    }
                    UpgradeType.STORAGE -> {
                        val levels = arrayListOf<StorageLevel>()
                        for (levelSec in upgradesFile.getConfigurationSection("$sec.levels").getKeys(false)) {
                            val levelRow = upgradesFile.getInt("$sec.levels.$levelSec.position.row")
                            val levelCollunm = upgradesFile.getInt("$sec.levels.$levelSec.position.collunm")
                            val levelSlotPos = SlotPos(levelRow, levelCollunm)
                            val price = upgradesFile.getDouble("$sec.levels.$levelSec.price")
                            val totalAmountPerDrop = upgradesFile.getInt("$sec.levels.$levelSec.totalAmountPerDrop")
                            val level = StorageLevel(levelSec.toInt(), price, levelSlotPos, totalAmountPerDrop)
                            levels.add(level)
                        }
                        val upgrade = StorageUpgrade(upgradeName, slotPos, itemStack, levels, upgradeType = upgradeType) as IUpgrade<Any>
                        list.add(upgrade)
                    }
                    UpgradeType.CYBORGSPEED -> {
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
                        val upgrade = CyborgSpeedUpgrade(upgradeName, slotPos, itemStack, levels, upgradeType = upgradeType) as IUpgrade<Any>
                        list.add(upgrade)
                    }
                    UpgradeType.CYBORGFORTUNE -> {
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
                        val upgrade = CyborgFortuneUpgrade(upgradeName, slotPos, itemStack, levels, upgradeType = upgradeType) as IUpgrade<Any>
                        list.add(upgrade)
                    }
                    UpgradeType.LAYERS -> {
                        val levels = arrayListOf<LayersLevel>()
                        for (levelSec in upgradesFile.getConfigurationSection("$sec.levels").getKeys(false)) {
                            val levelRow = upgradesFile.getInt("$sec.levels.$levelSec.position.row")
                            val levelCollunm = upgradesFile.getInt("$sec.levels.$levelSec.position.collunm")
                            val levelSlotPos = SlotPos(levelRow, levelCollunm)
                            val price = upgradesFile.getDouble("$sec.levels.$levelSec.price")
                            val sections = upgradesFile.getInt("$sec.levels.$levelSec.sections")
                            val level = LayersLevel(levelSec.toInt(), price, levelSlotPos, sections)
                            levels.add(level)
                        }
                        val upgrade = LayersUpgrade(upgradeName, slotPos, itemStack, levels, upgradeType = upgradeType) as IUpgrade<Any>
                        list.add(upgrade)
                    }
                    UpgradeType.LOOT -> {
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
                        val upgrade = LootUpgrade(upgradeName, slotPos, itemStack, levels, upgradeType = upgradeType) as IUpgrade<Any>
                        list.add(upgrade)
                    }
                }

            }
            return list
        }

        fun byType(upgradeType: UpgradeType): IUpgrade<Any>? {
            return this.upgradeList().find { it.upgradeType == upgradeType }
        }
    }
}