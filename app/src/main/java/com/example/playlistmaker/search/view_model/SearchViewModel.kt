package com.example.playlistmaker.search.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel(
    private val interactor: TracksInteractor
) : ViewModel() {
    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 500L
    }

    private val stateLiveData = MutableLiveData<SearchScreenState>()
    private var searchJob: Job? = null
    private var lastSearchText: String? = null

    fun observeState(): LiveData<SearchScreenState> = stateLiveData
    private fun renderState(state: SearchScreenState) {
        stateLiveData.postValue(state)
    }

    private var returnedFromPlayer: Boolean = false
    private var lastUnsuccessfulSearch: String = ""
    fun setReturnedFromPlayer(boolean: Boolean) {
        returnedFromPlayer = boolean
    }

    fun getReturnedFromPlayer(): Boolean {
        return returnedFromPlayer
    }

    fun onResume() {
        setShowingHistoryContent()
    }

    fun onTextChanged(searchText: String?) {
        if (searchText.isNullOrEmpty()) {
            setShowingHistoryContent()
        }
    }

    fun onFocusChanged(hasFocus: Boolean, searchText: String) {
        if (hasFocus && searchText.isEmpty()) {
            setShowingHistoryContent()
        }
    }

    fun setShowingHistoryContent() {
        viewModelScope.launch {
            interactor.readSearchHistory().collect {
                if (it.isNotEmpty()) {
                    renderState(SearchScreenState.HistoryContent(it))
                } else {
                    renderState(SearchScreenState.EmptyScreen)
                }
            }
        }
    }

    fun searchDebounce(searchText: String) {
        if (lastSearchText == searchText) return
        lastSearchText == searchText
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            searchRequest(searchText)
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
            viewModelScope.launch {
                interactor
                    .searchTracks(searchText)
                    .collect { pair ->
                        val foundTracks = ArrayList<Track>()
                        if (pair.first != null) {
                            foundTracks.addAll(pair.first!!)
                            if (foundTracks.isNotEmpty()) {
                                renderState(SearchScreenState.SearchContent(foundTracks))
                            } else {
                                renderState(
                                    SearchScreenState.EmptySearch
                                )
                            }
                        }
                        if (pair.second != null) {
                            renderState(SearchScreenState.Error(pair.second!!))
                            lastUnsuccessfulSearch = searchText
                        }
                    }
            }
        }
    }
}
