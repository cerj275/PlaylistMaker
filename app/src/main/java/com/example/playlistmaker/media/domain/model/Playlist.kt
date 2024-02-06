package com.example.playlistmaker.media.domain.model

data class Playlist(
    val id: Int = 0,
    val name: String,
    val description: String = "",
    val coverUri: String,
    val tracks: MutableList<String> = mutableListOf(),
    val numberOfTracks: Int = 0
)
