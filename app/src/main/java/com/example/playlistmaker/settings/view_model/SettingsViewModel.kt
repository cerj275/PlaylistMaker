package com.example.playlistmaker.settings.view_model

import androidx.lifecycle.ViewModel
import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.settings.domain.models.ThemeSettings
import com.example.playlistmaker.sharing.domain.api.SharingInteractor

class SettingsViewModel(
    private val settingsInteractor: SettingsInteractor,
    private val sharingInteractor: SharingInteractor
) : ViewModel() {

    fun shareLink() {
        sharingInteractor.shareApp()
    }

    fun sendMailToSupport() {
        sharingInteractor.openSupport()
    }

    fun openUserAgreement() {
        sharingInteractor.openTerms()
    }

    fun themeSwitched(isChecked: Boolean) {
        settingsInteractor.updateThemeSettings(ThemeSettings(isChecked))
    }

    fun getDarkThemeIsEnabled(): Boolean {
        return settingsInteractor.getThemeSettings().darkThemeEnabled
    }
}