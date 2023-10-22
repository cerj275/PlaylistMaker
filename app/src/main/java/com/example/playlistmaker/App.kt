package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.settings.domain.api.SettingsRepository
import com.example.playlistmaker.util.Creator


class App : Application() {

    private lateinit var repository: SettingsRepository

    override fun onCreate() {
        super.onCreate()

        repository = Creator.getSettingsRepository(this.applicationContext)
        switchTheme(repository.getThemeSettings().darkThemeEnabled)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}