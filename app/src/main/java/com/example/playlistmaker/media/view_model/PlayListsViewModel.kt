package com.example.playlistmaker.media.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.api.PlayListsInteractor
import com.example.playlistmaker.media.domain.model.Playlist
import kotlinx.coroutines.launch

class PlayListsViewModel(private val interactor: PlayListsInteractor) : ViewModel() {

    init {
        fillData()
    }

    private val stateLiveData = MutableLiveData<PlayListsState>()

    fun observeState(): LiveData<PlayListsState> = stateLiveData

    fun fillData() {
        viewModelScope.launch {
            interactor
                .getPlaylists()
                .collect {
                    processResult(it)
                }
        }
    }

    private fun processResult(playlists: List<Playlist>) {
        if (playlists.isEmpty()) {
            renderState(PlayListsState.Empty)
        } else {
            renderState(PlayListsState.Content(playlists))
        }
    }

    private fun renderState(state: PlayListsState) {
        stateLiveData.postValue(state)
    }
}