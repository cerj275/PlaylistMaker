package com.example.playlistmaker.di

import com.example.playlistmaker.media.data.FavoriteTracksRepositoryImpl
import com.example.playlistmaker.media.data.converters.FavoriteTrackDbConverter
import com.example.playlistmaker.media.domain.db.FavoriteTracksRepository
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
}