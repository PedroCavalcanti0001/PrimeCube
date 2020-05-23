package me.zkingofkill.primecubes.manager

import me.zkingofkill.primecubes.Main
import me.zkingofkill.primecubes.cube.Cube
import me.zkingofkill.primecubes.cube.CubeBlockLocation
import me.zkingofkill.primecubes.cube.CubeProps
import me.zkingofkill.primecubes.cube.UpgradeType
import me.zkingofkill.primecubes.cube.upgrade.cyborgfortune.impl.CyborgFortuneLevel
import me.zkingofkill.primecubes.util.freeSlots
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Player
import utils.ItemStackBuilder
import kotlin.random.Random

class CubeManager(var cube: Cube) {

    fun remove(player: Player) {
        var itemstack = cube.itemStack()
        if (cube.isAtMaximum() && cube.props.nextCube != null) {
            val cubeProps = CubeProps.byTypeId(cube.props.nextCube!!)
            if (cubeProps != null) {
                itemstack = cubeProps.itemStack()
                player.sendMessage(Main.singleton.messagesFile.getString("cubeUpToTheNextLevel")
                        .replace("&", "§"))
            }
        }
        cube.cuboid.cyborgLoc.world.entities.forEach {
            if (it is ArmorStand) {
                if (it.location.blockX == cube.cuboid.cyborgLoc.blockX &&
                        it.location.blockY == cube.cuboid.cyborgLoc.blockY  &&
                        it.location.blockZ == cube.cuboid.cyborgLoc.blockZ ) {
                    it.remove()
                }
            }
        }
        this.cube.deleted = true
        if (player.inventory.freeSlots(itemstack) >= 1) {
            player.inventory.addItem(itemstack)
        } else {
            cube.location.world.dropItemNaturally(cube.location, itemstack)
        }
        val allPrice = cube.allPrice(player)
        if (allPrice > 0.0) {
            Main.singleton.economy.depositPlayer(player, allPrice)
            cube.storage = arrayListOf()
        }
        cube.cuboid.allBlocks().forEach { it.type = Material.AIR }
    }

    fun place() {
        cube.cuboid.createCube()
        Main.singleton.server.scheduler.runTask(Main.singleton) {
            if (cube.unlockedCyborg()) {
                val level = cube.level(UpgradeType.CYBORGSPEED)
                if (level >= 1) {
                    cube.spawnCyborg()
                }
            }
        }
        list.add(cube)
    }


    companion object {
        val list = arrayListOf<Cube>()

        fun genId(): Int {
            return if (list.isNotEmpty()) list.maxBy { it.uniqueId }!!.uniqueId + 1 else 0
        }

        fun init() {
            Main.singleton.server.scheduler.runTaskTimer(Main.singleton, {
                list.filter { !it.deleted }.forEach { cube ->
                    cube.cuboid.cubeBlocksWhitoutLayers().forEach {
                        if (it.type == Material.AIR) {
                            val location = it.location
                            val canBreak = cube.canNowRenegerateTheBlock(location)
                            if (canBreak) {
                                val nextBlock = cube.nextBlock()
                                it.type = nextBlock.itemStack.type
                            }
                        }
                    }
                    if (cube.unlockedCyborg()) {
                        if (cube.cyborg.isToBreak()) {
                            val blocks = cube.cuboid.cubeBlocksWithLayers().filter { it.type != Material.AIR }
                            if (blocks.isEmpty()) return@forEach
                            val block = blocks.random()
                            var itemStack = ItemStackBuilder(block.type).setDurability(block.data.toInt()).build()
                            var cubeBlock = cube.cubeBlockByItemStack(itemStack)
                            val location = block.location
                            if (cube.availableStock(cubeBlock.id) >= 1) {
                                location.world.getNearbyEntities(location, 5.0, 5.0, 5.0)
                                        .filter { it is Player }
                                        .map { it as Player }
                                        .forEach { it.playSound(location, Sound.BLOCK_STONE_BREAK, 1f, 1f) }
                                block.type = Material.AIR
                                val find = cube.findCubeBlockLocation(location)
                                if (find == null) {
                                    cube.cubeBlockLocations.add(CubeBlockLocation(location, System.currentTimeMillis()))
                                } else {
                                    find.lastBreak = System.currentTimeMillis()
                                }
                                var amount = 1
                                var levelFortune = cube.level(UpgradeType.CYBORGFORTUNE)

                                if (levelFortune > 0) {
                                    val cyborgFortuneLevel = cube.iUpgradeLevelByLevel(UpgradeType.CYBORGFORTUNE, levelFortune) as CyborgFortuneLevel
                                    val rd = Random.nextInt(100) + 1
                                    if (cyborgFortuneLevel.chance >= rd) {
                                        amount = Random.nextInt(cyborgFortuneLevel.multiply) + 1
                                    }
                                }
                                cube.addDrop(cubeBlock.id, amount)
                            }
                        }
                    }
                }
            }, 35, 35)
        }

        fun saveAll() {
            val mysql = Main.singleton.db
            for (it in list) {
                if (it.deleted) {
                    mysql.delete(it)
                } else {
                    mysql.upsert(it)
                }
            }
        }

        fun delayedSaveAll() {
            Main.singleton.server.scheduler.runTaskTimerAsynchronously(Main.singleton, {
                saveAll()
            }, 20 * 5 * 60, 20 * 5 * 60)
        }
    }
}