package com.example.playlistmaker.media.domain.api

import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlayListsInteractor {

    fun getPlaylists(): Flow<List<Playlist>>
    suspend fun addPlaylist(playlist: Playlist)
    suspend fun updatePlaylist(playlist: Playlist)
    suspend fun addNewTrackToPlaylist(track: Track)
    suspend fun getPlaylist(id: Int): Playlist
    fun getTracksFromPlaylist(tracks: List<String>): Flow<List<Track>>
    suspend fun deleteTrack(trackId: String, playlistId: Int): Flow<List<Track>>
    suspend fun deletePlaylist(playlist: Playlist)
    suspend fun editPlaylist(playlist: Playlist)
}