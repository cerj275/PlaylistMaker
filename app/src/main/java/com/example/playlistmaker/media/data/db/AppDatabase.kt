package com.example.playlistmaker.media.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.media.data.db.dao.FavoriteTrackDao
import com.example.playlistmaker.media.data.db.dao.PlaylistDao
import com.example.playlistmaker.media.data.db.dao.TrackInPlaylistDao
import com.example.playlistmaker.media.data.db.entity.FavoriteTrackEntity
import com.example.playlistmaker.media.data.db.entity.PlaylistEntity
import com.example.playlistmaker.media.data.db.entity.TrackInPlaylistEntity

@Database(
    version = 3,
    entities = [FavoriteTrackEntity::class, PlaylistEntity::class, TrackInPlaylistEntity::class]
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun favoriteTrackDao(): FavoriteTrackDao
    abstract fun playlistDao(): PlaylistDao

    abstract fun trackInPlaylistDao(): TrackInPlaylistDao
}