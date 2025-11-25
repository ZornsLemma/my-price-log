package com.example.composetutorial.domain

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.composetutorial.AppScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

// TODO: MOVE THE FOLLOWING!?
const val defaultStalePriceThreshold = 30
const val defaultAncientPriceThresholdDays = 180
const val defaultAnnualInflationPercent = 5

data class PriceAgeSettings(val stalePriceThreshold: Int, val ancientPriceThresholdDays: Int, val annualInflationPercent: Int)

class SettingsRepository(private val dataStore: DataStore<Preferences>) {
    private object Keys {
        val STALE_PRICE_THRESHOLD_KEY =
            intPreferencesKey("stale_price_threshold") // TODO: RENAME THIS AND ALL ASSOCIATED VARS TO INCLUDE "DAYS"?
        val ANCIENT_PRICE_THRESHOLD_DAYS_KEY = intPreferencesKey("ancient_price_threshold_days")
        val ANNUAL_INFLATION_PERCENT_KEY = intPreferencesKey("annual_inflation_percent")
    }

    val stalePriceThresholdFlow: Flow<Int> = dataStore.data
        .map { prefs -> prefs[Keys.STALE_PRICE_THRESHOLD_KEY] ?: defaultStalePriceThreshold }

    val ancientPriceThresholdDaysFlow: Flow<Int> = dataStore.data.map { prefs -> prefs[Keys.ANCIENT_PRICE_THRESHOLD_DAYS_KEY] ?: defaultAncientPriceThresholdDays }

    val annualInflationPercentFlow: Flow<Int> = dataStore.data.map { prefs -> prefs[Keys.ANNUAL_INFLATION_PERCENT_KEY] ?: defaultAnnualInflationPercent }

    val priceAgeSettingsFlow: Flow<PriceAgeSettings> = combine(stalePriceThresholdFlow, ancientPriceThresholdDaysFlow, annualInflationPercentFlow) { stalePriceThreshold, ancientPriceThresholdDays, annualInflationPercent ->
        PriceAgeSettings(stalePriceThreshold, ancientPriceThresholdDays, annualInflationPercent)
    }

    fun setStalePriceThresholdAsync(stalePriceThreshold: Int) {
        setValueAsync(Keys.STALE_PRICE_THRESHOLD_KEY, stalePriceThreshold)
    }

    fun setAncientPriceThresholdDaysAsync(ancientPriceThresholdDays: Int) {
        setValueAsync(Keys.ANCIENT_PRICE_THRESHOLD_DAYS_KEY, ancientPriceThresholdDays)
    }

    fun setAnnualInflationPercentAsync(annualInflationPercent: Int) {
        setValueAsync(Keys.ANNUAL_INFLATION_PERCENT_KEY, annualInflationPercent)
    }

    private fun <T> setValueAsync(key: Preferences.Key<T>, value: T) {
        AppScope.io.launch {
            dataStore.edit { it[key] = value }
        }
    }
}

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
