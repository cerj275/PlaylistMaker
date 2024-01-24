package com.example.playlistmaker.media.domain.api

import android.net.Uri

interface NewPlaylistInteractor {

    suspend fun saveImageToPrivateStorage(uri: Uri, fileName: String)
}