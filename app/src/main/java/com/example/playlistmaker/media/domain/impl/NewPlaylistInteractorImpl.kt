package com.example.playlistmaker.media.domain.impl

import android.net.Uri
import com.example.playlistmaker.media.domain.api.NewPlaylistInteractor
import com.example.playlistmaker.media.domain.api.NewPlaylistRepository
import java.net.URI

class NewPlaylistInteractorImpl(private val newPlaylistRepository: NewPlaylistRepository) :
    NewPlaylistInteractor {
    override suspend fun saveImageToPrivateStorage(uri: Uri): Uri {
        return newPlaylistRepository.saveImageToPrivateStorage(uri)
    }
}