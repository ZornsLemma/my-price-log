package app.zornslemma.mypricelog.ui.common

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

data class Versioned<T>(val version: Long, val value: T) {
    companion object {
        fun <T> initial(initialValue: T): Versioned<T> =
            Versioned(version = -1L, value = initialValue)
    }
}

fun <T> Flow<T>.withVersion(): Flow<Versioned<T>> = flow {
    var version = 0L
    collect { value ->
        emit(Versioned(version, value))
        version++
    }
}
