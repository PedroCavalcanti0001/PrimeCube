package me.zkingofkill.primecubes.cube.upgrade

import fr.minuskube.inv.content.SlotPos

interface IUpgradeLevel {
    var level:Int
    var price:Double
    var slotPos:SlotPos
}