package app.zornslemma.mypricelog.data

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import app.zornslemma.mypricelog.debug.myCheck
import app.zornslemma.mypricelog.domain.MeasurementUnit
import app.zornslemma.mypricelog.domain.QuantityType
import app.zornslemma.mypricelog.domain.getRelevantMeasurementUnits
import kotlinx.parcelize.Parcelize

@Entity(
    tableName = "item",
    foreignKeys =
        [
            ForeignKey(
                entity = DataSet::class,
                parentColumns = ["id"],
                childColumns = ["data_set_id"],
                onDelete = ForeignKey.CASCADE,
            )
        ],
    indices = [Index(value = ["data_set_id"], unique = false)],
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
    val defaultUnitByQuantityType =
        QuantityType.entries.associateWithTo(mutableMapOf()) { quantityType ->
            dataSet.getRelevantMeasurementUnits(quantityType, includeDisplayOnly = false).first()
        }
    if (this == null) {
        // It's probably reasonable to default to sold by weight, and it's nice not to have
        // the possibility of a null state.
        return EditableItem(
            0,
            dataSet.id,
            "",
            QuantityType.WEIGHT,
            defaultUnitByQuantityType,
            false,
            "",
        )
    } else {
        myCheck(dataSet.id == dataSetId) {
            "Expected identical dataSetIds but have dataSet.id ${dataSet.id} and dataSetId $dataSetId"
        }
        defaultUnitByQuantityType[defaultUnit.quantityType] = defaultUnit
        return EditableItem(
            id,
            dataSet.id,
            name,
            defaultUnit.quantityType,
            defaultUnitByQuantityType,
            allowMultipack,
            notes,
        )
    }
}

@Parcelize
data class EditableItem(
    val id: Long,
    val dataSetId: Long,
    val name: String,
    val quantityType: QuantityType,
    val defaultUnitByQuantityType: Map<QuantityType, MeasurementUnit>,
    val allowMultipack: Boolean,
    val notes: String,
) : Parcelable {
    val defaultUnit: MeasurementUnit
        get() = defaultUnitByQuantityType[quantityType]!!
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
        notes = notes,
    )
}
