package me.zkingofkill.primecubes.gui

import fr.minuskube.inv.ClickableItem
import fr.minuskube.inv.SmartInventory
import fr.minuskube.inv.content.InventoryContents
import fr.minuskube.inv.content.InventoryProvider
import me.zkingofkill.primecubes.Main
import me.zkingofkill.primecubes.Main.Companion.singleton
import me.zkingofkill.primecubes.cube.Cube
import me.zkingofkill.primecubes.cube.UpgradeType
import me.zkingofkill.primecubes.cube.upgrade.cyborgfortune.impl.CyborgFortuneLevel
import me.zkingofkill.primecubes.cube.upgrade.cyborgspeed.impl.CyborgSpeedLevel
import me.zkingofkill.primecubes.cube.upgrade.layers.impl.LayersLevel
import me.zkingofkill.primecubes.cube.upgrade.loot.impl.LootLevel
import me.zkingofkill.primecubes.cube.upgrade.speed.impl.SpeedLevel
import me.zkingofkill.primecubes.cube.upgrade.storage.impl.StorageLevel
import me.zkingofkill.primecubes.utils.format
import org.bukkit.Material
import org.bukkit.entity.Player
import utils.CustomHead
import utils.ItemStackBuilder

class UpgradeGUI(var player: Player, var cube: Cube) : InventoryProvider {

    private val cf = singleton.config
    private val title = cf.getString("guis.upgrade.title")
            .replace("&", "§")
            .replace("{size}", cube.cubeSize.toString())
    private val rows = 4
    private var inventory: SmartInventory = SmartInventory.builder()
            .size(rows, 9)
            .title(title)
            .manager(singleton.inventoryManager)
            .provider(this)
            .build()

    override fun init(player: Player, contents: InventoryContents) {

        cf.getConfigurationSection("guis.upgrade.items").getKeys(false).forEach { sec ->

            val upgrade = if (sec != "previousGUI") UpgradeType.valueOf(sec.toUpperCase()) else null
            val row = cf.getInt("guis.upgrade.items.$sec.position.row")
            val collunm = cf.getInt("guis.upgrade.items.$sec.position.collunm")
            val itemName = cf.getString("guis.upgrade.items.$sec.name")
                    .replace("&", "§")
            var itemLore = if (upgrade == null || cube.props.upgrades.contains(upgrade)) {
                cf.getStringList("guis.upgrade.items.$sec.lore")
                        .map { it.replace("&", "§") }
            } else cf.getStringList("guis.upgrade.items.$sec.loreDoesNotApply")
                    .map { it.replace("&", "§") }

            if (upgrade != null) {
                val level = cube.level(upgrade)
                var iupgrade = cube.iUpgrade(upgrade)
                var nextUpgrade = iupgrade.nextLevel(level)
                var nextLevel = nextUpgrade?.level?.toString() ?: "max"
                var price = nextUpgrade?.price?.format() ?: "0"

                if (nextUpgrade is LayersLevel) {
                    val maxLayers = cube.cubeSize.xz * cube.cubeSize.xz
                    if (nextUpgrade.sections > maxLayers) {
                        nextUpgrade = null
                        price = "0"
                        nextLevel = "max"
                    }
                }
                itemLore = itemLore
                        .map { it.replace("{currentLevel}", level.toString()) }
                        .map { it.replace("{nextLevel}", nextLevel) }
                        .map { it.replace("{priceToUpgrade}", price) }

                when (sec) {
                    "speed" -> {
                        itemLore = if (level == 0) {
                            itemLore
                                    .map { it.replace("{current}", cube.props.defaultSpeed.toString()) }
                        } else {
                            val speedUpgrade = cube.iUpgradeLevelByLevel(upgrade, level) as SpeedLevel
                            itemLore.map { it.replace("{current}", speedUpgrade.timeToRegenerate.toString()) }
                        }

                        itemLore = if (nextUpgrade != null) {
                            itemLore.map { it.replace("{next}", (nextUpgrade as SpeedLevel).timeToRegenerate.toString()) }
                        } else {
                            itemLore.map { it.replace("{next}", "max") }
                        }
                    }
                    "layers" -> {
                        itemLore = if (level == 0) {
                            itemLore
                                    .map { it.replace("{current}", cube.props.defaultSections.toString()) }
                        } else {
                            val layersUpgrade = cube.iUpgradeLevelByLevel(upgrade, level) as LayersLevel
                            itemLore.map { it.replace("{current}", layersUpgrade.sections.toString()) }
                        }

                        itemLore = if (nextUpgrade != null) {
                            itemLore.map { it.replace("{next}", (nextUpgrade as LayersLevel).sections.toString()) }
                        } else {
                            itemLore.map { it.replace("{next}", "max") }
                        }
                    }
                    "loot" -> {
                        itemLore = if (level == 0) {
                            itemLore
                                    .map { it.replace("{current}", "0") }
                        } else {
                            val lootUpgrade = cube.iUpgradeLevelByLevel(upgrade, level) as LootLevel
                            itemLore.map { it.replace("{current}", "${lootUpgrade.increasePercentage}%") }
                        }

                        itemLore = if (nextUpgrade != null) {
                            itemLore.map { it.replace("{next}", "${(nextUpgrade as LootLevel).increasePercentage}%") }
                        } else {
                            itemLore.map { it.replace("{next}", "max") }
                        }
                    }
                    "storage" -> {
                        itemLore = if (level == 0) {
                            itemLore.map { it.replace("{current}", cube.props.defaultStorage.toString()) }
                        } else {
                            val storageUpgrade = cube.iUpgradeLevelByLevel(upgrade, level) as StorageLevel
                            itemLore.map { it.replace("{current}", storageUpgrade.totalAmountPerDrop.toString()) }
                        }
                        itemLore = if (nextUpgrade != null) {
                            itemLore.map { it.replace("{next}", (nextUpgrade as StorageLevel).totalAmountPerDrop.toString()) }
                        } else {
                            itemLore.map { it.replace("{next}", "max") }
                        }
                    }
                    "cyborgSpeed" -> {
                        itemLore = if (level == 0) {
                            itemLore.map { it.replace("{current}", "0") }
                        } else {
                            val cyborgSpeedLevel = cube.iUpgradeLevelByLevel(upgrade, level) as CyborgSpeedLevel
                            itemLore.map { it.replace("{current}", cyborgSpeedLevel.time.toString()) }
                        }
                        itemLore = if (nextUpgrade != null) {
                            itemLore.map { it.replace("{next}", (nextUpgrade as CyborgSpeedLevel).time.toString()) }
                        } else {
                            itemLore.map { it.replace("{next}", "max") }
                        }
                    }
                    "cyborgFortune" -> {
                        itemLore = if (level == 0) {
                            itemLore.map { it.replace("{current}", "0") }
                        } else {
                            val cyborgFortuneLevel
                                    = cube.iUpgradeLevelByLevel(upgrade, level) as CyborgFortuneLevel
                            itemLore.map { it.replace("{current}", cyborgFortuneLevel.multiply.toString()) }
                        }
                        itemLore = if (nextUpgrade != null) {
                            itemLore.map { it.replace("{next}", (nextUpgrade as CyborgFortuneLevel).multiply.toString()) }
                        } else {
                            itemLore.map { it.replace("{next}", "max") }
                        }
                    }
                }
            }
            val item = cf.getString("guis.upgrade.items.$sec.item").toUpperCase()
            val itemArgs = item.split(":")
            var itemId: String
            var itemDur = 0
            if (itemArgs.size == 2) {
                itemId = itemArgs[0]
                itemDur = itemArgs[1].toInt()
            } else {
                itemId = item
            }
            val itb = if (Material.getMaterial(itemId) != null) ItemStackBuilder(Material.getMaterial(itemId))
                    .setDurability(itemDur)
                    .setName(itemName)
                    .setLore(itemLore)
            else {
                ItemStackBuilder(CustomHead.itemFromUrl("http://textures.minecraft.net/texture/$itemId"))
                        .setName(itemName)
                        .setLore(itemLore)
            }

            contents.set(row, collunm, ClickableItem.of(itb.build()) {
                if (upgrade == null) {
                    MainGUI(player, cube).open()
                } else {
                    if (!cube.props.upgrades.contains(upgrade)) return@of
                    val level = cube.level(upgrade)
                    var iupgrade = cube.iUpgrade(upgrade)
                    var nextUpgrade = iupgrade.nextLevel(level)
                    if (nextUpgrade is LayersLevel) {
                        val maxLayers = cube.cubeSize.xz * cube.cubeSize.xz
                        if ((nextUpgrade as LayersLevel).sections > maxLayers) {
                            nextUpgrade = null
                        }
                    }
                    if (nextUpgrade != null) {
                        if (singleton.economy.getBalance(player) >= nextUpgrade!!.price) {
                            cube.addLevel(upgrade)
                            singleton.economy.withdrawPlayer(player, nextUpgrade!!.price)
                            player.sendMessage(Main.singleton.messagesFile.getString("successfullyUpgrade")
                                    .replace("&", "§"))

                            if(cube.isAtMaximum() && cube.props.nextCube != null){
                                player.sendMessage(singleton.messagesFile.getString("cubeWithAllUpgrades")
                                        .replace("&", "§"))
                            }
                            open()
                        } else {
                            player.sendMessage(Main.singleton.messagesFile.getString("withoutEnoughMoney")
                                    .replace("&", "§"))
                        }
                    } else {
                        player.sendMessage(Main.singleton.messagesFile.getString("maximumLevel")
                                .replace("&", "§"))
                    }
                }
            })
        }
    }


    override fun update(player: Player, contents: InventoryContents) {

    }

    fun open() {
        inventory.open(player)
    }

    fun close() {
        inventory.close(player)
    }
}