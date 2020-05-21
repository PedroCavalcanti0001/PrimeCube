package me.zkingofkill.primecubes.listener

import me.zkingofkill.primecubes.Main
import me.zkingofkill.primecubes.cube.Cube
import me.zkingofkill.primecubes.cube.CubeBlockLocation
import me.zkingofkill.primecubes.cube.CubeProps
import me.zkingofkill.primecubes.cube.UpgradeType
import me.zkingofkill.primecubes.gui.MainGUI
import me.zkingofkill.primecubes.util.removeItems
import me.zkingofkill.primecubes.util.tag
import org.bukkit.Material
import org.bukkit.entity.ArmorStand
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import utils.ItemStackBuilder

class CubeListeners : Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlace(event: BlockPlaceEvent) {
        val player = event.player
        val mainHand = player.inventory.itemInHand
        val block = event.blockPlaced
        var location = block.location
        location.yaw = player.location.yaw
        if (!event.isCancelled) {
            if (mainHand != null && mainHand.type != Material.AIR) {
                var type = mainHand.tag("{primecubes_typeId}")?.toInt()
                if (type != null) {
                    val cubeProps = CubeProps.byTypeId(type)

                    if (cubeProps != null) {
                        var upgrades = hashMapOf<UpgradeType, Int>()

                        cubeProps.upgrades.forEach {
                            val level = mainHand.tag("{primecubes_${it}}")?.toInt() ?: 0
                            upgrades[it] = level
                        }
                        val cube = Cube(typeId = type, owner = player.name, location = location, upgrades = upgrades)
                        Main.singleton.server.scheduler.runTask(Main.singleton) {
                            if (cube.enoughSpace()) {
                                cube.manager.place()
                                player.inventory.removeItems(mainHand, 1)

                            } else {
                                player.sendMessage(Main.singleton.messagesFile.getString("noSpace")
                                        .replace("&", "§"))
                            }
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
/*
    @EventHandler
    fun onKothEnd(event: KothEndEvent) {
        val winner = event.winner
        if(winner != null){
            winner.
    }

 */


    @EventHandler
    fun onArmorStandManipulate(event: PlayerInteractAtEntityEvent) {
        println(event.clickedPosition.toBlockVector().toLocation(event.player.world))
        val location = event.rightClicked.location
        val find = Cube.allCyborgLocations().find {
            it.y == location.y &&
                    it.x == location.x &&
                    location.z == it.z
        }
        if (find != null) {
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onInteract(event: PlayerInteractEvent) {
        val block = event.clickedBlock ?: return
        val location = block.location
        val player = event.player
        val inHand = player.inventory.itemInMainHand
        if (event.isCancelled) return
        if (inHand == null || inHand.type != Material.AIR) return
        if (block.type != Material.BEDROCK) return
        val cube = Cube.bordersByLocation(location)
        if (cube != null) {
            MainGUI(player, cube).open()
        }


    }


/*
    @EventHandler
    fun entityDamage(event: EntityDamageEvent) {
        val entity = event.entity
        if (entity is LivingEntity) {
            if (entity is Zombie) {
                if (entity.isBaby) {
                    if (!entity.hasAI()) {
                        val location = entity.location
                        val find = Cube.allCyborgLocations().find {
                            it.y == location.y &&
                                    it.x == location.x &&
                                    location.z == it.z &&
                                    location.yaw == it.yaw &&
                                    location.pitch == it.pitch
                        }
                        if (find != null) {
                            event.isCancelled = true
                        }
                    }
                }
            }
        }
    }

 */

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
        if (event.isCancelled) return
        if (cube != null) {
            var cubeBlock = cube.cubeBlockByItemStack(itemStack)
            if (cube.availableStock(cubeBlock.id) >= 1) {
                block.type = Material.AIR
                val find = cube.findCubeBlockLocation(location)
                if (find == null) {
                    cube.cubeBlockLocations.add(CubeBlockLocation(location, System.currentTimeMillis()))
                } else {
                    find.lastBreak = System.currentTimeMillis()
                }
                cube.addDrop(cubeBlock.id, 1)
            } else {
                player.sendMessage(Main.singleton.messagesFile.getString("noSpaceToStore")
                        .replace("&", "§"))
            }
            event.isCancelled = true
        }

        if (block !is ArmorStand) return
        val find = Cube.allCyborgLocations().find {
            it.y == location.y &&
                    it.x == location.x &&
                    location.z == it.z
        }
        if (find != null) {
            event.isCancelled = true
        }
    }
}