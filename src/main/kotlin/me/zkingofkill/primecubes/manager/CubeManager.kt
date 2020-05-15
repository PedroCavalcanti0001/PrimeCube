package me.zkingofkill.primecubes.manager

import me.zkingofkill.primecubes.Main
import me.zkingofkill.primecubes.cube.Cube
import me.zkingofkill.primecubes.utils.freeSlots
import org.bukkit.Material
import org.bukkit.entity.Player

class CubeManager(var cube: Cube) {

    fun remove(player: Player) {
        val itemstack = cube.itemStack()
        if (player.inventory.freeSlots(itemstack) >= 1) {
            player.inventory.addItem(itemstack)
        } else {
            cube.location.world.dropItemNaturally(cube.location, itemstack)
        }
        cube.cuboid.allBlocks().forEach { it.type = Material.AIR }
        list.remove(cube)
    }

    fun place() {
        cube.cuboid.createCube()
        list.add(cube)


    }

    fun persist() {

    }

    fun add() {

    }


    companion object {
        val list = arrayListOf<Cube>()

        fun genId(): Int {
            return if (list.isNotEmpty()) list.maxBy { it.uniqueId }!!.uniqueId + 1 else 0
        }

        fun init() {
            Main.singleton.server.scheduler.runTaskTimer(Main.singleton, {
                list.forEach { cube ->
                    cube.cuboid.cubeBlocksWithLayers().forEach {
                        if (it.type == Material.AIR) {
                            val location = it.location
                            val canBreak = cube.canNowRenegerateTheBlock(location)
                            if (canBreak) {
                                val nextBlock = cube.nextBlock()
                                it.type = nextBlock.itemStack.type
                            }
                        }
                    }
                }
            }, 20, 20)
        }


    }
}