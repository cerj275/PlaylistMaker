package com.example.playlistmaker.media.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.api.NewPlaylistInteractor
import com.example.playlistmaker.media.domain.api.PlayListsInteractor
import com.example.playlistmaker.media.domain.model.Playlist
import kotlinx.coroutines.launch

class NewPlaylistViewModel(
    private val interactor: PlayListsInteractor,
    private val newPlaylistInteractor: NewPlaylistInteractor
) : ViewModel() {


//    private var coverUri: Uri? = null
    private val stateLiveData = MutableLiveData<NewPlaylistScreenState>()
    fun observeState(): LiveData<NewPlaylistScreenState> = stateLiveData

    fun createPlaylist(playlist: Playlist) {
        viewModelScope.launch {

//            if (coverUri != null) {
//                newPlaylistInteractor.saveImageToPrivateStorage(coverUri!!, playlist.name)
//            }
            interactor.addPlaylist(playlist)
            interactor.getPlaylists()
        }
    }
}