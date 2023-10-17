package com.example.playlistmaker.search.view_model

import com.example.playlistmaker.search.domain.models.Track

sealed interface SearchScreenState {

    object Loading : SearchScreenState
    data class SearchContent(val tracks: List<Track>) : SearchScreenState
    data class HistoryContent(val tracks: List<Track>) : SearchScreenState
    data class Error(val errorMessage: String) : SearchScreenState
    object EmptySearch : SearchScreenState
    object EmptyScreen : SearchScreenState
}
