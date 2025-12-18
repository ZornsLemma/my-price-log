package app.zornslemma.mypricelog.app

import android.app.Application
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.room.withTransaction
import app.zornslemma.mypricelog.data.AppDatabase
import app.zornslemma.mypricelog.data.RepositoryImpl
import app.zornslemma.mypricelog.data.populateDemoData
import app.zornslemma.mypricelog.domain.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MyApplication : Application() {
    val repository: RepositoryImpl by lazy {
        val db = AppDatabase.getDatabase(this)
        RepositoryImpl(
            db,
            db.dataSetDao(),
            db.productDao(),
            db.sourceDao(),
            db.priceDao(),
            db.priceHistoryDao(),
        )
    }

    override fun onCreate() {
        super.onCreate()

        AppScope.io.launch {
            val demoDataInsertedKey = booleanPreferencesKey("demo_data_inserted")
            val demoDataInserted =
                dataStore.data.map { prefs -> prefs[demoDataInsertedKey] ?: false }.first()
            if (!demoDataInserted) {
                // To guard against the corner case where the demo data transaction succeeded but
                // we were killed before setting demoDataInsertedKey to true, we don't actually
                // do anything here if there are any data sets.
                if (repository.getAllDataSets().first().isEmpty()) {
                    val db = AppDatabase.getDatabase(this@MyApplication)

                    db.withTransaction {
                        // Manually adjust the starting sequence values for various tables. This
                        // increases the chances that foreign key bugs cause constraint violations,
                        // rather than silently referencing the wrong record. It also makes it
                        // easier to identify the type of ID during debugging based on its numeric
                        // range. We don't rely on IDs being non-overlapping for correctness.
                        //
                        // We leave data_set's sequence alone and let it start IDs at 1.
                        db.openHelper.writableDatabase.execSQL(
                            "INSERT INTO sqlite_sequence (name, seq) VALUES ('source', 1000)"
                        )
                        db.openHelper.writableDatabase.execSQL(
                            "INSERT INTO sqlite_sequence (name, seq) VALUES ('item', 2000)"
                        )
                        db.openHelper.writableDatabase.execSQL(
                            "INSERT INTO sqlite_sequence (name, seq) VALUES ('price', 10000)"
                        )
                        db.openHelper.writableDatabase.execSQL(
                            "INSERT INTO sqlite_sequence (name, seq) VALUES ('price_history', 100000)"
                        )

                        populateDemoData(repository, this@MyApplication)
                    }
                }

                dataStore.edit { it[demoDataInsertedKey] = true }
            }
        }
    }
}
