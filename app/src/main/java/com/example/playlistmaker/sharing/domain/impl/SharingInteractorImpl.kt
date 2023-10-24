package com.example.playlistmaker.sharing.domain.impl

import com.example.playlistmaker.sharing.data.api.ExternalNavigator
import com.example.playlistmaker.sharing.domain.api.SharingInteractor
import com.example.playlistmaker.sharing.domain.model.EmailData

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
) : SharingInteractor {
    override fun shareApp() {
        externalNavigator.shareLink(getShareAppLink())
    }

    override fun openTerms() {
        externalNavigator.openLink(getTermsLink())
    }

    override fun openSupport() {
        externalNavigator.openEmail(getSupportEmailData())
    }

    private fun getShareAppLink(): String {
        return APP_LINK
    }

    private fun getSupportEmailData(): EmailData {
        return EmailData(
            email = EMAIL,
            subject = SUBJECT,
            textMessage = TEXT_MESSAGE
        )
    }

    private fun getTermsLink(): String {
        return TERMS_LINK
    }

    companion object {
        private const val EMAIL = "cerj275@yandex.ru"
        private const val SUBJECT =
            "Сообщение разработчикам и разработчицам приложения Playlist Maker"
        private const val TEXT_MESSAGE =
            "Спасибо разработчикам и разработчицам за крутое приложение!"
        private const val APP_LINK = "https://practicum.yandex.ru/android-developer/"
        private const val TERMS_LINK = "https://yandex.ru/legal/practicum_offer/"
    }
}