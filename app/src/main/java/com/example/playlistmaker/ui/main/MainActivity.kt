package com.example.playlistmaker.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.ui.mediaLibrary.MediaLibraryActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.ui.search.SearchActivity
import com.example.playlistmaker.ui.settings.SettingsActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchButton = findViewById<Button>(R.id.buttonSearch)
        val mediaLibraryButton = findViewById<Button>(R.id.buttonMediaLibrary)
        val settingsButton = findViewById<Button>(R.id.buttonSettings)

        searchButton.setOnClickListener {
            val searchIntent = Intent(this, SearchActivity::class.java)
            startActivity(searchIntent)
        }
        mediaLibraryButton.setOnClickListener {
            val mediaLibraryIntent = Intent(this, MediaLibraryActivity::class.java)
            startActivity(mediaLibraryIntent)
        }

        settingsButton.setOnClickListener {
            val settingsIntent = Intent(this, SettingsActivity::class.java)
            startActivity(settingsIntent)
        }
    }
}