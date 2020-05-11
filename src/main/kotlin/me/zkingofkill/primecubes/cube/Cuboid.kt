package me.zkingofkill.primecubes.cube

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import kotlin.math.max
import kotlin.math.min


data class Cuboid(var location1: Location, var x: Int, var y: Int, var z: Int) {

    private var world: World = location1.world
    private var xMax: Int
    private var xMin: Int
    private var yMax: Int
    private var yMin: Int
    private var zMax: Int
    private var zMin: Int
    private val direction = direction()
    var location2: Location = secondLoc()

    init {
        location1 = face()
        this.xMax = max(location1.blockX, location2.blockX)
        this.xMin = min(location1.blockX, location2.blockX)
        this.yMax = max(location1.blockY, location2.blockY)
        this.yMin = min(location1.blockY, location2.blockY)
        this.zMax = max(location1.blockZ, location2.blockZ)
        this.zMin = min(location1.blockZ, location2.blockZ)

    }

    fun createCube() {
        blocksFromTwoPoints(location1, location2).forEach {
            it.type = Material.STONE
        }

        borders().forEach {
            it.type = Material.BEDROCK
        }
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
        val bottom1 = location1.clone().subtract(0.0, 1.0, 0.0)
        val bottom2 = location2.clone()
        val collunm1 = location1.clone()
        val collunm2 = location1.clone()
        val collunm3 = location1.clone()
        val collunm4 = location1.clone()
        lateinit var collunm1Top: Location
        lateinit var collunm2Top: Location
        lateinit var collunm3Top: Location
        lateinit var collunm4Top: Location
        bottom2.y = bottom1.y
        when (direction) {
            BlockFace.NORTH -> {
                bottom1.subtract(1.0, 0.0, 0.0).add(0.0, 0.0, 1.0)
                bottom2.subtract(0.0, 0.0, 1.0).add(1.0, 0.0, 0.0)

                collunm1.subtract(1.0, 0.0, 0.0).add(0.0, 0.0, 1.0)
                collunm2.add(x.toDouble(), 0.0, 1.0)
                collunm3.subtract(1.0, 0.0, z.toDouble())
                collunm4.subtract(0.0, 0.0, z.toDouble()).add(x.toDouble(), 0.0, 0.0)
                collunm1Top = collunm1.clone().add(0.0, y.toDouble(), 0.0)
                collunm2Top = collunm2.clone().add(0.0, y.toDouble(), 0.0)
                collunm3Top = collunm3.clone().add(0.0, y.toDouble(), 0.0)
                collunm4Top = collunm4.clone().add(0.0, y.toDouble(), 0.0)
                blocks.addAll(blocksFromTwoPoints(collunm1, collunm1Top))
                blocks.addAll(blocksFromTwoPoints(collunm2, collunm2Top))
                blocks.addAll(blocksFromTwoPoints(collunm3, collunm3Top))
                blocks.addAll(blocksFromTwoPoints(collunm4, collunm4Top))
                blocks.addAll(blocksFromTwoPoints(bottom1, bottom2))
                blocks.addAll(blocksFromTwoPoints(collunm1Top, collunm2Top))
                blocks.addAll(blocksFromTwoPoints(collunm2Top, collunm3Top))
                blocks.addAll(blocksFromTwoPoints(collunm3Top, collunm4Top))
            }
            BlockFace.SOUTH -> {
                bottom1.add(1.0, 0.0, 0.0).subtract(0.0, 0.0, 1.0)
                bottom2.add(0.0, 0.0, 1.0).subtract(1.0, 0.0, 0.0)
                collunm1.add(1.0, 0.0, 0.0).subtract(0.0, 0.0, 1.0)
                collunm2.subtract(x.toDouble(), 0.0, 1.0)
                collunm3.add(1.0, 0.0, z.toDouble())
                collunm4.add(0.0, 0.0, z.toDouble()).subtract(x.toDouble(), 0.0, 0.0)
                collunm1Top = collunm1.clone().add(0.0, y.toDouble(), 0.0)
                collunm2Top = collunm2.clone().add(0.0, y.toDouble(), 0.0)
                collunm3Top = collunm3.clone().add(0.0, y.toDouble(), 0.0)
                collunm4Top = collunm4.clone().add(0.0, y.toDouble(), 0.0)
                blocks.addAll(blocksFromTwoPoints(collunm1, collunm1Top))
                blocks.addAll(blocksFromTwoPoints(collunm2, collunm2Top))
                blocks.addAll(blocksFromTwoPoints(collunm3, collunm3Top))
                blocks.addAll(blocksFromTwoPoints(collunm4, collunm4Top))
                blocks.addAll(blocksFromTwoPoints(bottom1, bottom2))
                blocks.addAll(blocksFromTwoPoints(collunm1Top, collunm2Top))
                blocks.addAll(blocksFromTwoPoints(collunm2Top, collunm3Top))
                blocks.addAll(blocksFromTwoPoints(collunm3Top, collunm4Top))
            }
            BlockFace.WEST -> {
                bottom1.add(1.0, 0.0, 1.0)
                bottom2.subtract(1.0, 0.0, 1.0)
                collunm1.add(1.0, 0.0, 1.0).subtract(0.0, 0.0, 0.0)
                collunm2.add(1.0, 0.0, 0.0).subtract(0.0, 0.0, z.toDouble())
                collunm3.subtract(x.toDouble(), 0.0, 0.0).add(0.0, 0.0, 1.0)
                collunm4.subtract(x.toDouble(), 0.0, z.toDouble())

                collunm1Top = collunm1.clone().add(0.0, y.toDouble(), 0.0)
                collunm2Top = collunm2.clone().add(0.0, y.toDouble(), 0.0)
                collunm3Top = collunm3.clone().add(0.0, y.toDouble(), 0.0)
                collunm4Top = collunm4.clone().add(0.0, y.toDouble(), 0.0)

                blocks.addAll(blocksFromTwoPoints(collunm1, collunm1Top))
                blocks.addAll(blocksFromTwoPoints(collunm2, collunm2Top))
                blocks.addAll(blocksFromTwoPoints(collunm3, collunm3Top))
                blocks.addAll(blocksFromTwoPoints(collunm4, collunm4Top))
                blocks.addAll(blocksFromTwoPoints(bottom1, bottom2))
                blocks.addAll(blocksFromTwoPoints(collunm1Top, collunm2Top))
                blocks.addAll(blocksFromTwoPoints(collunm2Top, collunm3Top))
                blocks.addAll(blocksFromTwoPoints(collunm3Top, collunm4Top))
            }
            BlockFace.EAST -> {
                bottom1.subtract(1.0, 0.0, 1.0)
                bottom2.add(1.0, 0.0, 1.0)

                collunm1.subtract(1.0, 0.0, 1.0)
                collunm2.subtract(1.0, 0.0, 0.0).add(0.0, 0.0, z.toDouble())
                collunm3.add(x.toDouble(), 0.0, 0.0).subtract(0.0, 0.0, 1.0)
                collunm4.add(x.toDouble(), 0.0, z.toDouble())

                collunm1Top = collunm1.clone().add(0.0, y.toDouble(), 0.0)
                collunm2Top = collunm2.clone().add(0.0, y.toDouble(), 0.0)
                collunm3Top = collunm3.clone().add(0.0, y.toDouble(), 0.0)
                collunm4Top = collunm4.clone().add(0.0, y.toDouble(), 0.0)

                blocks.addAll(blocksFromTwoPoints(collunm1Top, collunm2Top))
                blocks.addAll(blocksFromTwoPoints(collunm2Top, collunm3Top))
                blocks.addAll(blocksFromTwoPoints(collunm3Top, collunm4Top))

                blocks.addAll(blocksFromTwoPoints(collunm1, collunm1Top))
                blocks.addAll(blocksFromTwoPoints(collunm2, collunm2Top))
                blocks.addAll(blocksFromTwoPoints(collunm3, collunm3Top))
                blocks.addAll(blocksFromTwoPoints(collunm4, collunm4Top))
                blocks.addAll(blocksFromTwoPoints(bottom1, bottom2))

            }

        }
        return blocks

    }

    private fun direction(): BlockFace {
        var rotation = (this.location1.yaw - 180) % 360.toDouble()
        if (rotation < 0) {
            rotation += 360.0
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
        val loc = location1.clone().add(0.0, 1.0, 0.0)
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
        val borders = borders()
        borders.addAll(blocksFromTwoPoints(location1, location2).toList())
        return borders
    }

    private fun secondLoc(): Location {
        var loc = location1.clone()
        return when (this.direction) {
            BlockFace.NORTH -> {
                loc = loc.subtract(0.0, 0.0, z.toDouble()).add(x.toDouble() - 1, y.toDouble(), 0.0)
                loc
            }
            BlockFace.SOUTH -> {
                loc = loc.subtract(x.toDouble() - 1, 0.0, 0.0).add(0.0, y.toDouble(), z.toDouble())
                loc
            }
            BlockFace.EAST -> {
                loc = loc.add(x.toDouble(), y.toDouble(), z.toDouble() - 1)
                loc
            }
            BlockFace.WEST -> {
                loc = loc.subtract(x.toDouble(), 0.0, z.toDouble() - 1).add(0.0, y.toDouble(), 0.0)
                loc
            }
            else -> location1
        }
    }

}