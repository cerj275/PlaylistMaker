package com.example.playlistmaker.media.view_model

import com.example.playlistmaker.media.domain.model.Playlist

sealed interface PlayListsState {
    object Empty : PlayListsState
    data class Content(val playlists: List<Playlist>) : PlayListsState
}