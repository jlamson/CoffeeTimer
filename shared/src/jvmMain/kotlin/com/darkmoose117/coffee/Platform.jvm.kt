package com.darkmoose117.coffee

class JvmPlatform : Platform {
    override val name: String = "Jvm: ${System.getenv()}"
}

actual fun getPlatform(): Platform = JvmPlatform()