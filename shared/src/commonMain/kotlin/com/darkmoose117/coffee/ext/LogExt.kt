package com.darkmoose117.coffee.ext

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import org.lighthousegames.logging.KmLog
import org.lighthousegames.logging.logging

fun <T> Flow<T>.logOnEach(
    log: KmLog = logging(),
    message: ((T) -> Any?)? = null
): Flow<T> = this.onEach {
    log.debug {
        message?.invoke(it) ?: "$it"
    }
}