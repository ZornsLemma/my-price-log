package app.zornslemma.mypricelog.ui.screens.settings

import android.app.Application
import androidx.lifecycle.ViewModel
import app.zornslemma.mypricelog.domain.SettingsRepository
import app.zornslemma.mypricelog.domain.dataStore

class SettingsViewModel(application: Application) : ViewModel() {
    val settingsRepository = SettingsRepository(application.dataStore)
}
