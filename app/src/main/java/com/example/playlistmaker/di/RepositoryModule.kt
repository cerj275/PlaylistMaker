package com.example.playlistmaker.di

import com.example.playlistmaker.media.data.FavoriteTracksRepositoryImpl
import com.example.playlistmaker.media.data.NewPlaylistRepositoryImpl
import com.example.playlistmaker.media.data.PlayListsRepositoryImpl
import com.example.playlistmaker.media.data.converters.FavoriteTrackDbConverter
import com.example.playlistmaker.media.data.converters.PlaylistDbConverter
import com.example.playlistmaker.media.domain.api.FavoriteTracksRepository
import com.example.playlistmaker.media.domain.api.NewPlaylistRepository
import com.example.playlistmaker.media.domain.api.PlayListsRepository
import com.example.playlistmaker.player.data.PlayerRepositoryImpl
import com.example.playlistmaker.player.domain.api.PlayerRepository
import com.example.playlistmaker.search.data.TracksRepositoryImpl
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.settings.data.impl.SettingsRepositoryImpl
import com.example.playlistmaker.settings.domain.api.SettingsRepository
import org.koin.dsl.module


val repositoryModule = module {
    single<TracksRepository> {
        TracksRepositoryImpl(get(), get(), get())
    }

    factory<PlayerRepository> {
        PlayerRepositoryImpl(get())
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(get())
    }
    factory { FavoriteTrackDbConverter() }

    single<FavoriteTracksRepository> {
        FavoriteTracksRepositoryImpl(get(), get())
    }

    factory { PlaylistDbConverter() }

    single<PlayListsRepository> {
        PlayListsRepositoryImpl(get(), get())
    }

    single<NewPlaylistRepository> {
        NewPlaylistRepositoryImpl(get())
    }
}