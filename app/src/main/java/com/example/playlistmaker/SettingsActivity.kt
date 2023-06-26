package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val backButton = findViewById<ImageButton>(R.id.buttonBack)
        val share = findViewById<TextView>(R.id.share)
        val support = findViewById<TextView>(R.id.support)
        val termsOfUse = findViewById<TextView>(R.id.termsOfUse)

        share.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            val ypLink = getString(R.string.yp_android_link)
            shareIntent.putExtra(Intent.EXTRA_TEXT, ypLink)
            startActivity(shareIntent)
        }
        support.setOnClickListener {
            val supportEmail = getString(R.string.yp_email)
            val topic = getString(R.string.feedback_topic)
            val message = getString(R.string.feedback_message)

            val supportIntent = Intent(Intent.ACTION_SENDTO)
            supportIntent.data = Uri.parse("mailto:")
            supportIntent.putExtra(Intent.EXTRA_EMAIL, supportEmail)
            supportIntent.putExtra(Intent.EXTRA_SUBJECT, topic)
            supportIntent.putExtra(Intent.EXTRA_TEXT, message)
            startActivity(supportIntent)
        }

        termsOfUse.setOnClickListener {
            val link = getString(R.string.terms_of_use_link)
            val termsOfUseIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
            startActivity(termsOfUseIntent)
        }

        backButton.setOnClickListener {
            finish()
        }
    }
}