package com.example.composetutorial.ui.common

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

data class Versioned<T>(
    val version: Long,
    val value: T
)

fun <T> Flow<T>.withVersion(): Flow<Versioned<T>> = flow {
    var version = 0L
    collect { value ->
        emit(Versioned(version, value))
        version++
    }
}

// TODO: SHOULD THIS BE A COMPANION OBJECT FUNCTION OR SOMETHING?
fun <T> initialVersioned(initialValue: T): Versioned<T> =
    Versioned(version = -1L, value = initialValue)
