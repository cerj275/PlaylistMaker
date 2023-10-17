package com.example.playlistmaker.search.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.util.Creator

class SearchViewModel(
    application: Application
) : AndroidViewModel(application) {

    companion object {
        fun getModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SearchViewModel(this[APPLICATION_KEY] as Application)
            }
        }
    }

    private val interactor = Creator.provideTracksInteractor(application)

    private val stateLiveData = MutableLiveData<SearchScreenState>()

    fun observeState(): LiveData<SearchScreenState> = stateLiveData
    private fun renderState(state: SearchScreenState) {
        stateLiveData.postValue(state)
    }

    fun onTextChanged(searchText: String?) {
        if (searchText.isNullOrEmpty()) {
            if (interactor.readSearchHistory().isNotEmpty()) { renderState(
                SearchScreenState.HistoryContent(
                    interactor.readSearchHistory()
                )
            ) } else { renderState(SearchScreenState.EmptySearch) }
        }
    }
    fun onFocusChanged(hasFocus: Boolean, searchText: String) {
        if (hasFocus && searchText.isEmpty() && interactor.readSearchHistory().isNotEmpty()) {
            renderState(SearchScreenState.HistoryContent(interactor.readSearchHistory()))
        }
    }

    fun setShowingHistoryContent() {
        if (interactor.readSearchHistory().isNotEmpty()) {
            renderState(SearchScreenState.HistoryContent(interactor.readSearchHistory()))
        }
    }
    fun clearSearchHistory() {
        interactor.clearSearchHistory()
        renderState(SearchScreenState.EmptyScreen)
    }

    fun refreshSearchButton(searchText: String) {
        searchRequest(searchText)
    }

    fun onTrackPressed(track: Track) {
        interactor.addTrackToSearchHistory(track)
    }

    fun searchRequest(searchText: String) {
        if (searchText.isNotEmpty()) {
            renderState(SearchScreenState.Loading)
            interactor.searchTracks(searchText, object : TracksInteractor.TracksConsumer {
                override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
                    if (foundTracks != null) {
                        if (foundTracks.isNotEmpty()) {
                            renderState(SearchScreenState.SearchContent(foundTracks))
                        } else {
                            renderState(
                                SearchScreenState.EmptySearch
                            )
                        }
                    }
                    if (errorMessage != null) {
                        renderState(SearchScreenState.Error(errorMessage))
                    }
                }
            })
        }
    }
}