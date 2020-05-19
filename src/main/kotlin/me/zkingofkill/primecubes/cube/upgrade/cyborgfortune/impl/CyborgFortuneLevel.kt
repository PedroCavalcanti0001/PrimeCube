package me.zkingofkill.primecubes.cube.upgrade.cyborgfortune.impl

import me.zkingofkill.primecubes.cube.upgrade.cyborgfortune.ICyborgFortuneLevel

class CyborgFortuneLevel(override var level: Int,
                         override var price: Double,
                         override var multiply: Int,
                         override var chance: Double
) : ICyborgFortuneLevel {
}