package com.example.playlistmaker.media.view_model

import com.example.playlistmaker.search.domain.models.Track

sealed interface FavoriteTracksScreenState {

    data class Content(val tracks: List<Track>) : FavoriteTracksScreenState

    object Empty : FavoriteTracksScreenState
}