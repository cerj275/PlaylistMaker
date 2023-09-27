package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.PlayerState

interface PlayerInteractor {
    fun preparePlayer(url: String)
    fun startPlayer()
    fun pausePlayer()
    fun releasePlayer()
    fun getCurrentPosition(): Int
    fun getPlayerState(): PlayerState
    fun setOnCompletionListener(onCompletionListener: (() -> Unit))
}