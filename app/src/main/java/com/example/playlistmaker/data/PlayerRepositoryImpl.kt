package com.example.playlistmaker.data

import android.media.MediaPlayer
import com.example.playlistmaker.domain.api.PlayerRepository
import com.example.playlistmaker.domain.models.PlayerState

class PlayerRepositoryImpl : PlayerRepository {

    override var playerState: PlayerState = PlayerState.STATE_DEFAULT
    private var mediaPlayer = MediaPlayer()

    override fun prepare(url: String) {
        mediaPlayer.apply {
            setDataSource(url)
            mediaPlayer.prepareAsync()
            playerState = PlayerState.STATE_PREPARED
        }
    }

    override fun start() {
        mediaPlayer.start()
        playerState = PlayerState.STATE_PLAYING
    }

    override fun pause() {
        mediaPlayer.pause()
        playerState = PlayerState.STATE_PAUSED
    }

    override fun release() {
        mediaPlayer.release()
        playerState = PlayerState.STATE_DEFAULT
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }

    override fun setOnCompletionListener(onCompletionListener: () -> Unit) {
        mediaPlayer.setOnCompletionListener {
            playerState = PlayerState.STATE_PREPARED
            onCompletionListener()
        }
    }
}