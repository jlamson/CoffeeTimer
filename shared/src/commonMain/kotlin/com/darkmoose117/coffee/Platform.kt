package com.darkmoose117.coffee

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
