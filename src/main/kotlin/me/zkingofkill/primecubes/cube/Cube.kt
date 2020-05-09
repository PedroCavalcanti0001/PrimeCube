package me.zkingofkill.primecubes.cube

class Cube(var id: Int,
           var owner: String,
           var cubePosition: CubePosition,
           var cubeSize: CubeSize,
           var life: Double,
           var cubeBlocks: ArrayList<CubeBlock>,
           var upgrades: HashMap<Int, Int>) {


    fun save() {

    }

    companion object {
        fun cPropsById(id: Int): CubeProps? {
            return CubeProps.list().find { it.id == id }
        }
    }
}