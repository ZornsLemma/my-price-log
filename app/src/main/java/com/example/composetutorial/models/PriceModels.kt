package com.example.composetutorial.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.composetutorial.CurrencyFormat
import com.example.composetutorial.DataSet
import com.example.composetutorial.Item
import com.example.composetutorial.MeasuredValue
import com.example.composetutorial.MeasurementUnit
import com.example.composetutorial.Source
import com.example.composetutorial.baseUnitForQuantityType
import com.example.composetutorial.devCheck
import com.example.composetutorial.formatDoubleForEditing
import com.example.composetutorial.parseStringAsDoubleOrNull
import kotlinx.parcelize.Parcelize
import java.time.Instant
import java.util.Locale

@Entity(
    tableName = "price",
    foreignKeys = [ForeignKey(
        entity = DataSet::class,
        parentColumns = ["id"],
        childColumns = ["data_set_id"],
        onDelete = ForeignKey.CASCADE
    ), ForeignKey(
        entity = Item::class,
        parentColumns = ["id"],
        childColumns = ["item_id"],
        onDelete = ForeignKey.CASCADE
    ), ForeignKey(
        entity = Source::class,
        parentColumns = ["id"],
        childColumns = ["source_id"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [
        Index(value = ["data_set_id"], unique = false), // just because this is a foreign key
        // We don't include data_set_id here because although some queries specify it along with item_id, it's just belt-and-braces - item_id already implies a data_set_id if all is well.
        Index(value = ["item_id"], unique = false),
        Index(value = ["source_id"], unique = false),
        // TODO: I need to remember to manually apply this index to my own "production" db on O6.
        // We put item_id first in this index as it's likely to be more selective than source_id and
        // ENHANCE: it may allow us to remove the index on item_id by itself later on. This index is
        // not just for efficiency; it will also prevent data corruption if a bug causes us to try
        // to insert more than one price for an (item, source) pair.
        Index(value = ["item_id", "source_id"], unique = true),
    ]
)
data class PriceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "data_set_id") val dataSetId: Long,
    @ColumnInfo(name = "item_id") val itemId: Long,
    @ColumnInfo(name = "source_id") val sourceId: Long,

    // The item is sold for "price" per "count"*"quantity_in_base_unit", e.g. £1.42 for 1x500g.
    //
    // We use floating point for the price - it saves worrying about storing in pence or the
    // currency's equivalent and then getting in a mess if somehow the conventional number of
    // decimal places changes. For the kinds of prices we are representing and the limited amount of
    // calculation we are doing on them, there should in practice be no problems at all, as long as
    // we round to the relevant number of decimal places on display.
    //
    // "quantity_in_base_unit" is stored in the metric base unit associated with the item_id's
    // quantity_type. This avoids having to do bulk database updates if the user wants to change
    // unit conventions - this could happen even within a measurement system if shops switch to
    // marking pack sizes in ounces instead of lbs, for example. We use floating point because it
    // allows us to round-trip non-metric measures perfectly (provided we round them for display),
    // and it doesn't seem to have any real downside in practice.
    val price: Double,
    val count: Long,
    @ColumnInfo(name = "quantity_in_base_unit") val quantityInBaseUnit: Double,

    // Although quantity is stored in the base unit, we also record the actual unit the user entered
    // the price in. This allows us to show it back to them in the most natural form when they are
    // e.g. comparing the database price with the current shelf price. We do have a default unit
    // stored on the item, but tracking it per actual price allows us to handle situations where
    // supermarket A sells milk in pint multiples while supermarket B sells it in litre multiples.
    @ColumnInfo(name = "user_unit") val userUnit: MeasurementUnit,

    @ColumnInfo(name = "confirmed_at") val confirmedAt: Instant,

    val notes: String,

    // modifiedAt is borderline redundant here, but it feels generally neater to have it here as
    // well as on PriceHistory and probably simplifies things.
    @ColumnInfo(name = "modified_at") val modifiedAt: Instant,
)

// ENHANCE: PriceWithItem is arguably redundant now - given we have an original_unit on each price,
// that effectively tells us the quantity type implicitly and we don't need to join to item to get
// it. However, I suspect it still has some value because it allows us to do a bit of extra
// validation which may catch bugs. My inclination is to keep it for now, since the code already
// exists, and perhaps refactor to remove this at some point in the future.
data class PriceWithItemEntity(
    @Embedded val priceEntity: PriceEntity,
    @ColumnInfo(name = "default_unit") val itemDefaultUnit: MeasurementUnit,
)

fun PriceWithItemEntity.toDomain(): Price {
    // I have checks like this in various places but this is probably a pretty solid place for one.
    // On the way from database->domain, this is where we have a "solid" itemDefaultUnit value
    // (because it came from a database join) and that gives us an independent cross-check that
    // priceEntity.userUnit is of the right QuantityType.
    devCheck(priceEntity.userUnit.quantityType == itemDefaultUnit.quantityType) {
        "Expected consistent units on PriceWithItemEntity but we have userUnit " + 
        "${priceEntity.userUnit} and itemDefaultUnit $itemDefaultUnit"
    }
    return Price(
        id = priceEntity.id,
        dataSetId = priceEntity.dataSetId,
        itemId = priceEntity.itemId,
        sourceId = priceEntity.sourceId,
        price = priceEntity.price,
        count = priceEntity.count,
        quantity = MeasuredValue(
            priceEntity.quantityInBaseUnit,
            baseUnitForQuantityType(priceEntity.userUnit.quantityType)
        ).to(priceEntity.userUnit),
        confirmedAt = priceEntity.confirmedAt,
        notes = priceEntity.notes,
        modifiedAt = priceEntity.modifiedAt,
        itemDefaultUnit = itemDefaultUnit,
    )
}

// Price is a domain-level class which is nice for us to work with, once we've got away from the
// database layer.
@Parcelize
data class Price(
    val id: Long = 0,
    val dataSetId: Long,
    val itemId: Long,
    val sourceId: Long,
    val count: Long,
    val price: Double,
    val quantity: MeasuredValue,
    val confirmedAt: Instant,
    val notes: String,
    val modifiedAt: Instant,
    // itemDefaultUnit is a copy of the defaultUnit from the Item when we originally read the
    // PriceWithItemEntity in from the database. It is intended to allow a best effort (protecting
    // against buggy code, not malicious code) validation that when we write back to the database,
    // quantity hasn't somehow mutated into a different QuantityType.
    val itemDefaultUnit: MeasurementUnit
) : Parcelable

fun Price.toEntity(): PriceEntity {
    // This check is just a more explicit version of that implicitly done inside the
    // quantity.asValue() call below.
    devCheck(quantity.unit.quantityType == itemDefaultUnit.quantityType) {
        "Expected consistent quantity type when converting Price to PriceEntity but found " + 
        "measure $quantity with itemDefaultUnit $itemDefaultUnit"
    }
    return PriceEntity(
        id = id,
        dataSetId = dataSetId,
        itemId = itemId,
        sourceId = sourceId,
        price = price,
        count = count,
        quantityInBaseUnit = quantity.asValue(baseUnitForQuantityType(itemDefaultUnit.quantityType)),
        userUnit = quantity.unit,
        confirmedAt = confirmedAt,
        notes = notes,
        modifiedAt = modifiedAt,
    )
}

fun Price.toEditable(locale: Locale, currencyFormat: CurrencyFormat): EditablePrice = EditablePrice(
    id = id,
    dataSetId = dataSetId,
    itemId = itemId,
    sourceId = sourceId,
    count = count.toString(),
    price = formatDoubleForEditing(
        price,
        minDecimals = currencyFormat.decimalPlaces,
        maxDecimals = currencyFormat.decimalPlaces,
        locale
    ),
// Rounding is particularly important here - for non-metric measures, which are stored in
// doubles in metric base units in the database, if we didn't round we could end up with
// some visible noise in the least significant decimal places.
    measureValue = formatDoubleForEditing(
        quantity.value, minDecimals = 0, maxDecimals = quantity.unit.maxDecimals, locale
    ),
    measurementUnit = quantity.unit,
    confirmedAt = confirmedAt,
    toConfirm = false,
    notes = notes,
    itemDefaultUnit = itemDefaultUnit
)

// A version of Price we can use while editing - it holds the same basic information but with mostly
// string representations for editability.
@Parcelize
data class EditablePrice(
    val id: Long,
    val dataSetId: Long,
    val itemId: Long,
    val sourceId: Long,
    val price: String,
    val count: String,
    val measureValue: String,
    val measurementUnit: MeasurementUnit,
    val confirmedAt: Instant,
    val toConfirm: Boolean,
    val notes: String,
    val itemDefaultUnit: MeasurementUnit,

    ) : Parcelable {

    // Constructor for adding the first price for a (source, item) combination - we have the
    // "parent" fields, but everything else starts off blank/default.
    // TODO: Is this a valid reason to use a contructor in Kotlin?
    constructor(
        dataSetId: Long, itemId: Long, sourceId: Long, itemDefaultUnit: MeasurementUnit
    ) : this(
        id = 0,
        dataSetId = dataSetId,
        itemId = itemId,
        sourceId = sourceId,
        price = "",
        count = "",
        measureValue = "",
        measurementUnit = itemDefaultUnit,
        confirmedAt = Instant.now(),
        toConfirm = true,
        notes = "",
        itemDefaultUnit = itemDefaultUnit
    )
}

// TODO: Tempish note - EditablePrice is a sort of "variant domain" class just for editing - we
// need to convert it to the "primary" domain class Price here. This name might be confusing
// all the same, as we are approaching domain from the opposite side to a toDomain() on an
// entity class
fun EditablePrice.toDomain(locale: Locale): Price? {
    val priceDouble = parseStringAsDoubleOrNull(locale, price)
    // If we are adding a first price for a non-multipack item, count may be an empty string and
    // we interpret that as 1. It's possible that at some point we will also allow count to be
    // empty for multipack items and interpret that as one, although currently the validation
    // will prevent us getting this far in that case. trim() is used to help with that case,
    // even though it's currently not strictly necessary.
    val countLong =
        if (count.trim().isEmpty()) 1L else parseStringAsDoubleOrNull(locale, count)?.toLong()
    val measureValueDouble = parseStringAsDoubleOrNull(locale, measureValue)
    return if (priceDouble == null || countLong == null || measureValueDouble == null) {
        null
    } else {
        val now = Instant.now()
        Price(
            id = id,
            dataSetId = dataSetId,
            itemId = itemId,
            sourceId = sourceId,
            price = priceDouble,
            count = countLong,
            quantity = MeasuredValue(measureValueDouble, measurementUnit),
            confirmedAt = if (toConfirm) now else confirmedAt,
            notes = notes,
            modifiedAt = now,
            itemDefaultUnit = itemDefaultUnit,
        )
    }
}
