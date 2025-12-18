package app.zornslemma.mypricelog.ui.common

sealed interface LoadState<out T> {
    data object Loading : LoadState<Nothing>

    // Empty isn't currently used, but it feels like it might be a good option in some future case
    // so I'll keep it around for now. T could be a nullable type to represent this concept, but
    // depending on the precise situation Empty+a non-nullable T might be better.
    @Suppress("unused") data object Empty : LoadState<Nothing>

    data class Loaded<T>(val value: T) : LoadState<T>
}

fun <T> LoadState<T>.valueOrNull(): T? =
    when (this) {
        is LoadState.Loaded -> value
        else -> null
    }
