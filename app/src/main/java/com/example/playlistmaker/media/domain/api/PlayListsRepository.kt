package com.example.playlistmaker.media.domain.api

import com.example.playlistmaker.media.domain.model.Playlist
import kotlinx.coroutines.flow.Flow

interface PlayListsRepository {
    fun getPlaylists(): Flow<List<Playlist>>
    suspend fun addPlaylist(playlist: Playlist)
    suspend fun deletePlaylist(playlist: Playlist)
//    suspend fun addNewTrack(track: Track, playlist: Playlist)
}