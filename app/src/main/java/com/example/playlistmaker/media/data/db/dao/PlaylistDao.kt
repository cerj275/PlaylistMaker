package com.example.playlistmaker.media.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.playlistmaker.media.data.db.entity.PlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity)
    @Query("SELECT * FROM playlists_table")
    fun getPlaylists(): Flow<List<PlaylistEntity>>
    @Query("SELECT*FROM playlists_table")
    fun getPlaylistsEntity(): List<PlaylistEntity>
    @Query("UPDATE playlists_table SET tracks=:tracks, numberOfTracks = :numberOfTracks WHERE id = :id")
    suspend fun updateNumberOfTracks(tracks: String, numberOfTracks: Int, id: Int)
    @Query("SELECT * FROM playlists_table WHERE id = :id")
    suspend fun getPlaylistById(id: Int): PlaylistEntity
    @Update
    suspend fun updateList(playlist: PlaylistEntity)
    @Delete
    suspend fun deletePlaylist(playlist: PlaylistEntity)
}
