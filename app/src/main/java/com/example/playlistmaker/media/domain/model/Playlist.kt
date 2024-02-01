package com.example.playlistmaker.media.domain.model

data class Playlist(
    val id: Int,
    val name: String,
    val description: String,
    val coverUri: String,
    val tracks: MutableList<String>,
    val numberOfTracks: Int
)
