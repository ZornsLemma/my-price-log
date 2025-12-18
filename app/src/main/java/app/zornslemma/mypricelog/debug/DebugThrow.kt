package app.zornslemma.mypricelog.debug

// debugThrow() calls left in the source code act as markers for places where it is useful to throw
// an exception to simulate a failure. With null arguments they should optimise down to nothing.
// During debugging we can supply a non-null argument and remove it when we're finished.
@Suppress("NOTHING_TO_INLINE")
inline fun debugThrow(message: String? = null) {
    if (message != null) {
        throw IllegalStateException(message)
    }
}
