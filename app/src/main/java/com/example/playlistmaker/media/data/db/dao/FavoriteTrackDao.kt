package com.example.playlistmaker.media.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.media.data.db.entity.FavoriteTrackEntity

@Dao
interface FavoriteTrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteTrack(track: FavoriteTrackEntity)

    @Delete
    suspend fun deleteFavoriteTrack(track: FavoriteTrackEntity)

    @Query("SELECT * FROM favorites_table order by insertionTime desc")
    suspend fun getFavoriteTracks(): List<FavoriteTrackEntity>

    @Query("SELECT trackId FROM favorites_table")
    suspend fun getFavoriteTracksId(): List<Int>
}