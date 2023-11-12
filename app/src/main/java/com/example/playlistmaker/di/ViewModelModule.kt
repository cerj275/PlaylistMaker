package com.example.playlistmaker.di

import com.example.playlistmaker.media.view_model.FavoritesViewModel
import com.example.playlistmaker.media.view_model.PlayListsViewModel
import com.example.playlistmaker.player.view_model.PlayerViewModel
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.view_model.SearchViewModel
import com.example.playlistmaker.settings.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        SearchViewModel(get())
    }

    viewModel { (track: Track) ->
        PlayerViewModel(track, get())
    }

    viewModel {
        SettingsViewModel(get(), get())
    }

    viewModel {
        FavoritesViewModel()
    }
    viewModel {
        PlayListsViewModel()
    }
}