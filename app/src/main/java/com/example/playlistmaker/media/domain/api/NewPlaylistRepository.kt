package com.example.playlistmaker.media.domain.api

import android.net.Uri

interface NewPlaylistRepository {

    suspend fun saveImageToPrivateStorage(uri: Uri, fileName: String)
}