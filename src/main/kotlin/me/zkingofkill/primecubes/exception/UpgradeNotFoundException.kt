package me.zkingofkill.primecubes.exception

import me.zkingofkill.primecubes.cube.UpgradeType

class UpgradeNotFoundException(var upgradeType:String) : Exception("the upgrade from id $upgradeType was not found!") {
}