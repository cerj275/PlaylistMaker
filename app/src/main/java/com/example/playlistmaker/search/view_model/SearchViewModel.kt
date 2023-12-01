package com.example.playlistmaker.search.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.models.Track

class SearchViewModel(
    private val interactor: TracksInteractor
) : ViewModel() {

    private val stateLiveData = MutableLiveData<SearchScreenState>()

    fun observeState(): LiveData<SearchScreenState> = stateLiveData
    private fun renderState(state: SearchScreenState) {
        stateLiveData.postValue(state)
    }

    private var returnedFromPlayer: Boolean = false
    private var lastUnsuccessfulSearch: String = ""
    private var isScreenPaused: Boolean = true
    fun setReturnedFromPlayer(boolean: Boolean) {
        returnedFromPlayer = boolean
    }

    fun getReturnedFromPlayer(): Boolean{
        return returnedFromPlayer
    }

    fun onResume() {
        isScreenPaused = false
        setShowingHistoryContent()
    }

    fun onPause() {
        isScreenPaused = true
    }

    fun onTextChanged(searchText: String?) {
        if (searchText.isNullOrEmpty()) {
            if (interactor.readSearchHistory().isNotEmpty()) {
                renderState(
                    SearchScreenState.HistoryContent(
                        interactor.readSearchHistory()
                    )
                )
            } else {
                renderState(SearchScreenState.EmptyScreen)
            }
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
        } else {
            renderState(SearchScreenState.EmptyScreen)
        }
    }

    fun clearSearchHistory() {
        interactor.clearSearchHistory()
        renderState(SearchScreenState.EmptyScreen)
    }

    fun refreshSearchButton() {
        searchRequest(lastUnsuccessfulSearch)
    }

    fun onTrackPressed(track: Track) {
        interactor.addTrackToSearchHistory(track)
        returnedFromPlayer = true
    }

    fun searchRequest(searchText: String) {
        returnedFromPlayer = false
        if (searchText.isNotEmpty()) {
            renderState(SearchScreenState.Loading)
            interactor.searchTracks(searchText, object : TracksInteractor.TracksConsumer {
                override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
                    if (!isScreenPaused) {
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
                            lastUnsuccessfulSearch = searchText
                        }
                    }
                }
            })
        }
    }
}
