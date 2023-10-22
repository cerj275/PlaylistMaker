package com.example.playlistmaker.player.view_model

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.util.Creator
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val track: Track,
    private val playerInteractor: PlayerInteractor,
    application: Application
) : AndroidViewModel(application) {

    companion object {
        fun getViewModelFactory(track: Track): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val interactor = Creator.getPlayerInteractor()
                PlayerViewModel(track, interactor, this[APPLICATION_KEY] as Application)
            }
        }

        private const val UPDATE_PLAYBACK_TIME_VALUE = 300L
    }

    private val handler = Handler(Looper.getMainLooper())

    private val stateLiveData = MutableLiveData<PlayerScreenState>()
    init {
        stateLiveData.value = PlayerScreenState.DefaultScreenState
    }
    fun observeState(): LiveData<PlayerScreenState> = stateLiveData

    private fun renderState(playerState: PlayerScreenState) {
        stateLiveData.postValue(playerState)
    }

    fun preparePlayer() {
        playerInteractor.preparePlayer(
            url = track.previewUrl ?: "",
            onPreparedListener = {
                renderState(PlayerScreenState.PreparedScreenState)
            },
            onCompletionListener = {
                handler.removeCallbacks(this::updatePlaybackTime)
                renderState(PlayerScreenState.PreparedScreenState)
            }
        )
    }

    private fun startPlayer() {
        playerInteractor.startPlayer()
        renderState(PlayerScreenState.PlayingScreenState)
        handler.post(this::updatePlaybackTime)
    }

    fun pausePlayer() {
        playerInteractor.pausePlayer()
        renderState(PlayerScreenState.PauseScreenState)
        handler.removeCallbacks(this::updatePlaybackTime)
    }

    fun releasePlayer() {
        playerInteractor.releasePlayer()
        handler.removeCallbacks(this::updatePlaybackTime)
    }

    fun playbackControl() {
        if (playerInteractor.isPlaying()) {
            pausePlayer()
        } else {
            startPlayer()
        }
    }

    private fun updatePlaybackTime() {
        handler.post(object : Runnable {
            override fun run() {
                handler.postDelayed({
                    if (playerInteractor.isPlaying()) {
                        val playbackTime = SimpleDateFormat("mm:ss", Locale.getDefault())
                            .format(playerInteractor.getCurrentPosition())
                        stateLiveData.postValue(PlayerScreenState.TimerState(playbackTime))
                        handler.postDelayed(this, UPDATE_PLAYBACK_TIME_VALUE)
                    }
                }, UPDATE_PLAYBACK_TIME_VALUE)
            }
        })
    }
}