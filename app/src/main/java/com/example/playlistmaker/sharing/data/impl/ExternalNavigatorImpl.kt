package com.example.playlistmaker.sharing.data.impl

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.playlistmaker.sharing.data.api.ExternalNavigator
import com.example.playlistmaker.sharing.domain.model.EmailData

class ExternalNavigatorImpl(private val context: Context) : ExternalNavigator {
    override fun shareLink(shareAppLink: String) {
        Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareAppLink)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(this)
        }

    }

    override fun openLink(termsLink: String) {
        Intent(Intent.ACTION_VIEW, Uri.parse(termsLink)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(this)
        }
    }

    override fun openEmail(supportEmailData: EmailData) {
        Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(supportEmailData.email))
            putExtra(Intent.EXTRA_SUBJECT, supportEmailData.subject)
            putExtra(Intent.EXTRA_TEXT, supportEmailData.textMessage)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(this)
        }
    }
}