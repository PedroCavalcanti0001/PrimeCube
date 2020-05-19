package me.zkingofkill.primecubes.cube

import me.zkingofkill.primecubes.Main
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import kotlin.math.max
import kotlin.math.min


data class Cuboid(var cube: Cube) {

    private var l1: Location = cube.location
    private var x = cube.cubeSize.xz
    private var y = cube.cubeSize.y
    private var z = cube.cubeSize.xz
    private var xMax: Int
    private var xMin: Int
    private var yMax: Int
    private var yMin: Int
    private var zMax: Int
    private var zMin: Int
    private val direction = direction()
    var cyborgLoc: Location
    var l2: Location = secondLoc()

    init {
        l1 = face()
        this.xMax = max(l1.blockX, l2.blockX)
        this.xMin = min(l1.blockX, l2.blockX)
        this.yMax = max(l1.blockY, l2.blockY)
        this.yMin = min(l1.blockY, l2.blockY)
        this.zMax = max(l1.blockZ, l2.blockZ)
        this.zMin = min(l1.blockZ, l2.blockZ)

        cyborgLoc = cyborgLoc()

    }

    fun cyborgLoc(): Location {
        var loc = l1.clone()
        loc.yaw = 0f
        borders()
        when (direction) {
            BlockFace.SOUTH -> {
                loc.subtract(0.0, 0.0, 0.5).add(0.5, 0.0, 0.0)
                loc.yaw = 360.0f
            }
            BlockFace.NORTH -> {
                loc.add(0.0, 0.0, 1.5).add(0.5, 0.0, 0.0)
                loc.yaw = -178.0f
            }
            BlockFace.EAST -> {
                loc.add(0.0, 0.0, 0.5).subtract(0.5,0.0,0.0)
                loc.yaw = -90.0f
            }
            BlockFace.WEST -> {
                loc.add(1.5, 0.0, 0.5)
                loc.yaw = 90.0f
            }
        }
        return loc
    }

    fun createCube() {
        cubeBlocksWithLayers().forEach {
            it.type = cube.nextBlock().itemStack.type
        }
        val borders = borders()
        Bukkit.getServer().scheduler.runTask(Main.singleton) {
            borders.forEach {
                it.type = Material.BEDROCK
            }
        }
    }

    fun cubeBlocksWithLayers(): List<Block> {
        val maxLayers = cube.availableLayers()
        return blocksAvailable(l1, l2, maxLayers)
    }
    fun cubeBlocksWhitoutLayers(): List<Block> {
        return blocksFromTwoPoints(l1, l2)
    }


    fun blocksFromTwoPoints(loc1: Location, loc2: Location): List<Block> {
        val blocks: MutableList<Block> = ArrayList()
        val topBlockX = if (loc1.blockX < loc2.blockX) loc2.blockX else loc1.blockX
        val bottomBlockX = if (loc1.blockX > loc2.blockX) loc2.blockX else loc1.blockX
        val topBlockY = if (loc1.blockY < loc2.blockY) loc2.blockY else loc1.blockY
        val bottomBlockY = if (loc1.blockY > loc2.blockY) loc2.blockY else loc1.blockY
        val topBlockZ = if (loc1.blockZ < loc2.blockZ) loc2.blockZ else loc1.blockZ
        val bottomBlockZ = if (loc1.blockZ > loc2.blockZ) loc2.blockZ else loc1.blockZ
        for (x in bottomBlockX..topBlockX) {
            for (z in bottomBlockZ..topBlockZ) {
                for (y in bottomBlockY..topBlockY) {
                    val block = loc1.world.getBlockAt(x, y, z)
                    blocks.add(block)
                }
            }
        }
        return blocks
    }

