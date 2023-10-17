package com.example.playlistmaker.player.domain.api

interface PlayerRepository {
    fun prepare(url: String, onCompletionListener: () -> Unit, onPreparedListener: () -> Unit)
    fun start()
    fun pause()
    fun release()
    fun getCurrentPosition(): Int
    fun isPlaying(): Boolean
}