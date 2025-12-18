package app.zornslemma.mypricelog.debug

import android.util.Log

// At least early in development, check() and require() would sometimes kill the app but without
// leaving a clear logcat trace, making it very hard to figure out what went wrong. I am not 100%
// sure I didn't get confused, I am struggling to reproduce this now. Talking to ChatGPT/Grok
// suggests this really does happen but I am not entirely convinced they're right. I created these
// replacements with more explicit logging and they did seem to help, so I guess there's no harm in
// continuing to use them. I'm just not certain they are necessary. It's possible that having the
// Log.e() occur *before* an exception is thrown increases the chances the log entry makes it to
// logcat.

inline fun myCheck(condition: Boolean, lazyMessage: () -> String) {
    if (!condition) {
        val msg = lazyMessage()
        val ex = IllegalStateException(msg) // same as check()
        Log.e("MyCheck", "FAILED CHECK: $msg", ex)
        throw ex
    }
}

inline fun myRequire(condition: Boolean, lazyMessage: () -> String) {
    if (!condition) {
        val msg = lazyMessage()
        val ex = IllegalArgumentException(msg) // same as require()
        Log.e("MyRequire", "FAILED REQUIRE: $msg", ex)
        throw ex
    }
}
