package com.example.playlistmaker.media.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.media.data.db.dao.FavoriteTrackDao
import com.example.playlistmaker.media.data.db.dao.PlaylistDao
import com.example.playlistmaker.media.data.db.entity.FavoriteTrackEntity
import com.example.playlistmaker.media.data.db.entity.PlaylistEntity

@Database(version = 2, entities = [FavoriteTrackEntity::class, PlaylistEntity::class])
abstract class AppDatabase : RoomDatabase() {

    abstract fun favoriteTrackDao(): FavoriteTrackDao
    abstract fun playlistDao(): PlaylistDao
}