package me.zkingofkill.primecubes.exception

import me.zkingofkill.primecubes.cube.UpgradeType

class UpgradeNotFoundException(var upgradeType:UpgradeType) : Exception("the upgrade from id $upgradeType was not found!") {
}