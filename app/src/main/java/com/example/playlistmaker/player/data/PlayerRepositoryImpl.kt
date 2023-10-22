package com.example.playlistmaker.player.data

import android.media.MediaPlayer
import com.example.playlistmaker.player.domain.api.PlayerRepository

class PlayerRepositoryImpl : PlayerRepository {

    private var mediaPlayer = MediaPlayer()

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
        mediaPlayer.release()
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }

    override fun isPlaying(): Boolean {
        return mediaPlayer.isPlaying
    }
}