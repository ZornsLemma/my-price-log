package app.zornslemma.mypricelog.debug

import android.util.Log
import kotlinx.coroutines.delay

// debugDelay() calls left in the source code act as markers for places where it is
// useful to add an artificial delay to simulate slow actions. With no/0L arguments
// they should optimise down to nothing. During debugging we can supply a non-0 argument
// and remove it when we're finished.
suspend inline fun debugDelay(timeMillis: Long = 0L) {
    if (timeMillis != 0L) {
        Log.w("DebugDelay", "debugDelay($timeMillis)")
        delay(timeMillis)
    }
}
