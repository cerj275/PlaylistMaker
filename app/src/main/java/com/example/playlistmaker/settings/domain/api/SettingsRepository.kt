package com.example.playlistmaker.settings.domain.api

import com.example.playlistmaker.settings.domain.models.ThemeSettings

interface SettingsRepository {
    fun getThemeSettings() : ThemeSettings
    fun updateThemeSettings(settings: ThemeSettings)
    fun switchTheme()
}