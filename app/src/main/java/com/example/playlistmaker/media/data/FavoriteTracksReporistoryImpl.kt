package com.example.playlistmaker.media.data

import com.example.playlistmaker.media.data.converters.FavoriteTrackDbConverter
import com.example.playlistmaker.media.data.db.AppDatabase
import com.example.playlistmaker.media.data.db.entity.FavoriteTrackEntity
import com.example.playlistmaker.media.domain.api.FavoriteTracksRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoriteTracksRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val favoriteTrackDbConverter: FavoriteTrackDbConverter
) : FavoriteTracksRepository {
    override suspend fun addFavoriteTrack(track: Track) {
        appDatabase.favoriteTrackDao().insertFavoriteTrack(favoriteTrackDbConverter.map(track))
    }

    override suspend fun deleteFavoriteTrack(track: Track) {
        appDatabase.favoriteTrackDao().deleteFavoriteTrack((favoriteTrackDbConverter.map(track)))
    }

    override fun getFavoriteTracks(): Flow<List<Track>> = flow {
        val tracks = appDatabase.favoriteTrackDao().getFavoriteTracks()
        emit(convertFromTrackEntity(tracks))
    }

    private fun convertFromTrackEntity(tracks: List<FavoriteTrackEntity>): List<Track> {
        return tracks.map { track -> favoriteTrackDbConverter.map(track) }
    }
}