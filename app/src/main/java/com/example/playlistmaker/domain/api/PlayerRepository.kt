package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.PlayerState

interface PlayerRepository {
    var playerState: PlayerState
    fun prepare(url: String)
    fun start()
    fun pause()
    fun release()
    fun getCurrentPosition(): Int
    fun setOnCompletionListener(onCompletionListener: () -> Unit)


}