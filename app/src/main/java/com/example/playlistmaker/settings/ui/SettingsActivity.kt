package com.example.playlistmaker.settings.ui

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.R
import com.example.playlistmaker.settings.view_model.SettingsViewModel
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val viewModel = ViewModelProvider(
            this,
            SettingsViewModel.getViewModelFactory()
        )[SettingsViewModel::class.java]

        val bBack = findViewById<ImageButton>(R.id.buttonBack)
        val tvShare = findViewById<TextView>(R.id.textViewShare)
        val tvSupport = findViewById<TextView>(R.id.textViewSupport)
        val tvTermsOfUse = findViewById<TextView>(R.id.textViewTermsOfUse)
        val smThemeSwitcher = findViewById<SwitchMaterial>(R.id.themeSwitcher)

        tvShare.setOnClickListener {
            viewModel.onSharePressed()
        }

        tvSupport.setOnClickListener {
            viewModel.onSupportPressed()
        }

        tvTermsOfUse.setOnClickListener {
            viewModel.onUserAgreementPressed()
        }

        smThemeSwitcher.isChecked = viewModel.getDarkThemeIsEnabled()

        smThemeSwitcher.setOnCheckedChangeListener { switcher, isChecked ->
            viewModel.onThemeSwitchedPressed(isChecked)
        }

        bBack.setOnClickListener {
            finish()
        }
    }
}