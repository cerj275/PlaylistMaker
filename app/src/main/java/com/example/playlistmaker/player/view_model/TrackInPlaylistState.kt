package com.example.playlistmaker.player.view_model

import com.example.playlistmaker.media.domain.model.Playlist

sealed class TrackInPlaylistState {

    class Contained(val playlist: Playlist) : TrackInPlaylistState()
    class Added(val playlist: Playlist) : TrackInPlaylistState()
}