package com.premiere.util

actual fun Number.formatToString(decimals: Int): String =
    "%.${decimals}f".format(this.toDouble())
