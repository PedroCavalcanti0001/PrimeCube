package me.zkingofkill.primecubes.cube

import me.zkingofkill.primecubes.Main
import me.zkingofkill.primecubes.cube.upgrade.IUpgrade
import me.zkingofkill.primecubes.cube.upgrade.IUpgradeLevel
import me.zkingofkill.primecubes.cube.upgrade.cyborglayers.impl.LayersUpgrade
import me.zkingofkill.primecubes.cube.upgrade.loot.impl.LootUpgrade
import me.zkingofkill.primecubes.cube.upgrade.speed.impl.SpeedUpgrade
import me.zkingofkill.primecubes.cube.upgrade.storage.impl.StorageUpgrade
import me.zkingofkill.primecubes.exception.UpgradeNotFoundException
import me.zkingofkill.primecubes.manager.CubeManager
import me.zkingofkill.primecubes.util.tag
import net.brcdev.shopgui.ShopGuiPlusApi
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import utils.CustomHead
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
                var upgrades: HashMap<UpgradeType, Int> = hashMapOf(),
                var placeTime: Long = System.currentTimeMillis(),
                var deleted: Boolean = false,
                var cubeBlockLocations: ArrayList<CubeBlockLocation> = arrayListOf()) {
    val cuboid = Cuboid(this)
    val cyborg = CubeCyborg(this)
    val manager: CubeManager = CubeManager(this)


    fun timeToSuccessfullyRemove(): Double {
        var f = -1.0
        val now = System.currentTimeMillis()
        val totalTime = this.props.timeToRemove
        val r = (now - placeTime) / 1000
        f = (((r - totalTime) * -1).toDouble())
        return f
    }

    fun findCubeBlockLocation(location: Location): CubeBlockLocation? {
        return cubeBlockLocations.find {
            it.location.blockX == location.blockX &&
                    it.location.blockY == location.blockY &&
                    it.location.blockZ == location.blockZ
        }
    }

    fun canNowRenegerateTheBlock(location: Location): Boolean {
        val find = findCubeBlockLocation(location)
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

    fun iUpgrade(upgradeType: UpgradeType): IUpgrade<Any>? {
        val find = props.upgrades.find { it.upgradeType == upgradeType }
        return find
    }

    fun iUpgradeLevelByLevel(upgradeType: UpgradeType, level: Int): IUpgradeLevel {
        val iupgrade = iUpgrade(upgradeType) ?: throw UpgradeNotFoundException(upgradeType.name)
        val levels = iupgrade.levels as ArrayList<IUpgradeLevel>
        return levels.find { it.level == level }!!

    }

    fun isAtMaximum(): Boolean {
        return (props.upgrades.find { upgradeProp ->
            val activatedCyborg = props.activatedCyborg
            val find = upgrades.entries.find { upgrade -> upgrade.key == upgradeProp.upgradeType}
            (find == null) || (upgradeProp.levelMax != find.value)
        }) == null
    }


    fun timeToRegenerate(): Int {
        val level = level(UpgradeType.SPEED)
        val upgrade = iUpgrade(UpgradeType.SPEED) as SpeedUpgrade
        return if (level == 0) {
            this.props.defaultSpeed
        } else upgrade.levels.find { it.level == level }!!.timeToRegenerate

    }

    fun addLevel(type: UpgradeType) {
        var find = upgrades.entries.find { it.key == type }
        if (find != null) {
            upgrades[type] = find.value + 1
        } else {
            upgrades[type] = 1
        }

        if (unlockedCyborg()) {
            val level = level(UpgradeType.CYBORGSPEED)
            if (level == 1) {
                spawnCyborg()
            }
        }
    }

    fun enoughSpace(): Boolean {
        val allBlocks = cuboid.allBlocks()
        val find = allBlocks.find { it.type != Material.AIR }
        return find == null
    }

    fun availableLayers(): Int {
        val levelSections = level(UpgradeType.CYBORGLAYERS)
        val upgrade = iUpgrade(UpgradeType.CYBORGLAYERS) as LayersUpgrade
        return return if (levelSections == 0)
            this.props.defaultSections else upgrade.levels.find { it.level == levelSections }!!.sections
    }

    fun availableStock(id: Int): Int {
        val levelStorage = level(UpgradeType.STORAGE)
        val upgrade = iUpgrade(UpgradeType.STORAGE) as StorageUpgrade
        val totalStock = if (levelStorage == 0) this.props.defaultStorage else upgrade.levels.find { it.level == levelStorage }!!.totalAmountPerDrop
        val find = storage.find { it.id == id }
        return if (find != null) totalStock - find.amount else totalStock
    }

    fun allPrice(player: Player): Double {
        val hook = Main.singleton.shopGUIPlusHook
        return storage.sumByDouble { cubeDrop ->
            val cubeBlock = cubeBlockById(cubeDrop.id)
            var avgPrice = if (hook &&
                    ShopGuiPlusApi.getItemStackPriceSell(player, cubeBlock.itemStack) != -1.0) {
                ShopGuiPlusApi.getItemStackPriceSell(player, cubeBlock.itemStack)
            } else cubeDrop.averagePrice
            avgPrice * cubeDrop.amount
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

    fun unlockedCyborg(): Boolean {
        return if (props.activatedCyborg && this.upgrades.containsKey(UpgradeType.CYBORGSPEED)) {
            val level = level(UpgradeType.CYBORGSPEED)
            level > 0
        } else false
    }


    fun addDrop(id: Int, amount: Int) {
        val cubeBlock = cubeBlockById(id)
        val dropStorage = storage.find { it.id == id }
        var level = level(UpgradeType.LOOT)
        var price = if (level > 0) {
            val upgrade = iUpgrade(UpgradeType.LOOT) as LootUpgrade
            val percentage = upgrade.levels.find { it.level == level }!!.increasePercentage

            cubeBlock.unitPrice + (cubeBlock.unitPrice * percentage / 100)
        } else {
            cubeBlock.unitPrice
        }
        if (dropStorage != null) {

            dropStorage.amount += amount
            dropStorage.averagePrice = price
        } else {
            storage.add(CubeDrop(id, price, amount))
        }
    }

    fun spawnCyborg() {
        val loc = cuboid.cyborgLoc()
        val armorStand: ArmorStand = loc.world.spawn(loc, ArmorStand::class.java) as ArmorStand
        armorStand.setGravity(false)
        armorStand.isMarker = false
        armorStand.isSmall = true
        armorStand.canPickupItems = false
        armorStand.isInvulnerable = true
        armorStand.isSilent = true
        armorStand.customName = this.uniqueId.toString()
        armorStand.isCustomNameVisible = false
        armorStand.setAI(false)
        armorStand.equipment.helmet = CustomHead.itemFromUrl("http://textures.minecraft.net/texture/" + Main.singleton.config.getString("cyborgHead"))
        armorStand.equipment.chestplate = ItemStackBuilder(Material.DIAMOND_CHESTPLATE).build()
        armorStand.equipment.leggings = ItemStackBuilder(Material.DIAMOND_LEGGINGS).build()
        armorStand.equipment.boots = ItemStackBuilder(Material.DIAMOND_BOOTS).build()
        armorStand.equipment.itemInHand = ItemStackBuilder(Material.DIAMOND_PICKAXE).build()
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


        val line = lore.find { it.contains("{upgradeLevel}") || it.contains("{UpgradeName}") }
        if (line != null) {
            var index = lore.indexOf(line)
            lore.remove(line)
            props.upgrades.forEach {

                val level = level(it.upgradeType)
                itemStack = itemStack.tag("{primecubes_${it}}", level.toString())
                lore.add(index, line.replace("{UpgradeName}", it.upgradeType.name)
                        .replace("{upgradeLevel}", level.toString())
                        .replace("&", "§"))
                index++
            }
        }
        val itemStackBuilder = ItemStackBuilder(itemStack)
        itemStackBuilder.setLore(lore)
        return itemStackBuilder.build().tag("{primecubes_typeId}", typeId.toString())
    }

    companion object {

        fun bordersByLocation(location: Location): Cube? {
            return CubeManager.list.filter { !it.deleted }.find { cube ->
                cube.cuboid.borders().filter { block ->
                    block.location.blockX == location.blockX &&
                            block.location.blockY == location.blockY &&
                            block.location.blockZ == location.blockZ
                }.isNotEmpty()
            }
        }

        fun breakZoneByLocation(location: Location): Cube? {
            return CubeManager.list.filter { !it.deleted }.find { cube ->
                cube.cuboid.cubeBlocksWhitoutLayers().filter { block ->
                    block.location.blockX == location.blockX &&
                            block.location.blockY == location.blockY &&
                            block.location.blockZ == location.blockZ
                }.isNotEmpty()
            }
        }

        fun breakZoneByLocationWhitoutLayers(location: Location): Cube? {
            return CubeManager.list.filter { !it.deleted }.find { cube ->
                cube.cuboid.cubeBlocksWhitoutLayers().filter { block ->
                    block.location.blockX == location.blockX &&
                            block.location.blockY == location.blockY &&
                            block.location.blockZ == location.blockZ
                }.isNotEmpty()
            }
        }

        fun allCyborgLocations(): ArrayList<Location> {
            return CubeManager.list.filter { it.unlockedCyborg() }.map { it.cuboid.cyborgLoc } as ArrayList<Location>
        }

    }
}