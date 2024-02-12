package com.example.playlistmaker.media.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites_table")
data class FavoriteTrackEntity(
    @PrimaryKey
    val trackId: String,
    val trackName: String,
    val artistName: String?,
    val trackTimeMillis: Long,
    val artworkUrl60: String,
    val artworkUrl100: String,
    val collectionName: String,
    val releaseDate: String?,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String?,
    val insertionTime: Long
)