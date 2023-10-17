package com.example.playlistmaker.settings.data.impl

import android.content.SharedPreferences
import com.example.playlistmaker.settings.domain.api.SettingsRepository
import com.example.playlistmaker.settings.domain.models.ThemeSettings

class SettingsRepositoryImpl(private val sharedPreferences: SharedPreferences) :
    SettingsRepository {

    companion object{
        const val DARK_THEME_ENABLED = "DARK_THEME_ENABLED"
    }
    override fun getThemeSettings(): ThemeSettings {
        return (ThemeSettings(sharedPreferences.getBoolean(DARK_THEME_ENABLED,false)))
    }

    override fun updateThemeSettings(settings: ThemeSettings) {
        sharedPreferences.edit()
            .putBoolean(DARK_THEME_ENABLED,settings.darkThemeEnabled)
            .apply()
    }
}