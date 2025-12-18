package app.zornslemma.mypricelog.data

import android.os.Parcelable
import androidx.annotation.StringRes
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import app.zornslemma.mypricelog.R
import app.zornslemma.mypricelog.common.formatDoubleForEditing
import app.zornslemma.mypricelog.common.parseStringAsDoubleOrNull
import app.zornslemma.mypricelog.debug.myCheck
import java.util.Locale
import kotlinx.parcelize.Parcelize

// The to*() functions here contains logic to convert loyalty percentages to/from price multipliers.
// 5% bonus is not the same as 5% discount/cashback. Suppose we want to buy something costing £100:
// - If there is a 5% discount, the price is £95 and we hand over £95.
// - If we get 5% cashback, we hand over £100 and get £5 back, so £95 net.
// - If we get 5% bonus in some "store account", we need to deposit £95.24 and the 5% bonus makes
//   that up to the £100 we need.
// - If we get 5% bonus as points on our spending, we theoretically spend £95.24, get 5% bonus as
//   points and that makes up the £100 we need. (In reality you can't do this, but I think in the
//   long term it works out as if you can.)
//
// I think an alternative but equivalent way to look at this is that with cashback, we can spend the
// cashback at the same store and get another 5% back on it, and repeat that. Whereas with a 5%
// bonus, we can't compound like this - we don't get a 5% bonus on bonus spending, only on cash
// spending.

@Entity(
    tableName = "source",
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
data class Source(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "data_set_id") val dataSetId: Long,
    val name: String,
    @ColumnInfo(name = "loyalty_type") val loyaltyType: LoyaltyType,
    @ColumnInfo(name = "loyalty_multiplier") val loyaltyMultiplier: Double,
    val notes: String,
) : Parcelable

fun Source?.toEditable(dataSetId: Long, locale: Locale): EditableSource {
    if (this == null) {
        return EditableSource(0, dataSetId, "", LoyaltyType.NONE, "", "")
    } else {
        myCheck(this.dataSetId == dataSetId) {
            "Expected identical dataSetIds but have this.dataSetId ${this.dataSetId} and dataSetId $dataSetId"
        }
        val loyaltyPercentage =
            when (loyaltyType) {
                LoyaltyType.NONE -> {
                    ""
                }

                LoyaltyType.BONUS -> {
                    formatDoubleForEditing(
                        100.0 / loyaltyMultiplier - 100.0,
                        minDecimals = 0,
                        maxDecimals = 2,
                        locale,
                    )
                }
                LoyaltyType.DISCOUNT -> {
                    formatDoubleForEditing(
                        100.0 * (1 - loyaltyMultiplier),
                        minDecimals = 0,
                        maxDecimals = 2,
                        locale,
                    )
                }
            }
        return EditableSource(id, dataSetId, name, loyaltyType, loyaltyPercentage, notes)
    }
}

@Parcelize
data class EditableSource(
    val id: Long,
    val dataSetId: Long,
    val name: String,
    val loyaltyType: LoyaltyType,
    val loyaltyPercentage: String,
    val notes: String,
) : Parcelable

fun EditableSource.toDomain(locale: Locale): Source? {
    val trimmedName = name.trim()
    // It could get confusing if an empty name leaked into the database (it would be
    // semi-invisible in the UI) so we'll check that here, even though we could generate a
    // Source with such a name and this is not really validation code - we expect to have been
    // called on a pre-validated EditableSource.
    if (trimmedName.isEmpty()) {
        return null
    }
    val loyaltyPercentage = parseStringAsDoubleOrNull(locale, loyaltyPercentage)
    val loyaltyMultiplier =
        when (loyaltyType) {
            LoyaltyType.NONE -> 1.0
            LoyaltyType.BONUS ->
                if (loyaltyPercentage != null) 100.0 / (100.0 + loyaltyPercentage) else null
            LoyaltyType.DISCOUNT ->
                if (loyaltyPercentage != null) 1.0 - (loyaltyPercentage / 100.0) else null
        }
    if (loyaltyMultiplier == null) {
        return null
    }
    return Source(
        id = id,
        dataSetId = dataSetId,
        name = trimmedName,
        loyaltyType = loyaltyType,
        loyaltyMultiplier = loyaltyMultiplier,
        notes = notes,
    )
}

enum class LoyaltyType(
    val id: Long,
    @field:StringRes val nameResource: Int,
    @field:StringRes val supportingTextResource: Int?,
) {
    NONE(1, R.string.loyalty_type_none, null),
    BONUS(2, R.string.loyalty_type_bonus, R.string.loyalty_type_bonus_supporting_text),
    DISCOUNT(3, R.string.loyalty_type_discount, R.string.loyalty_type_discount_supporting_text);

    companion object {
        private val loyaltyTypeById = LoyaltyType.entries.associateBy { it.id }

        fun fromValue(loyaltyDiscountTypeId: Long): LoyaltyType? =
            loyaltyTypeById[loyaltyDiscountTypeId]
    }
}
