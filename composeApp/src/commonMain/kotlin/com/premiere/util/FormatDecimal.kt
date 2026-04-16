package com.premiere.util

@OptIn(ExperimentalStdlibApi::class)
fun Number.formatToString(decimals: Int = 1): String =
    "%.${decimals}f".format(this.toDouble())