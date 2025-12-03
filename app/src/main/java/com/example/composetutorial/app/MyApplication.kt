package com.example.composetutorial.app

import android.app.Application
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.room.withTransaction
import com.example.composetutorial.domain.dataStore
import com.example.composetutorial.data.AppDatabase
import com.example.composetutorial.data.RepositoryImpl
import com.example.composetutorial.data.populateDemoData
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

// TODO: Lots of AI voodoo here, probably worth reading up later on how MyApplication should behave
// and what it maybe ought to be doing.
class MyApplication : Application() {
    val repository: RepositoryImpl by lazy {
        val db = AppDatabase.getDatabase(this)
        RepositoryImpl(
            db,
            db.dataSetDao(),
            db.productDao(),
            db.sourceDao(),
            db.priceDao(),
            db.priceHistoryDao()
        )
    }

    // TODO: ChatGPT magic, needs checking. May also be worth investigating ACRA/Cockroach/SimpleCrashReport open source libraries.
    // I am fairly sure this specific code is utterly useless anyway. Oddly enough it appears to *hide* a failing check() and
    // commenting it out again restores the crash, but when this code is in place I *don't* seem to get the GlobalExceptionHandler
    // logcat entry. So it seems doubly useless, in that it *hides* crashes somehow!? Really confused.
    /*
    override fun onCreate() {
        super.onCreate()

        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e("GlobalExceptionHandler", "Uncaught exception", throwable)

            val exceptionMessage = throwable.message ?: "No details"
            val stackTrace = throwable.stackTrace.joinToString("\n").take(500) // limit length

            Handler(Looper.getMainLooper()).post {
                val message = "The app crashed:\n$exceptionMessage\n\n" +
                        "Stack trace:\n$stackTrace\n\n" +
                        "Please take a screenshot or copy this info."

                // You need a way to get current activity or fallback context
                /* TODO: I need to define CurrentActivityHolder myself!? Seems complex and I'll just hack this for now and check those libraries out later
                val activity = CurrentActivityHolder.currentActivity
                if (activity != null) {
                    AlertDialog.Builder(activity)
                        .setTitle("Unexpected error")
                        .setMessage(message)
                        .setPositiveButton("OK") { _, _ ->
                            android.os.Process.killProcess(android.os.Process.myPid())
                            exitProcess(1)
                        }
                        .setCancelable(false)
                        .show()
                } else { */
                    // fallback: just kill app
                    android.os.Process.killProcess(android.os.Process.myPid())
                    exitProcess(1)
                /* }*/
            }
        }
    }
    */
    /*
    // TODO: Simpler ChatGPT magic
    override fun onCreate() {
        super.onCreate()

        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e("UncaughtException", "Uncaught exception in thread ${thread.name}", throwable)
        }
    }
    */

    override fun onCreate() {
        super.onCreate()

        AppScope.io.launch {
            val demoDataInsertedKey = booleanPreferencesKey("demo_data_inserted")
            val demoDataInserted = dataStore.data
                .map { prefs -> prefs[demoDataInsertedKey] ?: false }
                .first()
            if (!demoDataInserted) {
                // To guard against the corner case where the demo data transaction succeeded but
                // we were killed before setting demoDataInsertedKey to true, we don't actually
                // do anything here if there are any data sets.
                if (repository.getAllDataSets().first().isEmpty()) {
                    val db = AppDatabase.getDatabase(this@MyApplication)

                    db.withTransaction {
                        // Manually adjust the starting sequence values for various tables. This
                        // increases the chances that foreign key bugs cause constraint violations,
                        // rather than silently referencing the wrong record. It also makes it easier to
                        // identify the type of ID during debugging based on its numeric range. We don't
                        // rely on IDs being non-overlapping for correctness.
                        //
                        // We leave data_set's sequence alone and let it start IDs at 1.
                        db.openHelper.writableDatabase.execSQL("INSERT INTO sqlite_sequence (name, seq) VALUES ('source', 1000)")
                        db.openHelper.writableDatabase.execSQL("INSERT INTO sqlite_sequence (name, seq) VALUES ('item', 2000)")
                        db.openHelper.writableDatabase.execSQL("INSERT INTO sqlite_sequence (name, seq) VALUES ('price', 10000)")
                        db.openHelper.writableDatabase.execSQL("INSERT INTO sqlite_sequence (name, seq) VALUES ('price_history', 100000)")

                        populateDemoData(repository, this@MyApplication)
                    }
                }

                dataStore.edit() { it[demoDataInsertedKey] = true }
            }
        }
    }
}
