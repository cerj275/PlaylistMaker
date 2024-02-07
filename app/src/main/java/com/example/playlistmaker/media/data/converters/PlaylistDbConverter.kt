package com.example.playlistmaker.media.data.converters

import com.example.playlistmaker.media.data.db.entity.PlaylistEntity
import com.example.playlistmaker.media.data.db.entity.TrackInPlaylistEntity
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.search.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PlaylistDbConverter(private val gson: Gson) {
    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playlist.id,
            playlist.name,
            playlist.description,
            playlist.coverUri,
            gson.toJson(playlist.tracks),
            playlist.numberOfTracks,

            )
    }

    fun map(playlist: PlaylistEntity): Playlist {
        val typeToken = object : TypeToken<List<String>>(){}.type
        return Playlist(
            playlist.id,
            playlist.name,
            playlist.description,
            playlist.coverUri,
            gson.fromJson(playlist.tracks, typeToken),
            playlist.numberOfTracks
        )
    }

    fun map(track: Track): TrackInPlaylistEntity {
        return TrackInPlaylistEntity(
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackTimeMillis,
            track.artworkUrl100,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl
        )
    }

    fun map(list: List<String>): String = gson.toJson(list)

}

