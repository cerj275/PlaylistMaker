package com.example.playlistmaker.di

import com.example.playlistmaker.search.data.TracksRepositoryImpl
import com.example.playlistmaker.search.domain.api.TracksRepository
import org.koin.dsl.module


val repositoryModule = module {
    single<TracksRepository> {
        TracksRepositoryImpl(get(), get())
    }
}