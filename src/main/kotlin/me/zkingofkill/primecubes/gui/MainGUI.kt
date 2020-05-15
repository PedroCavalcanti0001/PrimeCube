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
import org.bukkit.Material
import org.bukkit.entity.Player
import utils.ItemStackBuilder
import kotlin.math.roundToLong

class MainGUI(var player: Player, var cube: Cube) : InventoryProvider {

    private val cf = singleton.config
    private val title = cf.getString("guis.cube.title")
            .replace("&", "§")
            .replace("{size}", cube.cubeSize.toString())
    private val rows = 6
    private var inventory: SmartInventory = SmartInventory.builder()
            .size(rows, 9)
            .title(title)
            .manager(singleton.inventoryManager)
            .provider(this)
            .build()

    override fun init(player: Player, contents: InventoryContents) {
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
        cube.storage.forEach {
            val cubeBlock = cube.cubeBlockById(it.id)
            val completePacks = it.amount / 64
            val incompletePack = it.amount % 64

            for (cp in (1..completePacks)) {
                val its = ItemStackBuilder(cubeBlock.itemStack).setAmount(64).build()
                items[index] = ClickableItem.of(its) {

                }
                index += 1
            }
            if (incompletePack != 0) {
                val its = ItemStackBuilder(cubeBlock.itemStack).setAmount(incompletePack).build()
                items[index] = ClickableItem.of(its) {

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
                    .map { it.replace("{price}", cube.allPrice().format()) }

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
                ItemStackBuilder(Main.singleton.headDatabaseAPI.getItemHead(itemId))
                        .setName(itemName)
                        .setLore(itemLore)
            }
            if (sec != "glassPane") {
                contents.set(row, collunm, ClickableItem.of(itb.build()) {
                    when (sec) {
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

        for (cubeDrop in cube.storage) {
            var stackAmount = 0
            var dropProps = cube.cubeBlockById(cubeDrop.id)

            if (stackAmount == 64 || cubeDrop.amount <= 64) {

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

fun main(args: Array<String>) {
    data class BlockDrop(val id: Int, val name: String, val amount: Int, var price: Double)

    val storage = arrayListOf(
            BlockDrop(1, "drop 1", 129, 30.0),
            BlockDrop(2, "drop 2", 64, 60.0)
    )

    /*
       @Key = id
       @value = quantidade
     */
    var withAmounts = hashMapOf<Int, Int>()
    var itemsSize = 0
    storage.forEach {
        val completePacks = it.amount / 64
        val incompletePack = it.amount % 64

        itemsSize += completePacks
        if (incompletePack != 0) {
            itemsSize += 1
        }
    }

}
