package app.zornslemma.mypricelog.app

import android.content.Context
import android.content.Intent

fun safeRestartApp(context: Context) {
    // ChatGPT told me to do this with AlarmManager, but that didn't work and I believe it isn't
    // the recommended approach on Android 10+. Perplexity recommended the following approach, which
    // feels like it has a subtle theoretical hole in it where the app might kill itself without
    // restarting, but I don't think it's possible to do better and in practice this probably won't
    // happen, and if it does it's a bit user-unfriendly but at least safe (the user just has to
    // restart the app manually).
    val packageManager = context.packageManager
    val launchIntent = packageManager.getLaunchIntentForPackage(context.packageName) ?: return
    val componentName = launchIntent.component ?: return
    val restartIntent = Intent.makeRestartActivityTask(componentName)
    context.startActivity(restartIntent)
    Runtime.getRuntime().exit(0)
}
