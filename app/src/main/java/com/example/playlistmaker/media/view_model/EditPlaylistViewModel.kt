package com.example.playlistmaker.media.view_model

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.api.NewPlaylistInteractor
import com.example.playlistmaker.media.domain.api.PlayListsInteractor
import com.example.playlistmaker.media.domain.model.Playlist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditPlaylistViewModel(
    interactor: PlayListsInteractor,
    newPlaylistInteractor: NewPlaylistInteractor
) : NewPlaylistViewModel(interactor, newPlaylistInteractor) {

    private val playlistLiveData = MutableLiveData<PlaylistDetailsState>()
    fun observePlayListLiveData(): LiveData<PlaylistDetailsState> = playlistLiveData

    fun fillData(playlist: Playlist) {
        viewModelScope.launch {
            getPlaylist(playlist.id)
            playlistLiveData.value =
                PlaylistDetailsState(playlist.coverUri, playlist.name, playlist.description)
            name = playlist.name
            description = playlist.description
            coverUri = playlist.coverUri
        }
    }

    private suspend fun getPlaylist(playlistId: Int): Playlist = withContext(Dispatchers.IO) {
        interactor.getPlaylist(playlistId)
    }

    fun getEditedName(text: CharSequence?) {
        name = text.toString()
    }

    fun getEditedDescription(text: CharSequence?) {
        description = text.toString()
    }

    fun editPlaylist(playlist: Playlist) {
        viewModelScope.launch {
            var newUri = ""
            if (coverUri.isNotEmpty()) {
                newUri =
                    newPlaylistInteractor.saveImageToPrivateStorage(Uri.parse(coverUri)).toString()
            }
            val temp: Playlist = playlist.copy(
                id = playlist.id,
                name = name,
                description = description,
                coverUri = newUri,
                tracks = playlist.tracks,
                numberOfTracks = playlist.numberOfTracks
            )
            interactor.editPlaylist(temp)
        }
    }
}