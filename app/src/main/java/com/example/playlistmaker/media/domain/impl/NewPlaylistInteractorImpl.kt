package com.example.playlistmaker.media.domain.impl

import android.net.Uri
import com.example.playlistmaker.media.domain.api.NewPlaylistInteractor
import com.example.playlistmaker.media.domain.api.NewPlaylistRepository

class NewPlaylistInteractorImpl(private val repository: NewPlaylistRepository) :
    NewPlaylistInteractor {
    override suspend fun saveImageToPrivateStorage(uri: Uri, fileName: String) {
        repository.saveImageToPrivateStorage(uri, fileName)
    }
}