package com.example.playlistmaker.media.view_model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.api.PlayListsInteractor
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.sharing.domain.api.SharingInteractor
import com.example.playlistmaker.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlaylistDetailsViewModel(
    private val context: Context,
    private val playlistId: Int,
    private val interactor: PlayListsInteractor,
    private val sharingInteractor: SharingInteractor
) : ViewModel() {
    private lateinit var playlist: Playlist
    private var tracks: List<Track> = listOf()

    init {
        renderState()
    }

    private val playlistLiveData = MutableLiveData<PlaylistDetailsState>()
    fun observePlaylistLiveData(): LiveData<PlaylistDetailsState> = playlistLiveData
    private val tracksLiveData = MutableLiveData<List<Track>>()
    fun observeTracksLiveData(): LiveData<List<Track>> = tracksLiveData
    private suspend fun getPlaylistById(): Playlist = withContext(Dispatchers.IO) {
        interactor.getPlaylist(playlistId)
    }

    fun getTracksFromPlaylist(playlist: Playlist) {
        viewModelScope.launch {
            interactor.getTracksFromPlaylist(playlist.tracks)
                .collect {
                    tracks = it.reversed()
                    tracksLiveData.value = it.reversed()
                }
        }
    }

    private fun renderPlaylistDetailsState() {
        playlistLiveData.value = PlaylistDetailsState(
            playlist.coverUri,
            playlist.name,
            playlist.description
        )
    }

    private fun renderState() {
        viewModelScope.launch {
            playlist = getPlaylistById()
            renderPlaylistDetailsState()
        }
    }

    fun deleteTrack(trackId: String, playlistId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            interactor.deleteTrack(trackId, playlistId).collect {
                withContext(Dispatchers.Main) {
                    tracksLiveData.value = it
                }
            }
        }
    }

    fun sharePlaylist() {
        sharingInteractor.sharePlaylist(
            Utils(context).getPlaylistInfoToShare(
                playlist,
                tracksLiveData.value!!
            )
        )
    }

    fun deletePlaylist(playlist: Playlist) {
        viewModelScope.launch(Dispatchers.IO) {
            interactor.deletePlaylist(playlist)
        }
    }


}