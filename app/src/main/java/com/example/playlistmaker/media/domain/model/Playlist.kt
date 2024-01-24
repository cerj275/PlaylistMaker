package com.example.playlistmaker.media.domain.model

import com.example.playlistmaker.search.domain.models.Track

data class Playlist(
    val id: Int,
    val name: String,
    val description: String,
    val coverUri: String,
    val tracks: MutableList<Track>,
    val numberOfTracks: Int
)
