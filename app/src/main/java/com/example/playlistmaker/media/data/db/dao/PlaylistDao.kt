package com.example.playlistmaker.media.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.media.data.db.entity.PlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity)

    @Query("SELECT * FROM playlists_table")
    fun getPlaylists(): Flow<List<PlaylistEntity>>

    @Query("UPDATE playlists_table SET tracks=:tracks, numberOfTracks = :numberOfTracks WHERE id = :id")
    suspend fun updateNumberOfTracks(tracks: String, numberOfTracks: Int, id: Int)
}
