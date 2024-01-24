package com.example.playlistmaker.player.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.media.view_model.PlayListsState
import com.example.playlistmaker.player.view_model.PlayerScreenState.Companion.PLAY
import com.example.playlistmaker.player.view_model.PlayerViewModel
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
        const val TRACK_KEY = "track_key"
        fun createArgs(trackJson: String): Bundle = bundleOf(TRACK_KEY to trackJson)
    }

    private val viewModel: PlayerViewModel by viewModel {
        parametersOf(track)
    }
    private val playlistsAdapter = PlayerBottomSheetAdapter {
        viewModel.onPlaylistClicked(it)
    }

    private lateinit var track: Track
    private lateinit var ibBackButton: ImageButton
    private lateinit var ivAlbumCover: ImageView
    private lateinit var tvTrackName: TextView
    private lateinit var tvArtistName: TextView
    private lateinit var tvDuration: TextView
    private lateinit var tvAlbum: TextView
    private lateinit var tvTrackAlbum: TextView
    private lateinit var tvYear: TextView
    private lateinit var tvGenre: TextView
    private lateinit var tvCountry: TextView
    private lateinit var tvPlayBackTime: TextView
    private lateinit var ivPlayButton: ImageView
    private lateinit var ivFavoriteButton: ImageView
    private lateinit var rvPlayerPlaylists: RecyclerView
    private lateinit var bottomSheetContainer: LinearLayout
    private lateinit var vOverlay: View
    private lateinit var ivAddToPlaylist: ImageView
    private lateinit var bCreatePlaylist: Button


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_player, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()

        ibBackButton.setOnClickListener {
            findNavController().navigateUp()
        }

        track = Gson().fromJson(
            requireArguments().getString(TRACK_KEY),
            object : TypeToken<Track>() {}.type
        )

        Glide.with(this)
            .load(track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"))
            .placeholder(R.drawable.ic_placeholder)
            .transform(RoundedCorners(ivAlbumCover.resources.getDimensionPixelSize(R.dimen.rounded_corners_big_cover)))
            .into(ivAlbumCover)

        tvTrackName.text = track.trackName
        tvArtistName.text = track.artistName
        tvDuration.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)

        if (track.releaseDate.isNullOrEmpty()) {
            tvYear.text = ""
        } else {
            tvYear.text = track.releaseDate?.substring(0, 4)
        }
        tvGenre.text = track.primaryGenreName
        tvCountry.text = track.country

        if (track.collectionName.isNullOrEmpty()) {
            tvAlbum.visibility = View.GONE
            tvTrackAlbum.visibility = View.GONE
        } else {
            tvTrackAlbum.text = track.collectionName
        }

        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> vOverlay.isVisible = false
                    else -> vOverlay.isVisible = true
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                vOverlay.alpha = slideOffset
            }
        })

        viewModel.observeState().observe(viewLifecycleOwner) {
            ivPlayButton.isEnabled = it.isPlayButtonEnabled
            tvPlayBackTime.text = it.progress
            setPlayButtonImage(it.buttonText)
        }
        viewModel.observeFavorite().observe(viewLifecycleOwner) { isFavorite ->
            setFavoriteButtonImage(isFavorite)
        }

        rvPlayerPlaylists.adapter = playlistsAdapter

        viewModel.observePlaylists().observe(viewLifecycleOwner) { state ->
            when (state) {
                is PlayListsState.Empty -> {}
                is PlayListsState.Content -> {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
                    vOverlay.isVisible = true
                    showContent(state.playlists)

                }
            }
        }

        ivFavoriteButton.setOnClickListener {
            viewModel.onFavoriteClicked()
        }

        viewModel.preparePlayer()
        ivPlayButton.setOnClickListener {
            viewModel.playbackControl()
        }


        ivAddToPlaylist.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        }

        bCreatePlaylist.setOnClickListener {
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

    private fun initViews() {
        ivAlbumCover = requireView().findViewById(R.id.cover)
        tvTrackName = requireView().findViewById(R.id.track_name)
        tvArtistName = requireView().findViewById(R.id.artist_name)
        tvDuration = requireView().findViewById(R.id.track_duration)
        tvAlbum = requireView().findViewById(R.id.album_title)
        tvTrackAlbum = requireView().findViewById(R.id.track_album)
        tvYear = requireView().findViewById(R.id.track_year)
        tvGenre = requireView().findViewById(R.id.track_genre)
        tvCountry = requireView().findViewById(R.id.track_country)
        tvPlayBackTime = requireView().findViewById(R.id.playback_time)
        ibBackButton = requireView().findViewById(R.id.buttonBack)
        ivPlayButton = requireView().findViewById(R.id.play_button)
        ivFavoriteButton = requireView().findViewById(R.id.favorite_button)
        bottomSheetContainer = requireView().findViewById(R.id.linearLayoutBottomSheetContainer)
        vOverlay = requireView().findViewById(R.id.overlay)
        ivAddToPlaylist = requireView().findViewById(R.id.imageViewAddToPlaylist)
        bCreatePlaylist = requireView().findViewById(R.id.buttonCreatePlaylist)
        rvPlayerPlaylists = requireView().findViewById(R.id.recyclerViewBottomSheet)
    }

    private fun setPlayButtonImage(buttonText: String) {
        if (buttonText == PLAY) {
            ivPlayButton.setImageDrawable(
                AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.ic_play
                )
            )
        } else {
            ivPlayButton.setImageDrawable(
                AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.ic_pause
                )
            )
        }
    }

    private fun setFavoriteButtonImage(isFavorite: Boolean) {
        if (isFavorite) {
            ivFavoriteButton.setImageDrawable(
                AppCompatResources.getDrawable(
                    requireContext(), R.drawable.ic_not_favorite
                )
            )
        } else {
            ivFavoriteButton.setImageDrawable(
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
}