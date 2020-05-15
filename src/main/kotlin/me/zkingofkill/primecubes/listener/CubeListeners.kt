package me.zkingofkill.primecubes.listener

import me.zkingofkill.primecubes.Main
import me.zkingofkill.primecubes.cube.Cube
import me.zkingofkill.primecubes.cube.CubeBlockLocation
import me.zkingofkill.primecubes.cube.CubeProps
import me.zkingofkill.primecubes.gui.MainGUI
import me.zkingofkill.primecubes.utils.removeItems
import me.zkingofkill.primecubes.utils.tag
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerInteractEvent
import utils.ItemStackBuilder

class CubeListeners : Listener {

    @EventHandler
    fun onPlace(event: BlockPlaceEvent) {
        val player = event.player
        val mainHand = player.inventory.itemInMainHand
        val block = event.blockPlaced
        var location = block.location
        location.yaw = player.location.yaw
        if (!event.isCancelled) {
            if (mainHand != null && mainHand.type != Material.AIR) {
                var type = mainHand.tag("{primecubes_typeId}")?.toInt()
                if (type != null) {
                    val cubeProps = CubeProps.byTypeId(type)
                    if (cubeProps != null) {
                        val cube = Cube(typeId = type, owner = player.name, location = location)
                        if (cube.enoughSpace(location)) {
                            cube.manager.place()
                            player.inventory.removeItems(mainHand, 1)
                        } else {
                            player.sendMessage(Main.singleton.messagesFile.getString("noSpace")
                                    .replace("&", "§"))
                        }
                    }
                    event.isCancelled = true
                }
            }

            val cube = Cube.breakZoneByLocationWhitoutLayers(location)
            if (cube != null) {
                event.isCancelled = true
            }
        }
    }


    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        val block = event.clickedBlock ?: return
        val location = block.location
        val player = event.player
        if (block.type != Material.BEDROCK) return
        val cube = Cube.bordersByLocation(location)
        if (cube != null) {
            MainGUI(player, cube).open()
        }
    }


    /*

     VERIFICAR SE O PLAYER PODE QUEBRAR

     */
    @EventHandler
    fun onBreak(event: BlockBreakEvent) {
        val block = event.block
        val location = block.location
        val player = event.player
        val cube = Cube.breakZoneByLocation(location)
        var itemStack = ItemStackBuilder(block.type).setDurability(block.data.toInt()).build()
        if (cube != null) {
            var cubeBlock = cube.cubeBlockByItemStack(itemStack)
            if (cube.availableStock(cubeBlock.id) >= 1) {
                block.type = Material.AIR
                cube.cubeBlockLocations.add(CubeBlockLocation(location, System.currentTimeMillis()))
                cube.addDrop(cubeBlock.id, 1)
            } else {
                player.sendMessage(Main.singleton.messagesFile.getString("noSpaceToStore")
                        .replace("&", "§"))
            }
            event.isCancelled = true
        }
    }
}