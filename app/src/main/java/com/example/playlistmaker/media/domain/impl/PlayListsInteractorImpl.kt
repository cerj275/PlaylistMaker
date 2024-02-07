package com.example.playlistmaker.media.domain.impl

import com.example.playlistmaker.media.domain.api.PlayListsInteractor
import com.example.playlistmaker.media.domain.api.PlayListsRepository
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class PlayListsInteractorImpl(private val repository: PlayListsRepository) :
    PlayListsInteractor {
    override fun getPlaylists(): Flow<List<Playlist>> {
        return repository.getPlaylists()
    }

    override suspend fun addPlaylist(playlist: Playlist) {
        repository.addPlaylist(playlist)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        repository.updatePlaylist(playlist)
    }

    override suspend fun addNewTrackToPlaylist(track: Track) {
        repository.addNewTrackToPlaylist(track)
    }

}