package com.example.playlistmaker.player.domain.api

interface PlayerInteractor {
    fun preparePlayer(url: String, onCompletionListener: () -> Unit, onPreparedListener: () -> Unit)
    fun startPlayer()
    fun pausePlayer()
    fun releasePlayer()
    fun getCurrentPosition(): Int
    fun isPlaying(): Boolean
}