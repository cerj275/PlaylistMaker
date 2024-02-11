package com.example.playlistmaker.media.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Playlist(
    val id: Int = 0,
    val name: String,
    val description: String = "",
    val coverUri: String,
    val tracks: List<String> = mutableListOf(),
    val numberOfTracks: Int = 0
) : Parcelable
