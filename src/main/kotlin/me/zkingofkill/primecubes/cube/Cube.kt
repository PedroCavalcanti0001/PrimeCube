package me.zkingofkill.primecubes.cube

import me.zkingofkill.primecubes.Main
import me.zkingofkill.primecubes.cube.upgrade.IUpgrade
import me.zkingofkill.primecubes.cube.upgrade.layers.impl.LayersUpgrade
import me.zkingofkill.primecubes.cube.upgrade.loot.impl.LootUpgrade
import me.zkingofkill.primecubes.cube.upgrade.speed.impl.SpeedUpgrade
import me.zkingofkill.primecubes.cube.upgrade.storage.impl.StorageUpgrade
import me.zkingofkill.primecubes.exception.UpgradeNotFoundException
import me.zkingofkill.primecubes.manager.CubeManager
import me.zkingofkill.primecubes.utils.tag
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import utils.ItemStackBuilder
import kotlin.random.Random

data class Cube(var typeId: Int,
                val props: CubeProps = CubeProps.byTypeId(typeId, true)!!,
                var uniqueId: Int = CubeManager.genId(),
                var owner: String,
                var location: Location,
                var cubeSize: CubeSize = props.cubeSize,
                var life: Double = props.maxLife,
                var storage: ArrayList<CubeDrop> = arrayListOf(),
                var cubeBlocks: ArrayList<CubeBlock> = arrayListOf(),
                var upgrades: HashMap<UpgradeType, Int> = hashMapOf(),
                var placeTime: Long = System.currentTimeMillis(),
                var cubeBlockLocations: ArrayList<CubeBlockLocation> = arrayListOf()) {
    val cuboid = Cuboid(this)
    val cyborg = CubeCyborg(this)
    val manager: CubeManager = CubeManager(this)


    fun timeToSuccessfullyRemove(): Double {
        var f = -1.0
        val now = System.currentTimeMillis()
        val totalTime = 60
        val r = (now - placeTime) / 1000
        f = (((r - totalTime) * -1).toDouble())
        return f
    }


    fun canNowRenegerateTheBlock(location: Location): Boolean {
        val find = cubeBlockLocations.find {
            it.location.blockX == location.blockX &&
                    it.location.blockY == location.blockY &&
                    it.location.blockZ == location.blockZ
        }
        return if (find != null) {
            val canBreak = find.timeToRefil(this) <= 0.0
            if (canBreak) {
                find.lastBreak = System.currentTimeMillis()
                true
            } else {
                false
            }
        } else {
            val cubeBlockLocation = CubeBlockLocation(location, System.currentTimeMillis())
            cubeBlockLocations.add(cubeBlockLocation)
            true
        }
    }

    fun level(type: UpgradeType): Int {
        val upgrade = upgrades.entries.find { it.key == type }
        return upgrade?.value ?: 0
    }

    fun timeToRegenerate(): Int {
        val level = level(UpgradeType.SPEED)
        val upgrade = IUpgrade.byType(UpgradeType.SPEED) as SpeedUpgrade
        return if (!this.upgrades.contains(UpgradeType.SPEED)) {
            this.props.defaultSpeed
        } else upgrade.levels[level].timeToRegenerate

    }

    fun addLevel(type: UpgradeType) {
        var find = upgrades.entries.find { it.key == type }
        if (find != null) {
            find.setValue(find.value + 1)
        } else {
            upgrades[type] = 1
        }
    }

    fun enoughSpace(location: Location): Boolean {
        val allBlocks = this.cuboid.allBlocks().filter {
            it.location.blockX != location.blockX &&
                    it.location.blockY != location.blockY &&
                    it.location.blockZ != location.blockZ
        }

        val find = allBlocks.find { it.type != Material.AIR }
        return find == null
    }

    fun availableLayers(): Int {
        val levelSections = level(UpgradeType.LAYERS)
        val upgrade = IUpgrade.byType(UpgradeType.LAYERS) as LayersUpgrade
        return return if (!this.props.upgrades.contains(UpgradeType.LAYERS) || levelSections == 0)
            this.props.defaultSections else upgrade.levels[levelSections].sections
    }

    fun availableStock(id: Int): Int {
        val levelStorage = level(UpgradeType.STORAGE)
        val upgrade = IUpgrade.byType(UpgradeType.STORAGE) as StorageUpgrade
        val totalStock = if (levelStorage == 0) this.props.defaultStorage else upgrade.levels[levelStorage].totalAmountPerDrop
        val find = storage.find { it.id == id }
        return if (find != null) totalStock - find.amount else totalStock
    }

    fun allPrice(): Double {
        return storage.sumByDouble { cubeDrop ->
            return cubeDrop.amount * cubeDrop.averagePrice
        }
    }

    fun cubeBlockByItemStack(itemStack: ItemStack): CubeBlock {
        var cubeBlock = props.blocks.find {
            it.itemStack.type == itemStack.type &&
                    itemStack.durability == it.itemStack.durability
        } ?: throw NullPointerException("cube block (${itemStack.type}) not found!")
        return cubeBlock
    }

    fun cubeBlockById(id: Int): CubeBlock {
        var cubeBlock = props.blocks.find { it.id == id } ?: throw NullPointerException("cube drop (id $id) not found!")
        return cubeBlock
    }


    fun addDrop(id: Int, amount: Int) {
        val cubeBlock = cubeBlockById(id)
        val dropStorage = storage.find { it.id == id }
        var level = level(UpgradeType.LOOT)
        var price = if (level > 0) {
            val upgrade = IUpgrade.byType(UpgradeType.LOOT) as LootUpgrade
            val percentage = upgrade.levels[level].increasePercentage
            cubeBlock.unitPrice + (cubeBlock.unitPrice / percentage * 100)
        } else {
            cubeBlock.unitPrice
        }
        if (dropStorage != null) {
            price += (price + dropStorage.averagePrice) / (dropStorage.amount + amount)
            dropStorage.amount += amount
            dropStorage.averagePrice = price
        } else {
            storage.add(CubeDrop(id, price, amount))
        }
    }


    fun sellAll(player: Player) {
        Main.singleton.economy.depositPlayer(player, allPrice())
        storage = arrayListOf()
    }

    fun nextBlock(): CubeBlock {
        val blocks = this.props.blocks
        blocks.sortedBy { it.chance }
        val rd = blocks.filter {
            val chance = Random.nextInt(100) + 1
            it.chance >= chance
        }.random()
        return rd
    }

    fun itemStack(): ItemStack {
        var itemStack = props.itemStack.clone()
        val lore = if (itemStack.hasItemMeta() && itemStack.itemMeta.hasLore()) itemStack.itemMeta.lore else arrayListOf()

        val itemStackBuilder = ItemStackBuilder(itemStack)

        val line = lore.find { it.contains("{upgradeLevel}") || it.contains("{UpgradeName}") }
        if (line != null) {
            var index = lore.indexOf(line)
            lore.remove(line)
            props.upgrades.forEach {
                val upgrade = IUpgrade.byType(it) ?: throw UpgradeNotFoundException(it)
                val level = level(it)
                itemStack = itemStack.tag("{primecubes_${it}_$level}", level.toString())
                lore.add(index, line.replace("{UpgradeName}", upgrade.name)
                        .replace("{upgradeLevel}", level.toString())
                        .replace("&", "§"))
                index++
            }
        }
        itemStackBuilder.setLore(lore)

        return itemStackBuilder.build().tag("{primecubes_typeId}", typeId.toString())
    }

    companion object {

        fun bordersByLocation(location: Location): Cube? {
            return CubeManager.list.find { cube ->
                cube.cuboid.borders().filter { block ->
                    block.location.blockX == location.blockX &&
                            block.location.blockY == location.blockY &&
                            block.location.blockZ == location.blockZ
                }.isNotEmpty()
            }
        }

        fun breakZoneByLocation(location: Location): Cube? {
            return CubeManager.list.find { cube ->
                cube.cuboid.cubeBlocksWithLayers().filter { block ->
                    block.location.blockX == location.blockX &&
                            block.location.blockY == location.blockY &&
                            block.location.blockZ == location.blockZ
                }.isNotEmpty()
            }
        }

        fun breakZoneByLocationWhitoutLayers(location: Location): Cube? {
            return CubeManager.list.find { cube ->
                cube.cuboid.cubeBlocksWhitoutLayers().filter { block ->
                    block.location.blockX == location.blockX &&
                            block.location.blockY == location.blockY &&
                            block.location.blockZ == location.blockZ
                }.isNotEmpty()
            }
        }


    }
}

