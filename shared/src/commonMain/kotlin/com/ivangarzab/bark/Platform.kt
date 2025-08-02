package com.ivangarzab.bark

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform