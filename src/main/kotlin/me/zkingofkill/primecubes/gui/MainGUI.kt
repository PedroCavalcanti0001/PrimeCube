package me.zkingofkill.primecubes.gui

import fr.minuskube.inv.ClickableItem
import fr.minuskube.inv.SmartInventory
import fr.minuskube.inv.content.InventoryContents
import fr.minuskube.inv.content.InventoryProvider
import fr.minuskube.inv.content.SlotIterator
import me.zkingofkill.primecubes.Main
import me.zkingofkill.primecubes.Main.Companion.singleton
import me.zkingofkill.primecubes.cube.Cube
import me.zkingofkill.primecubes.utils.format
import me.zkingofkill.primecubes.utils.freeSlots
import org.bukkit.Material
import org.bukkit.entity.Player
import utils.CustomHead
import utils.ItemStackBuilder
import kotlin.math.roundToLong

class MainGUI(var player: Player, var cube: Cube) : InventoryProvider {

    private val cf = singleton.config
    private val title = cf.getString("guis.cube.title")
            .replace("&", "§")
            .replace("{size}", cube.cubeSize.toString())
    private val rows = 6
    private var page = 1
    private var inventory: SmartInventory = SmartInventory.builder()
            .size(rows, 9)
            .title(title)
            .manager(singleton.inventoryManager)
            .provider(this)
            .build()

    override fun init(player: Player, contents: InventoryContents) {
        val allPrice =  cube.allPrice(player)
        val pagination = contents.pagination()
        var itemsSize = 0
        cube.storage.forEach {
            val completePacks = it.amount / 64
            val incompletePack = it.amount % 64
            itemsSize += completePacks
            if (incompletePack != 0) {
                itemsSize += 1
            }
        }
        val items = arrayOfNulls<ClickableItem>(itemsSize)
        var index = 0
        cube.storage.forEach { cubeDrop ->
            val cubeBlock = cube.cubeBlockById(cubeDrop.id)
            val completePacks = cubeDrop.amount / 64
            val incompletePack = cubeDrop.amount % 64
            for (cp in (1..completePacks)) {
                val its = ItemStackBuilder(cubeBlock.itemStack.clone()).setAmount(64).build()
                items[index] = ClickableItem.of(its) {
                    if (player.inventory.freeSlots(its) >= 64) {
                        cubeDrop.amount -= 64
                        player.inventory.addItem(its)
                        open()
                        player.sendMessage(Main.singleton.messagesFile.getString("dropRemoved")
                                .replace("&", "§"))
                    } else {
                        player.sendMessage(Main.singleton.messagesFile.getString("noInventorySpace")
                                .replace("&", "§"))
                    }
                }
                index += 1
            }
            if (incompletePack != 0) {
                val its = ItemStackBuilder(cubeBlock.itemStack.clone()).setAmount(incompletePack).build()
                items[index] = ClickableItem.of(its) {
                    if (player.inventory.freeSlots(its) >= incompletePack) {
                        cubeDrop.amount -= incompletePack
                        player.inventory.addItem(its)
                        open()
                        player.sendMessage(Main.singleton.messagesFile.getString("dropRemoved")
                                .replace("&", "§"))
                    } else {
                        player.sendMessage(Main.singleton.messagesFile.getString("noInventorySpace")
                                .replace("&", "§"))
                    }
                }
                index += 1
            }

        }
        pagination.setItems(*items)
        pagination.setItemsPerPage(15)
        val iterator = contents.newIterator(SlotIterator.Type.HORIZONTAL, 1, 2)
        for (row in 1..3) {
            for (collunm in 0..1) {
                iterator.blacklist(row, collunm)
            }
            for (collunm in 7..8) {
                iterator.blacklist(row, collunm)
            }
        }

        pagination.addToIterator(iterator)


        cf.getConfigurationSection("guis.cube.items").getKeys(false).forEach { sec ->
            val row = cf.getInt("guis.cube.items.$sec.position.row")
            val collunm = cf.getInt("guis.cube.items.$sec.position.collunm")
            val itemName = cf.getString("guis.cube.items.$sec.name")
                    .replace("&", "§")
            var itemLore = cf.getStringList("guis.cube.items.$sec.lore")
                    .map { it.replace("&", "§") }
                    .map { it.replace("{price}",allPrice.format()) }

            val item = cf.getString("guis.cube.items.$sec.item").toUpperCase()
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
            if (sec != "glassPane") {
                contents.set(row, collunm, ClickableItem.of(itb.build()) {
                    when (sec) {
                        "previousPage" -> {
                            page -= 1
                            inventory.open(player, pagination.previous().page)
                        }
                        "nextPage" -> {
                            page += 1
                            inventory.open(player, pagination.next().page)
                        }

                        "sellAll" -> {
                            val allPrice = cube.allPrice(player)

                            if (allPrice > 0.0) {
                                singleton.economy.depositPlayer(player, allPrice)
                                player.sendMessage(Main.singleton.messagesFile.getString("dropsSold")
                                        .replace("{total}", allPrice.format())
                                        .replace("&", "§"))
                                cube.storage = arrayListOf()
                                open()
                            } else {
                                player.sendMessage(Main.singleton.messagesFile.getString("noDrops")
                                        .replace("&", "§"))
                            }
                        }

                        "remove" -> {
                            val timeToSuccessfullyRemove = cube.timeToSuccessfullyRemove()
                            if (timeToSuccessfullyRemove <= 0.0) {
                                cube.manager.remove(player)
                                close()
                                player.sendMessage(singleton.messagesFile.getString("cubeRemoved")
                                        .replace("&", "§"))
                            } else {
                                player.sendMessage(Main.singleton.messagesFile.getString("cantBreak")
                                        .replace("&", "§")
                                        .replace("{seconds}", timeToSuccessfullyRemove.roundToLong().toString()))
                            }
                        }
                        "upgrades" -> {
                            UpgradeGUI(player, cube).open()
                        }
                    }
                })
            } else {
                for (fRow in 0..5) {
                    for (fCollunm in 0..8) {
                        if (((fRow in 1..3) && (fCollunm <= 1 || fCollunm >= 7) && !contents[fRow, fCollunm].isPresent) ||
                                fRow !in 1..3 && !contents[fRow, fCollunm].isPresent) {
                            contents.set(fRow, fCollunm, ClickableItem.empty(itb.build()))
                        }
                    }
                }
            }
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