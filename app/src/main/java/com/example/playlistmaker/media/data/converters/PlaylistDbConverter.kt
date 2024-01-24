package com.example.playlistmaker.media.data.converters

import com.example.playlistmaker.media.data.db.entity.PlaylistEntity
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.search.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class PlaylistDbConverter {
    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playlist.id,
            playlist.name,
            playlist.description,
            playlist.coverUri,
            convertListToString(playlist.tracks),
            playlist.numberOfTracks,

            )
    }

    fun map(playlist: PlaylistEntity): Playlist {
        return Playlist(
            playlist.id,
            playlist.name,
            playlist.description,
            playlist.coverUri,
            convertStringToList(playlist.tracks),
            playlist.numberOfTracks
        )
    }

    private fun convertListToString(tracks: MutableList<Track>): String {
        return Gson().toJson(tracks)
    }

    private fun convertStringToList(id: String): MutableList<Track> {
        val type: Type = object : TypeToken<MutableList<Track?>?>() {}.type
        return Gson().fromJson(id, type) as MutableList<Track>
    }
    fun convertToPlaylist(playlist: PlaylistEntity): Playlist {
        return Playlist(
            playlist.id,
            playlist.name,
            playlist.description,
            playlist.coverUri,
            convertStringToList(playlist.tracks),
            playlist.numberOfTracks
        )
    }
}

