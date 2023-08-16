package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate


const val SHARED_PREFERENCES = "shared_preferences"
const val DARK_THEME_ENABLED = "dark_theme_enabled"
class App : Application() {

    private var darkTheme = false

    override fun onCreate() {
        super.onCreate()

        val sharedPrefs = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE)
        switchTheme(sharedPrefs.getBoolean(DARK_THEME_ENABLED, false))
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}