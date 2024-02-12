package com.example.playlistmaker.media.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.media.data.db.entity.TrackInPlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackInPlaylistDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrackToPlaylist(track: TrackInPlaylistEntity)

    @Query("SELECT * FROM tracks_in_playlist_table")
    fun getTracksFromPlaylist(): Flow<List<TrackInPlaylistEntity>>

    @Query("DELETE FROM tracks_in_playlist_table where trackId =:trackId")
    suspend fun deleteTrack(trackId: String)

    @Query("SELECT*FROM tracks_in_playlist_table")
    fun getTracksEntity(): List<TrackInPlaylistEntity>
}