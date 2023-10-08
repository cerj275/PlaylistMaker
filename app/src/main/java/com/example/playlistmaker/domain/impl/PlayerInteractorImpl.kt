package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.PlayerInteractor
import com.example.playlistmaker.domain.api.PlayerRepository
import com.example.playlistmaker.domain.models.PlayerState

class PlayerInteractorImpl(private val playerRepository: PlayerRepository): PlayerInteractor {
    override fun preparePlayer(url: String) {
        playerRepository.prepare(url)
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

    override fun getPlayerState(): PlayerState {
        return playerRepository.playerState
    }

    override fun setOnCompletionListener(onCompletionListener: () -> Unit) {
        playerRepository.setOnCompletionListener(onCompletionListener)
    }
}