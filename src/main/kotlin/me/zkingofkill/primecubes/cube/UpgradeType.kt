package me.zkingofkill.primecubes.cube

enum class UpgradeType {
    CYBORGFORTUNE, LOOT, CYBORGSPEED, LAYERS, SPEED, STORAGE;

    companion object {
        fun contains(name: String): Boolean {
            return values().map { it.name }.contains(name)
        }
    }
}