package com.example.playlistmaker.media.data

import com.example.playlistmaker.media.data.converters.PlaylistDbConverter
import com.example.playlistmaker.media.data.db.AppDatabase
import com.example.playlistmaker.media.data.db.entity.PlaylistEntity
import com.example.playlistmaker.media.data.db.entity.TrackInPlaylistEntity
import com.example.playlistmaker.media.domain.api.PlayListsRepository
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

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

    override suspend fun getPlaylist(id: Int): Playlist = withContext(Dispatchers.IO) {
        playlistDbConverter.map(database.playlistDao().getPlaylistById(id))
    }

    override fun getTracksFromPlaylist(tracks: List<String>): Flow<List<Track>> {
        val list = database.trackInPlaylistDao().getTracksFromPlaylist().map {
            convertToTracklist(it)
        }
        return list.map { it.filter { tracks.contains(it.trackId) } }
    }

    override suspend fun deleteTrack(trackId: String, playlistId: Int): Flow<List<Track>> {
        val playlist = playlistDbConverter.map(database.playlistDao().getPlaylistById(playlistId))
        var temp = playlist.copy(tracks = playlist.tracks.filterNot { it.contains(trackId) })
        temp = temp.copy(numberOfTracks = temp.tracks.size)
        database.playlistDao().updateList(playlistDbConverter.map(temp))
        deleteTrackFromPlaylist(trackId, playlistId)
        val list =
            database.trackInPlaylistDao().getTracksFromPlaylist().map { convertToTracklist(it) }
        return list.map { tracks -> tracks.filter { track -> temp.tracks.contains(track.trackId) } }
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        val tracklist = convertToTracklist(database.trackInPlaylistDao().getTracksEntity())
        for (track in tracklist) {
            deleteTrackFromPlaylist(track.trackId, playlist.id)
        }
        database.playlistDao().deletePlaylist(playlistDbConverter.map(playlist))
    }

    override suspend fun editPlaylist(playlist: Playlist) {
        database.playlistDao().updateList(playlistDbConverter.map(playlist))
    }


    private fun convertToPlaylist(playlists: List<PlaylistEntity>): List<Playlist> =
        playlists.map { playlist -> playlistDbConverter.map(playlist) }

    private fun convertToTracklist(trackList: List<TrackInPlaylistEntity>): List<Track> {
        return trackList.map { track -> playlistDbConverter.map(track) }
    }

    private suspend fun deleteTrackFromPlaylist(trackId: String, playlistId: Int) {
        var mustBeDeleted = true
        val temp: List<Playlist> =
            database.playlistDao().getPlaylistsEntity().map { playlistDbConverter.map(it) }
        for (playlist in temp) {
            if (playlist.tracks.contains(trackId) && playlist.id != playlistId) {
                mustBeDeleted = false
                break
            }
        }
        if (mustBeDeleted) {
            database.trackInPlaylistDao().deleteTrack(trackId)
        }
    }
}