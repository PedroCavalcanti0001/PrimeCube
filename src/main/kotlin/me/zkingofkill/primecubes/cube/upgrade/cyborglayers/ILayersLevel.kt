package me.zkingofkill.primecubes.cube.upgrade.cyborglayers

import me.zkingofkill.primecubes.cube.upgrade.IUpgradeLevel

interface ILayersLevel : IUpgradeLevel{
    override var level: Int
    override var price: Double

    var sections: Int
}