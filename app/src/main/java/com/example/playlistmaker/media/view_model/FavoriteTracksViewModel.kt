package com.example.playlistmaker.media.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.db.FavoriteTracksInteractor
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.launch

class FavoriteTracksViewModel(private val interactor: FavoriteTracksInteractor) : ViewModel() {

    private val stateLiveData = MutableLiveData<FavoriteTracksScreenState>()
    fun  observeState(): LiveData<FavoriteTracksScreenState> = stateLiveData

    fun fillData() {
        viewModelScope.launch {
            interactor.getFavoriteTracks().collect { tracks ->
                processResult(tracks)
            }
        }
    }

    private fun processResult(tracks: List<Track>) {
        if (tracks.isEmpty()) {
            renderState(FavoriteTracksScreenState.Empty)
        } else {
            renderState(FavoriteTracksScreenState.Content(tracks))
        }
    }

    private fun renderState(state: FavoriteTracksScreenState) {
        stateLiveData.postValue(state)
    }
}