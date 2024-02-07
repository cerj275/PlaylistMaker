package com.example.playlistmaker.media.data

import com.example.playlistmaker.media.data.converters.PlaylistDbConverter
import com.example.playlistmaker.media.data.db.AppDatabase
import com.example.playlistmaker.media.data.db.entity.PlaylistEntity
import com.example.playlistmaker.media.domain.api.PlayListsRepository
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlayListsRepositoryImpl(
    private val database: AppDatabase,
    private val playlistDbConverter: PlaylistDbConverter,
) : PlayListsRepository {

    override suspend fun addPlaylist(playlist: Playlist) {
        database.playlistDao().insertPlaylist(playlistDbConverter.map(playlist))
    }

    override fun getPlaylists(): Flow<List<Playlist>> = database.playlistDao().getPlaylists().map {
        convertToPlaylist(it)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        database.playlistDao().updateNumberOfTracks(
            id = playlist.id,
            tracks = playlistDbConverter.map(playlist.tracks),
            numberOfTracks = playlist.numberOfTracks
        )
    }

    override suspend fun addNewTrackToPlaylist(track: Track) {
        database.trackInPlaylistDao().insertTrackToPlaylist(playlistDbConverter.map(track))
    }

    private fun convertToPlaylist(playlists: List<PlaylistEntity>): List<Playlist> =
        playlists.map { playlist -> playlistDbConverter.map(playlist) }
}