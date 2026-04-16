package com.premiere

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform