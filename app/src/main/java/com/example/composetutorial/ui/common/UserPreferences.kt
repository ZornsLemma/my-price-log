package com.example.composetutorial.ui.common

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.example.composetutorial.app.AppScope
import com.example.composetutorial.ui.common.userPreferencesStore
import com.google.protobuf.InvalidProtocolBufferException
import kotlinx.coroutines.launch
import java.io.InputStream
import java.io.OutputStream

// TODO: ChatGPT/Grok magic
object UserPreferencesSerializer : Serializer<UserPrefs.UserPreferences> {
    override val defaultValue: UserPrefs.UserPreferences = UserPrefs.UserPreferences.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): UserPrefs.UserPreferences {
        try {
            return UserPrefs.UserPreferences.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: UserPrefs.UserPreferences, output: OutputStream) {
        t.writeTo(output)
    }
}

// TODO: ChatGPT magic
val Context.userPreferencesStore: DataStore<UserPrefs.UserPreferences> by dataStore(
    fileName = "user_prefs.pb",
    serializer = UserPreferencesSerializer
)

suspend fun setSelectedDataSetId(context: Context, dataSetId: Long) {
    updateUserPreferences(context) { builder -> builder.setSelectedDataSetId(dataSetId) }
}

suspend fun setSelectedItemId(context: Context, dataSetId: Long, itemId: Long) {
    updateUserPreferences(context) { builder -> builder.putSelectedItemIdForDataSetId(dataSetId, itemId) }
}

suspend fun setSelectedSourceId(context: Context, dataSetId: Long, sourceId: Long) {
    updateUserPreferences(context) { builder -> builder.putSelectedSourceIdForDataSetId(dataSetId, sourceId) }
}

suspend fun updateUserPreferences(context: Context, update: (UserPrefs.UserPreferences.Builder) -> Unit) {
    context.userPreferencesStore.updateData { prefs ->
        prefs.toBuilder().apply(update).build() }
}

fun setSelectedDataSetIdAsync(context: Context, dataSetId: Long) {
    AppScope.io.launch {
        setSelectedDataSetId(context, dataSetId)
    }
}
fun setSelectedItemIdAsync(context: Context, dataSetId: Long, itemId: Long) {
    AppScope.io.launch {
        setSelectedItemId(context, dataSetId, itemId)
    }
}
fun setSelectedSourceIdAsync(context: Context, dataSetId: Long, sourceId: Long) {
    AppScope.io.launch {
        setSelectedSourceId(context, dataSetId, sourceId)
    }
}