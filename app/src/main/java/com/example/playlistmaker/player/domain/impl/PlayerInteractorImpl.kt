package com.example.playlistmaker.player.domain.impl

import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.player.domain.api.PlayerRepository

class PlayerInteractorImpl(private val playerRepository: PlayerRepository): PlayerInteractor {
    override fun preparePlayer(url: String, onCompletionListener: () -> Unit, onPreparedListener: () -> Unit) {
        playerRepository.prepare(url, onCompletionListener, onPreparedListener)
    }

    override fun startPlayer() {
        playerRepository.start()
    }

    override fun pausePlayer() {
        playerRepository.pause()
    }

    override fun releasePlayer() {
        playerRepository.release()
    }

    override fun getCurrentPosition(): Int {
        return playerRepository.getCurrentPosition()
    }

    override fun isPlaying(): Boolean {
        return playerRepository.isPlaying()
    }
}