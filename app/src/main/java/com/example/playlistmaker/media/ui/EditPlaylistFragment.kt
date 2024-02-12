package com.example.playlistmaker.media.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.media.view_model.EditPlaylistViewModel
import com.example.playlistmaker.media.view_model.PlaylistDetailsState
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditPlaylistFragment : NewPlaylistFragment() {
    companion object {
        fun createArgs(playlist: Playlist, playlistId: Int): Bundle =
            bundleOf(PLAYLIST_KEY to playlist, PLAYLIST_ID_KEY to playlistId)

        private const val PLAYLIST_KEY = "playlist_key"
        private const val PLAYLIST_ID_KEY = "playlist_id_key"

    }

    private lateinit var playlist: Playlist
    override val viewModel: EditPlaylistViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        playlist = requireArguments().getParcelable<Playlist>(PLAYLIST_KEY) as Playlist
        super.onViewCreated(view, savedInstanceState)

        viewModel.fillData(playlist)
        viewModel.observePlayListLiveData().observe(viewLifecycleOwner) { state ->
            render(state)
        }

        binding.buttonBack.setOnClickListener {
            findNavController().navigateUp()
        }

        pickMedia()

        binding.buttonCreateNewPlaylist.setOnClickListener {
            viewModel.editPlaylist(playlist)
            Toast.makeText(
                requireContext(),
                resources.getString(R.string.playlist_saved, binding.editTextPlaylistName.text),
                Toast.LENGTH_SHORT
            ).show()
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.editPlaylistFragment, true)
                .build()
            findNavController().navigate(
                R.id.action_editPlaylistFragment_to_playlistDetailsFragment,
                createArgs(playlist, playlist.id), navOptions
            )
        }

        binding.editTextPlaylistName.doOnTextChanged { text, start, before, count ->
            text.let { binding.buttonCreateNewPlaylist.isEnabled = !text.isNullOrEmpty() }
            viewModel.getEditedName(text)
        }

        binding.editTextPlaylistDescription.doOnTextChanged { text, start, before, count ->
            viewModel.getEditedDescription(text)
        }
    }

    private fun render(state: PlaylistDetailsState) {
        Glide.with(binding.imageViewAddCover)
            .load(playlist.coverUri)
            .placeholder(R.drawable.ic_placeholder)
            .centerCrop()
            .into(binding.imageViewAddCover)
        binding.editTextPlaylistName.setText(state.name)
        binding.editTextPlaylistDescription.setText(state.description)
        binding.buttonCreateNewPlaylist.text = resources.getString(R.string.save)
        binding.textViewTittle.text = resources.getString(R.string.edit)
    }
}
