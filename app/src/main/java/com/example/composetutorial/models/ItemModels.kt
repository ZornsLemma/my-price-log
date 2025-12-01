package com.example.composetutorial.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.composetutorial.debug.myCheck
import com.example.composetutorial.domain.MeasurementUnit
import com.example.composetutorial.domain.QuantityType
import com.example.composetutorial.domain.getRelevantMeasurementUnits
import kotlinx.parcelize.Parcelize

@Entity(
    tableName = "item", foreignKeys = [ForeignKey(
        entity = DataSet::class,
        parentColumns = ["id"],
        childColumns = ["data_set_id"],
        onDelete = ForeignKey.CASCADE
    )], indices = [Index(value = ["data_set_id"], unique = false)]
)
@Parcelize
data class Item(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "data_set_id") val dataSetId: Long,
    val name: String,
    // default_unit implicitly specifies the item's QuantityType. It also serves as the default unit
    // to use when the user is entering the first price for an (item, source) combination.
    @ColumnInfo(name = "default_unit") val defaultUnit: MeasurementUnit,
    @ColumnInfo(name = "allow_multipack") val allowMultipack: Boolean,
    val notes: String,
) : Parcelable

fun Item?.toEditable(dataSet: DataSet): EditableItem {
    val defaultUnitIdByQuantityTypeOrdinal = QuantityType.entries.map { quantityType ->
        dataSet.getRelevantMeasurementUnits(
            quantityType, includeDisplayOnly = false
        ).first().id
    }.toMutableList()
    if (this == null) {
        // It's probably reasonable to default to sold by weight, and it's nice not to have
        // the possibility of a null state.
        return EditableItem(
            0, dataSet.id, "", QuantityType.WEIGHT,
            defaultUnitIdByQuantityTypeOrdinal, false, ""
        )
    } else {
        myCheck(dataSet.id == dataSetId) {
            "Expected identical dataSetIds but have dataSet.id ${dataSet.id} and dataSetId $dataSetId"
        }
        defaultUnitIdByQuantityTypeOrdinal[defaultUnit.quantityType.ordinal] = defaultUnit.id
        return EditableItem(
            id,
            dataSet.id,
            name,
            defaultUnit.quantityType,
            defaultUnitIdByQuantityTypeOrdinal,
            allowMultipack,
            notes
        )
    }
}

// Note that we have the surprisingly horrific code around defaultUnitIdByQuantityTypeOrdinal instead
// of a simple "val defaultUnit: MeasurementUnit" because I thought it would be user-friendly to keep
// the selected unit for each quantity type while the user is editing, and then it turns into a
// nightmare of un-parcelizable types and working with ordinals and IDs rather than enum class
// objects themselves. It probably isn't that bad in hindsight, but the code is way more complex
// than feels necessary.
@Parcelize
data class EditableItem(
    val id: Long,
    val dataSetId: Long,
    val name: String,
    val quantityType: QuantityType,
    val defaultUnitIdByQuantityTypeOrdinal: List<Long>,
    val allowMultipack: Boolean,
    val notes: String,
) : Parcelable {
    val defaultUnit: MeasurementUnit
        get() = MeasurementUnit.fromId(
            defaultUnitIdByQuantityTypeOrdinal[quantityType.ordinal]
        )!!

    // TODO: I have had some intermittent crashes when on the "Edit product" screen and I put it in
    // background, adb kill it and then return to it via the overview menu. The error in logcat is
    // fairly consistently "java.lang.IllegalArgumentException: No enum constant
    // com.example.composetutorial.MeasurementUnit.ĭ????" with almost nothing helpful in the gigantic
    // stack backtrace. This does not seem very easy to reproduce, but has cropped up once or twice.
    // I really don't know what's going on. About all I can do is leave this note here to remind
    // me in case I spot something later or if this does go wrong again or to spend some more time
    // trying to reproduce this later. Now that I've rewritten the SavedStateHandle code there's
    // even more reason to suspect this is fine, but I never did actually find the problem and it's
    // just possible this is still lurking so I'll keep this comment for now.
}

fun EditableItem.toDomain(): Item? {
    val trimmedName = name.trim()
    // It could get confusing if an empty name leaked into the database (it would be
    // semi-invisible in the UI) so we'll check that here, even though we could generate an
    // Item with such a name and this is not really validation code - we expect to have been
    // called on a pre-validated EditableItem.
    if (trimmedName.isEmpty()) {
        return null
    }
    // This is a devCheck not a "return null" check because it indicates an internal error.
    myCheck(quantityType == defaultUnit.quantityType) {
        "Expected consistent quantity types on EditableItem but have $quantityType and $defaultUnit"
    }
    return Item(
        id = id,
        dataSetId = dataSetId,
        name = trimmedName,
        defaultUnit = defaultUnit,
        allowMultipack = allowMultipack,
        notes = notes
    )
}