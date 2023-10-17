package com.example.playlistmaker.player.domain.models


sealed interface PlayerScreenState {
    object DefaultScreenState: PlayerScreenState
    object PreparedScreenState : PlayerScreenState
    object PlayingScreenState: PlayerScreenState
    object PauseScreenState: PlayerScreenState
}