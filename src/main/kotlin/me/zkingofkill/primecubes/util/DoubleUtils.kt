package me.zkingofkill.primecubes.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

fun Double.format():String{
    var Local = Locale("pt","BR")
    var formatter = DecimalFormat("#,##0.00", DecimalFormatSymbols(Local))
    return formatter.format(this)
}