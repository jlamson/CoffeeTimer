package com.darkmoose117.coffeetimer

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform