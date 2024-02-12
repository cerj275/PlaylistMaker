package com.example.playlistmaker.utils

import android.content.Context
import com.example.playlistmaker.R
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.search.domain.models.Track

class Utils(val context: Context) {

    fun getPlaylistInfoToShare(
        playlist: Playlist,
        tracks: List<Track>,
    ): String {
        val text = buildString {
            append(playlist.name)
            appendLine()
            if (!playlist.description.isNullOrEmpty()) {
                append(playlist.description)
            }
            appendLine()
            append(
                context.resources.getQuantityString(
                    R.plurals.number_of_tracks,
                    tracks.size,
                    tracks.size
                )
            )
            for (i in tracks.indices) {
                appendLine()
                append(
                    "${i + 1}. ${tracks[i].artistName} - ${tracks[i].trackName} ${
                        context.resources.getQuantityString(
                            R.plurals.duration_of_tracks,
                            Converter().convertTimeMillisToMinutes(tracks[i].trackTimeMillis),
                            Converter().convertTimeMillisToMinutes(tracks[i].trackTimeMillis)
                        )
                    }"
                )
            }
        }
        return text
    }
}