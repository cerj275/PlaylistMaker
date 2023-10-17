package com.example.playlistmaker.settings.view_model

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.App
import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.settings.domain.models.ThemeSettings
import com.example.playlistmaker.sharing.domain.api.SharingInteractor
import com.example.playlistmaker.util.Creator

class SettingsViewModel(
    private val application: App,
    private val settingsInteractor: SettingsInteractor,
    private val sharingInteractor: SharingInteractor
) : AndroidViewModel(application) {

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as App
                val settingsInteractor =
                    Creator.provideSettingsInteractor(application.applicationContext)
                val sharingInteractor =
                    Creator.provideSharingInteractor(application.applicationContext)
                SettingsViewModel(application, settingsInteractor, sharingInteractor)
            }
        }
    }

    fun onSharePressed() {
        sharingInteractor.shareApp()
    }

    fun onSupportPressed() {
        sharingInteractor.openSupport()
    }

    fun onUserAgreementPressed() {
        sharingInteractor.openTerms()
    }

    fun onThemeSwitchedPressed(isChecked: Boolean) {
        application.switchTheme(isChecked)
        settingsInteractor.updateThemeSettings(ThemeSettings(isChecked))
    }

    fun getDarkThemeIsEnabled(): Boolean {
        return settingsInteractor.getThemeSettings().darkThemeEnabled
    }
}