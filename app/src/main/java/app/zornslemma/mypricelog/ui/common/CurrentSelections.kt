package app.zornslemma.mypricelog.ui.common

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import app.zornslemma.mypricelog.app.AppScope
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream
import kotlinx.coroutines.launch

object CurrentSelectionsSerializer : Serializer<UserPrefs.CurrentSelections> {
    override val defaultValue: UserPrefs.CurrentSelections =
        UserPrefs.CurrentSelections.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): UserPrefs.CurrentSelections {
        try {
            return UserPrefs.CurrentSelections.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: UserPrefs.CurrentSelections, output: OutputStream) {
        t.writeTo(output)
    }
}

val Context.userPreferencesStore: DataStore<UserPrefs.CurrentSelections> by
    dataStore(fileName = "user_prefs.pb", serializer = CurrentSelectionsSerializer)

suspend fun setSelectedDataSetId(context: Context, dataSetId: Long) {
    updateCurrentSelections(context) { builder -> builder.setSelectedDataSetId(dataSetId) }
}

suspend fun setSelectedItemId(context: Context, dataSetId: Long, itemId: Long) {
    updateCurrentSelections(context) { builder ->
        builder.putSelectedItemIdForDataSetId(dataSetId, itemId)
    }
}

suspend fun setSelectedSourceId(context: Context, dataSetId: Long, sourceId: Long) {
    updateCurrentSelections(context) { builder ->
        builder.putSelectedSourceIdForDataSetId(dataSetId, sourceId)
    }
}

suspend fun updateCurrentSelections(
    context: Context,
    update: (UserPrefs.CurrentSelections.Builder) -> Unit,
) {
    context.userPreferencesStore.updateData { prefs -> prefs.toBuilder().apply(update).build() }
}

fun setSelectedDataSetIdAsync(context: Context, dataSetId: Long) {
    AppScope.io.launch { setSelectedDataSetId(context, dataSetId) }
}

fun setSelectedItemIdAsync(context: Context, dataSetId: Long, itemId: Long) {
    AppScope.io.launch { setSelectedItemId(context, dataSetId, itemId) }
}

fun setSelectedSourceIdAsync(context: Context, dataSetId: Long, sourceId: Long) {
    AppScope.io.launch { setSelectedSourceId(context, dataSetId, sourceId) }
}
