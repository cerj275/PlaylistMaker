package com.example.playlistmaker.player.view_model

sealed class PlayerScreenState(
    val isPlayButtonEnabled: Boolean,
    val buttonText: String,
    val progress: String
) {
    class DefaultScreenState : PlayerScreenState(false, PLAY, ZERO_PLAYBACK_TIME_VALUE)
    class PreparedScreenState : PlayerScreenState(true, PLAY, ZERO_PLAYBACK_TIME_VALUE)
    class PlayingScreenState(progress: String) : PlayerScreenState(true, PAUSE, progress)
    class PauseScreenState(progress: String) : PlayerScreenState(true, PLAY, progress)

    companion object {
        const val ZERO_PLAYBACK_TIME_VALUE = "00:00"
        const val PLAY = "PLAY"
        const val PAUSE = "PAUSE"
    }
}