package me.zkingofkill.primecubes.exception

class CubePropsNotFoundException(var typeId:Int) : Exception("cube properties ID $typeId not found!") {
}