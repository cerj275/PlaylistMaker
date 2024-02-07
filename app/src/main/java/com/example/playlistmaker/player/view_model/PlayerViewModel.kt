package com.example.playlistmaker.player.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.api.FavoriteTracksInteractor
import com.example.playlistmaker.media.domain.api.PlayListsInteractor
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.media.view_model.PlayListsState
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.utils.SingleLiveEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val track: Track,
    private val playerInteractor: PlayerInteractor,
    private val favoriteTracksInteractor: FavoriteTracksInteractor,
    private val playlistInteractor: PlayListsInteractor

) : ViewModel() {

    companion object {
        private const val UPDATE_PLAYBACK_TIME_VALUE = 300L
    }

    private var timerJob: Job? = null
    private val stateLiveData = MutableLiveData<PlayerScreenState>()
    private val favoriteLiveData = MutableLiveData<Boolean>()
    private val playlistsStateLiveData = MutableLiveData<PlayListsState>()
    private var trackInPlaylistLiveData = SingleLiveEvent<TrackInPlaylistState>()

    init {
        stateLiveData.value = PlayerScreenState.DefaultScreenState()
        favoriteLiveData.value = track.isFavorite
        fillData()
    }

    fun observeState(): LiveData<PlayerScreenState> = stateLiveData
    fun observeFavorite(): LiveData<Boolean> = favoriteLiveData
    fun observePlaylists(): LiveData<PlayListsState> = playlistsStateLiveData
    fun observeTrackInPlaylist(): SingleLiveEvent<TrackInPlaylistState> = trackInPlaylistLiveData


    private fun renderState(playerState: PlayerScreenState) {
        stateLiveData.postValue(playerState)
    }

    fun preparePlayer() {
        playerInteractor.preparePlayer(
            url = track.previewUrl ?: "",
            onPreparedListener = {
                renderState(PlayerScreenState.PreparedScreenState())
            },
            onCompletionListener = {
                timerJob?.cancel()
                renderState(PlayerScreenState.PreparedScreenState())
            }
        )
    }

    private fun startPlayer() {
        playerInteractor.startPlayer()
        renderState(PlayerScreenState.PlayingScreenState(getCurrentPlayerPosition()))
        startTimer()
    }

    fun pausePlayer() {
        playerInteractor.pausePlayer()
        timerJob?.cancel()
        renderState(PlayerScreenState.PauseScreenState(getCurrentPlayerPosition()))
    }

    fun releasePlayer() {
        playerInteractor.releasePlayer()
        timerJob?.cancel()
        stateLiveData.value = PlayerScreenState.DefaultScreenState()
    }

    fun playbackControl() {
        if (playerInteractor.isPlaying()) {
            pausePlayer()
        } else {
            startPlayer()
        }
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (playerInteractor.isPlaying()) {
                delay(UPDATE_PLAYBACK_TIME_VALUE)
                stateLiveData.postValue(
                    PlayerScreenState.PlayingScreenState(
                        getCurrentPlayerPosition()
                    )
                )
            }
        }
    }

    private fun getCurrentPlayerPosition(): String {
        return SimpleDateFormat(
            "mm:ss",
            Locale.getDefault()
        ).format(playerInteractor.getCurrentPosition())
    }

    override fun onCleared() {
        super.onCleared()
        releasePlayer()
    }

    fun onFavoriteClicked() {
        favoriteLiveData.value = !track.isFavorite
        viewModelScope.launch {
            if (track.isFavorite) {
                favoriteTracksInteractor.deleteFavoriteTrack(track)
                track.isFavorite = false
            } else {
                favoriteTracksInteractor.addFavoriteTrack(track)
                track.isFavorite = true
            }
        }
    }

    fun fillData() {
        viewModelScope.launch {
            playlistInteractor
                .getPlaylists()
                .collect {
                    processResult(it)
                }
        }
    }

    private fun processResult(playlists: List<Playlist>) {
        if (playlists.isEmpty()) {
            renderPlaylistsState(PlayListsState.Empty)
        } else {
            renderPlaylistsState(PlayListsState.Content(playlists))
        }
    }

    fun addToPlaylist() {
        viewModelScope.launch {
            playlistInteractor.getPlaylists()
        }
    }

    private fun renderPlaylistsState(state: PlayListsState) {
        playlistsStateLiveData.postValue(state)
    }

    private fun addNewTrackToPlaylist(track: Track) {
        viewModelScope.launch {
            playlistInteractor.addNewTrackToPlaylist(track)
        }
    }

    fun checkTrackInPlaylist(track: Track, playlist: Playlist) {
        if (playlist.tracks.contains(track.trackId)) {
            trackInPlaylistLiveData.postValue(TrackInPlaylistState.Contained(playlist))
        } else {
            updatePlaylist(track, playlist)
            addNewTrackToPlaylist(track)
            trackInPlaylistLiveData.postValue(TrackInPlaylistState.Added(playlist))
        }
    }

    private fun updatePlaylist(track: Track, playlist: Playlist) {
        val list = mutableListOf<String>()
        list.addAll(playlist.tracks)
        list.add(track.trackId)
        viewModelScope.launch {
            playlistInteractor.updatePlaylist(
                playlist.copy(
                    tracks = list,
                    numberOfTracks = list.size
                )
            )
        }
    }
}