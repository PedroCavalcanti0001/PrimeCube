package me.zkingofkill.primecubes.cube.upgrade

import fr.minuskube.inv.content.SlotPos
import me.zkingofkill.primecubes.Main
import me.zkingofkill.primecubes.cube.UpgradeType
import me.zkingofkill.primecubes.cube.upgrade.cyborgfortune.impl.CyborgFortuneLevel
import me.zkingofkill.primecubes.cube.upgrade.cyborgfortune.impl.CyborgFortuneUpgrade
import me.zkingofkill.primecubes.cube.upgrade.cyborgspeed.impl.CyborgSpeedLevel
import me.zkingofkill.primecubes.cube.upgrade.cyborgspeed.impl.CyborgSpeedUpgrade
import me.zkingofkill.primecubes.cube.upgrade.cyborglayers.impl.LayersLevel
import me.zkingofkill.primecubes.cube.upgrade.cyborglayers.impl.LayersUpgrade
import me.zkingofkill.primecubes.cube.upgrade.loot.impl.LootLevel
import me.zkingofkill.primecubes.cube.upgrade.loot.impl.LootUpgrade
import me.zkingofkill.primecubes.cube.upgrade.speed.impl.SpeedLevel
import me.zkingofkill.primecubes.cube.upgrade.speed.impl.SpeedUpgrade
import me.zkingofkill.primecubes.cube.upgrade.storage.impl.StorageLevel
import me.zkingofkill.primecubes.cube.upgrade.storage.impl.StorageUpgrade
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import utils.ItemStackBuilder

interface IUpgrade<ImplLevel> {
    var name: String
    var slotPos: SlotPos
    var itemStack: ItemStack
    fun nextLevel(currentLevel: Int): IUpgradeLevel?
    var levels: ArrayList<ImplLevel>
    var levelMax: Int
    var upgradeType: UpgradeType

}