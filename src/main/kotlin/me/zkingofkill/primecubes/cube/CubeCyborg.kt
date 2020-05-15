package me.zkingofkill.primecubes.cube

class CubeCyborg(var cube: Cube) {

    val enabled = {
        cube.upgrades.containsKey(UpgradeType.CYBORGSPEED)
    }.invoke()

    fun spawn() {

        if(enabled){

        }
    }

    fun despawn() {

    }
}