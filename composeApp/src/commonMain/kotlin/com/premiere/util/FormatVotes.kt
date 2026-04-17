package com.premiere.util

fun Int?.formatVotes(): String {
    if (this == null) return "-"
    if (this >= 1_000_000) return "${(this / 1_000_000f).formatToString(1)}M"
    if (this >= 1_000) return "${(this / 1_000f).formatToString(0)}K"
    return this.toString()
}
