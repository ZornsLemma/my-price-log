package app.zornslemma.mypricelog.domain

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import app.zornslemma.mypricelog.app.AppScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

const val defaultStalePriceThresholdDays = 30
const val defaultAncientPriceThresholdDays = 180
const val defaultAnnualInflationPercent = 5

data class PriceAgeSettings(
    val stalePriceThresholdDays: Int,
    val ancientPriceThresholdDays: Int,
    val annualInflationPercent: Int,
)

class SettingsRepository(private val dataStore: DataStore<Preferences>) {
    private object Keys {
        val STALE_PRICE_THRESHOLD_DAYS_KEY = intPreferencesKey("stale_price_threshold_days")
        val ANCIENT_PRICE_THRESHOLD_DAYS_KEY = intPreferencesKey("ancient_price_threshold_days")
        val ANNUAL_INFLATION_PERCENT_KEY = intPreferencesKey("annual_inflation_percent")
    }

    val stalePriceThresholdDaysFlow: Flow<Int> =
        dataStore.data.map { prefs ->
            prefs[Keys.STALE_PRICE_THRESHOLD_DAYS_KEY] ?: defaultStalePriceThresholdDays
        }

    val ancientPriceThresholdDaysFlow: Flow<Int> =
        dataStore.data.map { prefs ->
            prefs[Keys.ANCIENT_PRICE_THRESHOLD_DAYS_KEY] ?: defaultAncientPriceThresholdDays
        }

    val annualInflationPercentFlow: Flow<Int> =
        dataStore.data.map { prefs ->
            prefs[Keys.ANNUAL_INFLATION_PERCENT_KEY] ?: defaultAnnualInflationPercent
        }

    val priceAgeSettingsFlow: Flow<PriceAgeSettings> =
        combine(
            stalePriceThresholdDaysFlow,
            ancientPriceThresholdDaysFlow,
            annualInflationPercentFlow,
        ) { stalePriceThresholdDays, ancientPriceThresholdDays, annualInflationPercent ->
            PriceAgeSettings(
                stalePriceThresholdDays,
                ancientPriceThresholdDays,
                annualInflationPercent,
            )
        }

    fun setStalePriceThresholdAsync(stalePriceThreshold: Int) {
        setValueAsync(Keys.STALE_PRICE_THRESHOLD_DAYS_KEY, stalePriceThreshold)
    }

    fun setAncientPriceThresholdDaysAsync(ancientPriceThresholdDays: Int) {
        setValueAsync(Keys.ANCIENT_PRICE_THRESHOLD_DAYS_KEY, ancientPriceThresholdDays)
    }

    fun setAnnualInflationPercentAsync(annualInflationPercent: Int) {
        setValueAsync(Keys.ANNUAL_INFLATION_PERCENT_KEY, annualInflationPercent)
    }

    private fun <T> setValueAsync(key: Preferences.Key<T>, value: T) {
        AppScope.io.launch { dataStore.edit { it[key] = value } }
    }
}

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
