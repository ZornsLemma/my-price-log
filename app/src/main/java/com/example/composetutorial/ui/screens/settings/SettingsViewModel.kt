package com.example.composetutorial.ui.screens.settings

import android.app.Application
import androidx.lifecycle.ViewModel
import com.example.composetutorial.SettingsRepository
import com.example.composetutorial.dataStore

class SettingsViewModel(
    application: Application) : ViewModel() {
    val settingsRepository = SettingsRepository(application.dataStore)
}
