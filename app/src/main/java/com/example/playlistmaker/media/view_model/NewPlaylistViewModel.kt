package com.example.playlistmaker.media.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.api.PlayListsInteractor
import com.example.playlistmaker.media.domain.model.Playlist
import kotlinx.coroutines.launch

class NewPlaylistViewModel(
    private val interactor: PlayListsInteractor,
) : ViewModel() {
    fun createPlaylist(playlist: Playlist) {
        viewModelScope.launch {
            interactor.addPlaylist(playlist)
            interactor.getPlaylists()
        }
    }
}