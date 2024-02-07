package com.example.playlistmaker.media.domain.api

import android.net.Uri
import java.net.URI

interface NewPlaylistInteractor {
    suspend fun saveImageToPrivateStorage(uri: Uri): Uri
}