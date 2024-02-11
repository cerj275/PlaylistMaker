package com.example.playlistmaker.media.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistDetailsBinding
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.media.view_model.PlaylistDetailsState
import com.example.playlistmaker.media.view_model.PlaylistDetailsViewModel
import com.example.playlistmaker.player.ui.PlayerFragment
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.TrackAdapter
import com.example.playlistmaker.utils.Converter
import com.example.playlistmaker.utils.debounce
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlaylistDetailsFragment : Fragment() {
    companion object {
        private const val PLAYLIST_ID_KEY = "playlist_id_key"
        private const val PLAYLIST_KEY = "playlist_key"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        fun createArgs(playlistId: Int, playlist: Playlist): Bundle =
            bundleOf(PLAYLIST_ID_KEY to playlistId, PLAYLIST_KEY to playlist)

    }

    private var _binding: FragmentPlaylistDetailsBinding? = null
    private var playlistId: Int? = null
    private val binding get() = _binding!!
    private val viewModel: PlaylistDetailsViewModel by viewModel {
        parametersOf(playlistId)
    }
    private val trackAdapter = TrackAdapter(
        clickListener = { track -> onClickDebounce(track) },
        longClickListener = { track -> onLongClick(track) }
    )
    private lateinit var onClickDebounce: (Track) -> Unit
    private lateinit var onLongClick: (Track) -> Unit
    private lateinit var tracksInPlaylist: List<Track>
    private lateinit var playlist: Playlist

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playlistId = requireArguments().getInt(PLAYLIST_ID_KEY)
        playlist = requireArguments().getParcelable<Playlist>(PLAYLIST_KEY) as Playlist


        viewModel.observePlaylistLiveData().observe(viewLifecycleOwner) {
            render(it)
        }
        viewModel.getTracksFromPlaylist(playlist)

        viewModel.observeTracksLiveData().observe(viewLifecycleOwner) { tracks ->
            tracksInPlaylist = tracks
            binding.textViewPlaylistDuration.text = resources.getQuantityString(
                R.plurals.duration_of_tracks,
                Converter().convertTimeMillisToMinutes(tracks.sumOf { it.trackTimeMillis }),
                Converter().convertTimeMillisToMinutes(tracks.sumOf { it.trackTimeMillis })
            )
            binding.textViewPlaylistSize.text = resources.getQuantityString(
                R.plurals.number_of_tracks,
                tracks.size,
                tracks.size
            )
            trackAdapter.trackList.clear()
            trackAdapter.trackList.addAll(tracks)
            trackAdapter.notifyDataSetChanged()
            showTracksInPlaylist()
        }

        binding.buttonBack.setOnClickListener {
            findNavController().popBackStack(R.id.mediaLibraryFragment, false)
        }

        onClickDebounce = debounce(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { track ->
            findNavController().navigate(
                R.id.action_playlistDetailsFragment_to_playerFragment,
                PlayerFragment.createArgs(track.toString())
            )
        }

        onLongClick = {
            createConfirmDialog(
                context = requireContext(),
                tittle = resources.getString(R.string.delete_track),
                positiveButtonText = resources.getString(R.string.yes),
                negativeButtonText = resources.getString(R.string.no),
                positiveAction = { viewModel.deleteTrack(it.trackId, playlist.id) },
                negativeAction = {}
            )
        }

        binding.recyclerViewBottomSheet.adapter = trackAdapter

        val bottomSheetBehavior =
            BottomSheetBehavior.from(binding.linearLayoutMenuBottomSheetContainer).apply {
                state = BottomSheetBehavior.STATE_HIDDEN
            }

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> binding.overlay.isVisible = false
                    else -> binding.overlay.isVisible = true
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        })

        binding.buttonSharePlaylist.setOnClickListener { sharePlaylist() }
        binding.textViewPlaylistShare.setOnClickListener { sharePlaylist() }
        binding.textViewDeletePlaylist.setOnClickListener {
            createConfirmDialog(
                context = requireContext(),
                tittle = resources.getString(R.string.confirm_delete_playlist, playlist.name),
                positiveButtonText = resources.getString(R.string.yes),
                negativeButtonText = resources.getString(R.string.no),
                positiveAction = {
                    viewModel.deletePlaylist(playlist)
                    findNavController().navigateUp()
                },
                negativeAction = {}
            )
        }

        binding.buttonPlaylistOptions.setOnClickListener {
            Glide.with(requireContext())
                .load(playlist.coverUri)
                .placeholder(R.drawable.ic_placeholder)
                .centerInside()
                .transform(RoundedCorners(this.resources.getDimensionPixelSize(R.dimen.rounded_corners_big_cover)))
                .into(binding.playlistCover)
            binding.textViewPlaylistDetailsName.text = playlist.name
            binding.textViewPlaylistDuration.text = playlist.numberOfTracks.toString()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        }

        binding.textViewEditPlaylistInfo.setOnClickListener {
            findNavController().navigate(
                R.id.action_playlistDetailsFragment_to_editPlaylistFragment,
                EditPlaylistFragment.createArgs(playlist, playlistId!!)
            )
        }
    }

    private fun render(playlistDetailsState: PlaylistDetailsState) {
        Glide.with(binding.imageViewPlaylistCover)
            .load(playlistDetailsState.coverUri)
            .placeholder(R.drawable.ic_placeholder)
            .transform(CenterCrop())
            .into(binding.imageViewPlaylistCover)
        binding.textViewPlaylistName.text = playlistDetailsState.name
        binding.textViewPlaylistDescription.text = playlistDetailsState.description
    }

    private fun showTracksInPlaylist() {
        if (tracksInPlaylist.isEmpty()) {
            binding.linearLayoutEmptyPlaylist.isVisible = true
            binding.recyclerViewBottomSheet.isVisible = false
        } else {
            binding.linearLayoutEmptyPlaylist.isVisible = false
            binding.recyclerViewBottomSheet.isVisible = true
        }
    }

    private fun createConfirmDialog(
        context: Context,
        tittle: String,
        positiveButtonText: String,
        negativeButtonText: String,
        positiveAction: () -> Unit,
        negativeAction: () -> Unit
    ) {
        MaterialAlertDialogBuilder(context,R.style.DialogStyle)
            .setTitle(tittle)
            .setNegativeButton(negativeButtonText) { _, _ -> negativeAction.invoke() }
            .setPositiveButton(positiveButtonText) { _, _ -> positiveAction.invoke() }
            .show()
    }

    private fun sharePlaylist() {
        if (playlist.tracks.isEmpty()) {
            Toast.makeText(
                requireContext(),
                resources.getString(R.string.empty_playlist_to_share_message),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            viewModel.sharePlaylist()
        }
    }
}
