package com.example.playlistmaker.media.view_model

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.api.NewPlaylistInteractor
import com.example.playlistmaker.media.domain.api.PlayListsInteractor
import com.example.playlistmaker.media.domain.model.Playlist
import kotlinx.coroutines.launch

open class NewPlaylistViewModel(
    val interactor: PlayListsInteractor,
    val newPlaylistInteractor: NewPlaylistInteractor
) : ViewModel() {

    protected open var name: String = ""
    protected open var coverUri: String = ""
    protected open var description: String = ""

    fun getName(text: CharSequence?) {
        name = text.toString()
    }

    fun getDescription(text: CharSequence?) {
        description = text.toString()
    }

    fun createPlaylist() {
        viewModelScope.launch {
            var newUri = ""
            if (coverUri.isNotEmpty()) {
                newUri =
                    newPlaylistInteractor.saveImageToPrivateStorage(Uri.parse(coverUri)).toString()
            }
            interactor.addPlaylist(
                Playlist(
                    name = name,
                    description = description,
                    coverUri = newUri
                )
            )
        }
    }

    fun getCoverUri(uri: Uri?) {
        coverUri = uri.toString()
    }
}