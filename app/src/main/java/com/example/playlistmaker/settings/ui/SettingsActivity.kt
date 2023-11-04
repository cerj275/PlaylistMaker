package com.example.playlistmaker.settings.ui

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.settings.view_model.SettingsViewModel
import com.google.android.material.switchmaterial.SwitchMaterial
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {

    private val viewModel: SettingsViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val bBack = findViewById<ImageButton>(R.id.buttonBack)
        val tvShare = findViewById<TextView>(R.id.textViewShare)
        val tvSupport = findViewById<TextView>(R.id.textViewSupport)
        val tvTermsOfUse = findViewById<TextView>(R.id.textViewTermsOfUse)
        val smThemeSwitcher = findViewById<SwitchMaterial>(R.id.themeSwitcher)

        tvShare.setOnClickListener {
            viewModel.shareLink()
        }

        tvSupport.setOnClickListener {
            viewModel.sendMailToSupport()
        }

        tvTermsOfUse.setOnClickListener {
            viewModel.openUserAgreement()
        }

        smThemeSwitcher.isChecked = viewModel.getDarkThemeIsEnabled()

        smThemeSwitcher.setOnCheckedChangeListener { switcher, isChecked ->
            viewModel.themeSwitched(isChecked)
        }

        bBack.setOnClickListener {
            finish()
        }
    }
}