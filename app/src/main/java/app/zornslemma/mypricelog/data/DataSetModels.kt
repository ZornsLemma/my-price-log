package app.zornslemma.mypricelog.data

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import app.zornslemma.mypricelog.domain.UnitFamily
import app.zornslemma.mypricelog.ui.common.currencyOrNull
import java.util.Locale
import kotlinx.parcelize.Parcelize

@Entity(tableName = "data_set")
@Parcelize
data class DataSet(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    @ColumnInfo(name = "currency_code") val currencyCode: String,
    // ENHANCE: For now, I think I will ask the system to format currencies using the currency_code.
    // Later on we may want to give DataSet a "use system formatting" flag and some parameters
    // (currency prefix/suffix/decimal places) which the user can specify to override the system
    // formatting. I think it may be that e.g. the system formatting of USD when in a GBP locale may
    // be a bit annoying ("US$ 123.00" instead of "$123.00" perhaps - not tested though) so this
    // extension is not necessarily ridiculous, but let's keep it simple for now. Having the option
    // to use system formatting is good, and that will probably always be the default.
    @ColumnInfo(name = "allow_metric") val allowMetric: Boolean,
    @ColumnInfo(name = "allow_imperial") val allowImperial: Boolean,
    @ColumnInfo(name = "allow_us_customary") val allowUSCustomary: Boolean,
    val notes: String,
) : Parcelable

fun DataSet?.toEditable(locale: Locale): EditableDataSet {
    if (this == null) {
        val defaultUnitFamilies = getDefaultUnitFamilies(locale)
        return EditableDataSet(
            0,
            "",
            locale.currencyOrNull()?.currencyCode ?: "",
            UnitPreferences(
                allowMetric = UnitFamily.METRIC in defaultUnitFamilies,
                allowImperial = UnitFamily.IMPERIAL in defaultUnitFamilies,
                allowUSCustomary = UnitFamily.US_CUSTOMARY in defaultUnitFamilies,
            ),
            notes = "",
        )
    } else {
        return EditableDataSet(
            id = id,
            name = name,
            currencyCode = currencyCode,
            unitPreferences =
                UnitPreferences(
                    allowMetric = allowMetric,
                    allowImperial = allowImperial,
                    allowUSCustomary = allowUSCustomary,
                ),
            notes = notes,
        )
    }
}

fun getDefaultUnitFamilies(locale: Locale): Set<UnitFamily> =
    when (locale.country.uppercase()) {
        // ChatGPT suggests it's common to have dual metric and US customary labelling in US
        // supermarkets and that some users may want to use metric, so we enable it by default. I'll
        // do the same for Liberia and Myanmar too for now.
        "US",
        "LR",
        "MM" -> setOf(UnitFamily.ITEM, UnitFamily.METRIC, UnitFamily.US_CUSTOMARY)
        "GB" -> setOf(UnitFamily.ITEM, UnitFamily.METRIC, UnitFamily.IMPERIAL)
        else -> setOf(UnitFamily.ITEM, UnitFamily.METRIC)
    }

@Parcelize
data class UnitPreferences(
    val allowMetric: Boolean,
    val allowImperial: Boolean,
    val allowUSCustomary: Boolean,
) : Parcelable

@Parcelize
data class EditableDataSet(
    val id: Long,
    val name: String,
    val currencyCode: String,
    val unitPreferences: UnitPreferences,
    val notes: String,
) : Parcelable

fun EditableDataSet.toDomain(): DataSet? {
    val trimmedName = name.trim()
    // It could get confusing if an empty name leaked into the database (it would be
    // semi-invisible in the UI) so we'll check that here, even though we could generate a
    // Source with such a name and this is not really validation code - we expect to have been
    // called on a pre-validated EditableSource.
    if (trimmedName.isEmpty()) {
        return null
    }
    return DataSet(
        id = id,
        name = trimmedName,
        currencyCode = currencyCode,
        allowMetric = unitPreferences.allowMetric,
        allowImperial = unitPreferences.allowImperial,
        allowUSCustomary = unitPreferences.allowUSCustomary,
        notes = notes,
    )
}
