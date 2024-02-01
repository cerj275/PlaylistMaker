package com.example.playlistmaker.player.data

import android.media.MediaPlayer
import com.example.playlistmaker.player.domain.api.PlayerRepository

class PlayerRepositoryImpl(private val mediaPlayer: MediaPlayer) : PlayerRepository {

    override fun prepare(url: String, onCompletionListener: () -> Unit, onPreparedListener: () -> Unit) {
        mediaPlayer.apply {
            setDataSource(url)
            mediaPlayer.prepareAsync()
            setOnPreparedListener {
                onPreparedListener.invoke()
            }
            setOnCompletionListener {
                onCompletionListener.invoke()
            }
        }
    }

    override fun start() {
        mediaPlayer.start()
    }

    override fun pause() {
        mediaPlayer.pause()
    }

    override fun release() {
        mediaPlayer.reset()
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }

    override fun isPlaying(): Boolean {
        return mediaPlayer.isPlaying
    }
}