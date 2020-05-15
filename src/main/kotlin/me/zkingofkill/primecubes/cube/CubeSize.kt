package me.zkingofkill.primecubes.cube
/*
    Tamanho de uma mina
 */
data class CubeSize(var xz:Double, var y:Double){

    override fun toString(): String {
        return "${xz.toInt()}x${y.toInt()}"
    }
}