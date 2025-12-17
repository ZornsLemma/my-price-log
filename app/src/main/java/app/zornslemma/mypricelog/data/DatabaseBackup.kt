package app.zornslemma.mypricelog.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.util.Log
import app.zornslemma.mypricelog.R
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

private const val TAG = "DatabaseBackup"

fun backupDatabase(context: Context, targetUri: Uri) {
    val db = AppDatabase.getDatabase(context)
    // The next line is voodoo which Grok suggested "might" be necessary and ChatGPT seemed to
    // agree there could be borderline cases. I am not convinced but I guess it's likely harmless
    // at worst.
    db.openHelper.writableDatabase.query("PRAGMA wal_checkpoint(FULL)")
    val dbPath =
        checkNotNull(db.openHelper.writableDatabase.path) { "Expected non-null database path" }

    // Use a temp file to dump to.
    // ENHANCE: Could/should we take steps to try to delete this afterwards if an exception occurs?
    // I imagine it isn't too critical as we will have at most one temp file and thus at worst we
    // double the size of our data storage, and our database isn't likely to be that big in the
    // first place. It's probably as simple as a try-finally block though, just needs a bit of
    // testing.
    val backupFile = File(context.cacheDir, "backup_temp.db")

    // VACUUM INTO the temp file. I tried using copy() but the WAL files make this unreliable, and
    // based on discussions with both ChatGPT and Grok there is no simple workaround. VACUUM INTO
    // needs a minSdk>=30 and if that proves annoying, it might be worth investigating the rather
    // tricksy alternatives later on.
    val rawDb = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE)
    rawDb.execSQL("VACUUM INTO '${backupFile.absolutePath.replace("'", "''")}'")
    rawDb.close()

    // Copy the temp file to the user-selected URI.
    context.contentResolver.openOutputStream(targetUri)?.use { output ->
        FileInputStream(backupFile).use { input -> input.copyTo(output) }
    }

    backupFile.delete() // Clean up temp file
}

fun restoreDatabase(context: Context, sourceUri: Uri) {
    val dbFile = context.getDatabasePath(DB_NAME)

    // Create a temp file from the URI.
    val tempFile = File(context.cacheDir, "temp_backup.db")
    try {
        // Copy sourceUri to temp file.
        context.contentResolver.openInputStream(sourceUri)?.use { input ->
            FileOutputStream(tempFile).use { output -> input.copyTo(output) }
        }
            ?: throw IOException(
                context.getString(R.string.message_failed_to_open_input_stream_for_uri, sourceUri)
            )

        // Validate the backup file.
        checkDatabaseRestoreCandidate(context, tempFile.path)

        // The backup file is OK, so we'll go ahead and overwrite our internal database now.

        // Close Room to avoid conflicts.
        AppDatabase.clearInstance()

        // Delete existing database files for clean slate. I don't know if this is necessary but at
        // one point Grok suggested this might be useful to avoid old SHM/WAL files hanging around
        // and confusing things. I don't think this will hurt so let's be cautious.
        context.deleteDatabase(DB_NAME)

        // Copy tempFile to internal database location.
        FileInputStream(tempFile).use { input ->
            FileOutputStream(dbFile, false).use { output -> input.copyTo(output) }
        }
    } finally {
        tempFile.delete() // Clean up temp file
    }
}

private fun checkDatabaseRestoreCandidate(context: Context, dbPath: String) {
    val db = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY)
    db.use { db ->
        val version = db.version
        if (version > DB_VERSION) {
            throw IllegalStateException(
                context.getString(R.string.message_database_to_restore_too_new, version, DB_VERSION)
            )
        }

        // Sanity check this isn't a database from some other random app. We're not trying to guard
        // against malicious inputs here, just the user accidentally picking the wrong database.
        val expectedTables = listOf("data_set", "item", "price", "price_history", "source")
        expectedTables.forEach { table ->
            val cursor =
                db.rawQuery(
                    "SELECT name FROM sqlite_master WHERE type='table' AND name=?",
                    arrayOf(table),
                )
            cursor.use { cursor ->
                val tableExists = cursor.moveToFirst()
                Log.d(TAG, "tableExists $table: $tableExists")
                if (!tableExists) {
                    throw IllegalStateException(
                        context.getString(
                            R.string.message_the_database_to_restore_was_not_created_with_this_app
                        )
                    )
                }
            }
        }
    }
}
