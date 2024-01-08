package com.example.playlistmaker.media.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.FragmentFavoriteTracksBinding
import com.example.playlistmaker.media.view_model.FavoriteTracksScreenState
import com.example.playlistmaker.media.view_model.FavoriteTracksViewModel
import com.example.playlistmaker.player.ui.PlayerActivity
import com.example.playlistmaker.player.ui.PlayerActivity.Companion.TRACK_KEY
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.TrackAdapter
import com.example.playlistmaker.utils.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteTracksFragment : Fragment() {

    companion object {
        fun newInstance() = FavoriteTracksFragment()
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private var _binding: FragmentFavoriteTracksBinding? = null
    private val binding get() = _binding!!
    private val favoriteTracksViewModel: FavoriteTracksViewModel by viewModel()
    private val favoriteTracksAdapter = TrackAdapter {
        onTrackClickDebounce(it)
    }
    private lateinit var onTrackClickDebounce: (Track) -> Unit
    private lateinit var rvFavoriteTracksList: RecyclerView
    private lateinit var llEmptyFavoriteTracks: LinearLayout


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvFavoriteTracksList = binding.recyclerViewFavoriteTracks
        llEmptyFavoriteTracks = binding.linearLayoutEmptyFavoriteTracks


        favoriteTracksViewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        onTrackClickDebounce = debounce<Track>(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { track ->
            startPlayerActivity(track)
        }

        rvFavoriteTracksList.adapter = favoriteTracksAdapter
    }

    override fun onResume() {
        super.onResume()
        favoriteTracksViewModel.fillData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun startPlayerActivity(track: Track) {
        val intent = Intent(requireContext(), PlayerActivity::class.java)
        intent.putExtra(TRACK_KEY, track)
        startActivity(intent)
    }

    private fun render(state: FavoriteTracksScreenState) {
        when (state) {
            is FavoriteTracksScreenState.Content -> showFavoriteTracks(state.tracks)
            is FavoriteTracksScreenState.Empty -> showEmptyFavoriteTracks()
        }
    }

    private fun showFavoriteTracks(tracks: List<Track>) {
        llEmptyFavoriteTracks.visibility = View.GONE
        rvFavoriteTracksList.visibility = View.VISIBLE
        favoriteTracksAdapter.trackList.clear()
        favoriteTracksAdapter.trackList.addAll(tracks)
        favoriteTracksAdapter.notifyDataSetChanged()
    }

    private fun showEmptyFavoriteTracks() {
        llEmptyFavoriteTracks.visibility = View.VISIBLE
        rvFavoriteTracksList.visibility = View.GONE
    }
}