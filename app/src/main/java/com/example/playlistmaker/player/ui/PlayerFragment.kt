package com.example.playlistmaker.player.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.media.view_model.PlayListsState
import com.example.playlistmaker.player.view_model.PlayerScreenState.Companion.PLAY
import com.example.playlistmaker.player.view_model.PlayerViewModel
import com.example.playlistmaker.player.view_model.TrackInPlaylistState
import com.example.playlistmaker.search.domain.models.Track
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerFragment : Fragment() {
    companion object {
        private const val TRACK_KEY = "track_key"
        private const val ZERO = 0
        private const val FOUR = 4
        fun createArgs(trackJson: String): Bundle = bundleOf(TRACK_KEY to trackJson)
    }

    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlayerViewModel by viewModel {
        parametersOf(track)
    }

    private val playlistsAdapter = PlayerBottomSheetAdapter { playlist ->
        viewModel.checkTrackInPlaylist(track, playlist)
    }

    private lateinit var track: Track

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonBack.setOnClickListener {
            findNavController().navigateUp()
        }

        track = Gson().fromJson(
            requireArguments().getString(TRACK_KEY),
            object : TypeToken<Track>() {}.type
        )

        Glide.with(this)
            .load(track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"))
            .placeholder(R.drawable.ic_placeholder)
            .transform(RoundedCorners(binding.cover.resources.getDimensionPixelSize(R.dimen.rounded_corners_big_cover)))
            .into(binding.cover)

        binding.trackName.text = track.trackName
        binding.artistName.text = track.artistName
        binding.trackDuration.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)

        if (track.releaseDate.isNullOrEmpty()) {
            binding.trackYear.text = ""
        } else {
            binding.trackYear.text = track.releaseDate?.substring(ZERO, FOUR)
        }
        binding.trackGenre.text = track.primaryGenreName
        binding.trackCountry.text = track.country

        if (track.collectionName.isNullOrEmpty()) {
            binding.albumTitle.visibility = View.GONE
            binding.trackAlbum.visibility = View.GONE
        } else {
            binding.trackAlbum.text = track.collectionName
        }

        val bottomSheetBehavior =
            BottomSheetBehavior.from(binding.linearLayoutBottomSheetContainer).apply {
                state = BottomSheetBehavior.STATE_HIDDEN
            }

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.isVisible = false
                        binding.buttonBack.isEnabled = true
                        val callback = object : OnBackPressedCallback(true) {
                            override fun handleOnBackPressed() {
                                findNavController().navigateUp()
                            }

                        }
                        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, callback)
                    }

                    else -> {
                        binding.overlay.isVisible = true
                        binding.buttonBack.isEnabled = false
                        val callback = object : OnBackPressedCallback(true) {
                            override fun handleOnBackPressed() {
                                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                            }

                        }
                        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, callback)
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        })

        viewModel.observeState().observe(viewLifecycleOwner) {
            binding.playButton.isEnabled = it.isPlayButtonEnabled
            binding.playbackTime.text = it.progress
            setPlayButtonImage(it.buttonText)
        }
        viewModel.observeFavorite().observe(viewLifecycleOwner) { isFavorite ->
            setFavoriteButtonImage(isFavorite)
        }

        binding.recyclerViewBottomSheet.adapter = playlistsAdapter

        viewModel.observePlaylists().observe(viewLifecycleOwner) { state ->
            when (state) {
                is PlayListsState.Empty -> {}
                is PlayListsState.Content -> {
                    showContent(state.playlists)

                }
            }
        }

        viewModel.observeTrackInPlaylist().observe(viewLifecycleOwner) { state ->
            when (state) {
                is TrackInPlaylistState.Contained -> {
                    Toast.makeText(
                        requireContext(),
                        (getString(R.string.track_contained, state.playlist.name)),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is TrackInPlaylistState.Added -> {
                    Toast.makeText(
                        requireContext(),
                        (getString(R.string.track_added, state.playlist.name)),
                        Toast.LENGTH_SHORT
                    ).show()
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                }
            }
        }

        binding.favoriteButton.setOnClickListener {
            viewModel.onFavoriteClicked()
        }

        viewModel.preparePlayer()
        binding.playButton.setOnClickListener {
            viewModel.playbackControl()
        }


        binding.imageViewAddToPlaylist.setOnClickListener {
            viewModel.addToPlaylist()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.buttonCreatePlaylist.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            findNavController().navigate(R.id.action_playerFragment_to_newPlaylistFragment)
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.releasePlayer()
    }

    private fun setPlayButtonImage(buttonText: String) {
        if (buttonText == PLAY) {
            binding.playButton.setImageDrawable(
                AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.ic_play
                )
            )
        } else {
            binding.playButton.setImageDrawable(
                AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.ic_pause
                )
            )
        }
    }

    private fun setFavoriteButtonImage(isFavorite: Boolean) {
        if (isFavorite) {
            binding.favoriteButton.setImageDrawable(
                AppCompatResources.getDrawable(
                    requireContext(), R.drawable.ic_not_favorite
                )
            )
        } else {
            binding.favoriteButton.setImageDrawable(
                AppCompatResources.getDrawable(
                    requireContext(), R.drawable.ic_favorite
                )
            )
        }
    }

    private fun showContent(playlists: List<Playlist>) {
        playlistsAdapter.playlists.clear()
        playlistsAdapter.playlists.addAll(playlists)
        playlistsAdapter.notifyDataSetChanged()
    }

    override fun onResume() {
        viewModel.fillData()
        super.onResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}