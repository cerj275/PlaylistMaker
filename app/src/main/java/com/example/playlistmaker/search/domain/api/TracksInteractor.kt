package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface TracksInteractor {
    fun searchTracks(expression: String): Flow<Pair<List<Track>?, String?>>
    fun readSearchHistory(): Flow<List<Track>>
    fun addTrackToSearchHistory(track: Track)
    fun clearSearchHistory()
}