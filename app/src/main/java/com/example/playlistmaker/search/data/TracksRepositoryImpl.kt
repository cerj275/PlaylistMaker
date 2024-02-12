package com.example.playlistmaker.search.data

import com.example.playlistmaker.media.data.db.AppDatabase
import com.example.playlistmaker.search.data.dto.TracksSearchRequest
import com.example.playlistmaker.search.data.dto.TracksSearchResponse
import com.example.playlistmaker.search.domain.api.SearchHistory
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
    private val localStorage: SearchHistory,
    private val appDatabase: AppDatabase
) : TracksRepository {
    override fun searchTracks(expression: String): Flow<Resource<List<Track>>> = flow {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        when (response.resultCode) {
            -1 -> {
                emit(Resource.Error("Проверьте подключение к интернету"))
            }
            200 -> {
                val favoriteTracksId = appDatabase.favoriteTrackDao().getFavoriteTracksId()
                with(response as TracksSearchResponse) {
                    val data = results.map {
                        Track(
                            it.trackId,
                            it.trackName,
                            it.artistName,
                            it.trackTimeMillis,
                            it.artworkUrl60,
                            it.artworkUrl100,
                            it.collectionName,
                            it.releaseDate,
                            it.primaryGenreName,
                            it.country,
                            it.previewUrl,
                            favoriteTracksId.contains(it.trackId)
                        )
                    }
                    emit(Resource.Success(data))
                }
            }

            else -> {
                emit(Resource.Error("Ошибка сервера"))
            }
        }
    }

    override fun readSearchHistory(): Flow<List<Track>> = flow {
        val searchHistory = localStorage.readSearchHistory()
        val favoriteTracksId = appDatabase.favoriteTrackDao().getFavoriteTracksId()
        emit(searchHistory.map {
            it.copy(isFavorite = favoriteTracksId.contains(it.trackId))
        })
    }

    override fun addTrackToSearchHistory(track: Track) {
        localStorage.addTrackToSearchHistory(track)
    }

    override fun clearSearchHistory() {
        localStorage.clearSearchHistory()
    }
}