package app.zornslemma.mypricelog.app

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

// Using AppScope.io signals: "This is an app-wide background task that must be allowed to complete
// without being cancelled when the user leaves the screen, and it *will not* touch any UI or
// short-lived objects which may have gone out of scope." This is safe and desired for things like
// DataStore updates. Practically speaking, CoroutineScope(Dispatchers.IO).launch {} is equivalent
// in behaviour (new scope per call, GC'd safely), but AppScope.io better documents intent and
// avoids allocation.
object AppScope {
    val io = CoroutineScope(Dispatchers.IO + SupervisorJob())
}
