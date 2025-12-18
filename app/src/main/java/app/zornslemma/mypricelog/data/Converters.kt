package app.zornslemma.mypricelog.data

import androidx.room.TypeConverter
import app.zornslemma.mypricelog.domain.MeasurementUnit
import app.zornslemma.mypricelog.domain.QuantityType
import java.time.Instant

class Converters {
    // We don't have any QuantityType fields in the database at the moment, but we'll keep the
    // converters for it around so if we add one in the future it will just work rather than ending
    // up being stored as a string.

    @Suppress("unused")
    @TypeConverter
    fun fromQuantityType(quantityType: QuantityType?): Int? {
        return quantityType?.id
    }

    @Suppress("unused")
    @TypeConverter
    fun toQuantityType(value: Int?): QuantityType? {
        return value?.let { QuantityType.fromId(it) }
    }

    @TypeConverter
    fun fromMeasurementUnit(measurementUnit: MeasurementUnit?): Long? {
        return measurementUnit?.id
    }

    @TypeConverter
    fun toMeasurementUnit(value: Long?): MeasurementUnit? {
        return value?.let { MeasurementUnit.fromId(it) }
    }

    @TypeConverter
    fun fromInstant(instant: Instant?): Long? {
        return instant?.toEpochMilli()
    }

    @TypeConverter
    fun toInstant(value: Long?): Instant? {
        return value?.let { Instant.ofEpochMilli(it) }
    }

    @TypeConverter
    fun fromLoyaltyType(loyaltyType: LoyaltyType?): Long? {
        return loyaltyType?.id
    }

    @TypeConverter
    fun toLoyaltyType(value: Long?): LoyaltyType? {
        return value?.let { LoyaltyType.fromValue(it) }
    }
}