    fun borders(): ArrayList<Block> {
        val blocks = arrayListOf<Block>()

        val collunm1 = l1.clone()
        val collunm2 = l1.clone()
        val collunm3 = l1.clone()
        val collunm4 = l1.clone()

        when (direction) {
            BlockFace.NORTH -> {
                collunm1.subtract(1.0, 0.0, 0.0).add(0.0, 0.0, 1.0)
                collunm2.add(x, 0.0, 1.0)
                collunm3.subtract(1.0, 0.0, z)
                collunm4.subtract(0.0, 0.0, z).add(x, 0.0, 0.0)
            }
            BlockFace.SOUTH -> {
                collunm1.add(1.0, 0.0, 0.0).subtract(0.0, 0.0, 1.0)
                collunm2.subtract(x, 0.0, 1.0)
                collunm3.add(1.0, 0.0, z)
                collunm4.add(0.0, 0.0, z).subtract(x, 0.0, 0.0)
            }
            BlockFace.WEST -> {
                collunm1.add(1.0, 0.0, 1.0).subtract(0.0, 0.0, 0.0)
                collunm2.add(1.0, 0.0, 0.0).subtract(0.0, 0.0, z)
                collunm3.subtract(x, 0.0, 0.0).add(0.0, 0.0, 1.0)
                collunm4.subtract(x, 0.0, z)
            }
            BlockFace.EAST -> {
                collunm1.subtract(1.0, 0.0, 1.0)
                collunm2.subtract(1.0, 0.0, 0.0).add(0.0, 0.0, z)
                collunm3.add(x, 0.0, 0.0).subtract(0.0, 0.0, 1.0)
                collunm4.add(x, 0.0, z)
            }
        }

        blocks.addAll(blocksFromTwoPoints(collunm1.clone().subtract(0.0, 1.0, 0.0),
                collunm2.clone().subtract(0.0, 1.0, 0.0)))

        blocks.addAll(blocksFromTwoPoints(collunm2.clone().subtract(0.0, 1.0, 0.0),
                collunm4.clone().subtract(0.0, 1.0, 0.0)))

        blocks.addAll(blocksFromTwoPoints(collunm1.clone().subtract(0.0, 1.0, 0.0),
                collunm3.clone().subtract(0.0, 1.0, 0.0)))

        blocks.addAll(blocksFromTwoPoints(collunm3.clone().subtract(0.0, 1.0, 0.0),
                collunm4.clone().subtract(0.0, 1.0, 0.0)))

        var collunm1Top = collunm1.clone().add(0.0, y, 0.0)
        var collunm2Top = collunm2.clone().add(0.0, y, 0.0)
        var collunm3Top = collunm3.clone().add(0.0, y, 0.0)
        var collunm4Top = collunm4.clone().add(0.0, y, 0.0)

        blocks.addAll(blocksFromTwoPoints(collunm1, collunm1Top))
        blocks.addAll(blocksFromTwoPoints(collunm2, collunm2Top))
        blocks.addAll(blocksFromTwoPoints(collunm3, collunm3Top))
        blocks.addAll(blocksFromTwoPoints(collunm4, collunm4Top))
        blocks.addAll(blocksFromTwoPoints(collunm1Top, collunm2Top))
        blocks.addAll(blocksFromTwoPoints(collunm2Top, collunm4Top))
        blocks.addAll(blocksFromTwoPoints(collunm1Top, collunm3Top))
        blocks.addAll(blocksFromTwoPoints(collunm3Top, collunm4Top))

        return blocks

    }

    fun blocksAvailable(loc1: Location, loc2: Location, layers: Int): List<Block> {
        val blocks: MutableList<Block> = ArrayList()
        val topBlockX = if (loc1.blockX < loc2.blockX) loc2.blockX else loc1.blockX
        val bottomBlockX = if (loc1.blockX > loc2.blockX) loc2.blockX else loc1.blockX
        val topBlockY = if (loc1.blockY < loc2.blockY) loc2.blockY else loc1.blockY
        val bottomBlockY = if (loc1.blockY > loc2.blockY) loc2.blockY else loc1.blockY
        val topBlockZ = if (loc1.blockZ < loc2.blockZ) loc2.blockZ else loc1.blockZ
        val bottomBlockZ = if (loc1.blockZ > loc2.blockZ) loc2.blockZ else loc1.blockZ
        var totalPlaced = 0
        for (x in (bottomBlockX..topBlockX)) {
            for (z in (bottomBlockZ..topBlockZ)) {
                for (y in bottomBlockY..topBlockY) {
                    val block = loc1.world.getBlockAt(x, y, z)
                    blocks.add(block)
                }
                totalPlaced += 1
                if (totalPlaced == layers) {
                    return blocks
                }
            }
        }
        return blocks
    }

    private fun direction(): BlockFace {
        var rotation = (this.l1.yaw - 180) % 360
        if (rotation < 0) {
            rotation += 360.0.toFloat()
        }
        return if ((0 <= rotation && rotation < 22.5) || (22.5 <= rotation && rotation < 67.5) || (292.5 <= rotation && rotation < 337.5) || (337.5 <= rotation && rotation < 360.0)) {
            BlockFace.NORTH
        } else if (67.5 <= rotation && rotation < 112.5) {
            BlockFace.EAST
        } else if ((157.5 <= rotation && rotation < 202.5) || (112.5 <= rotation && rotation < 157.5) || (202.5 <= rotation && rotation < 247.5)) {
            BlockFace.SOUTH
        } else if (247.5 <= rotation && rotation < 292.5) {
            BlockFace.WEST
        } else {
            BlockFace.NORTH
        }
    }

    private fun face(): Location {
        val loc = l1.clone().add(0.0, 1.0, 0.0)
        return when (direction) {
            BlockFace.NORTH -> {
                loc.subtract(0.0, 0.0, 1.0)
            }
            BlockFace.SOUTH -> {
                loc.add(0.0, 0.0, 1.0)
            }
            BlockFace.WEST -> {
                loc.subtract(1.0, 0.0, 0.0)
            }
            BlockFace.EAST -> {
                loc.add(1.0, 0.0, 0.0)
            }
            else -> loc
        }
    }

    fun allBlocks(): ArrayList<Block> {
        val borders = ArrayList(borders())
        borders.addAll(blocksFromTwoPoints(l1, l2).toList())
        return borders
    }

    private fun secondLoc(): Location {
        var loc = l1.clone()
        return when (this.direction) {
            BlockFace.NORTH -> {
                loc = loc.subtract(0.0, 0.0, z).add(x - 1, y, 0.0)
                loc
            }
            BlockFace.SOUTH -> {
                loc = loc.subtract(x - 1, 0.0, 0.0).add(0.0, y, z)
                loc
            }
            BlockFace.EAST -> {
                loc = loc.add(x, y, z - 1)
                loc
            }
            BlockFace.WEST -> {
                loc = loc.subtract(x, 0.0, z - 1).add(0.0, y, 0.0)
                loc
            }
            else -> l1
        }
    }

}