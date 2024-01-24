package com.example.playlistmaker.media.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.media.view_model.PlayListsState
import com.example.playlistmaker.media.view_model.PlayListsViewModel
import com.example.playlistmaker.utils.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayListsFragment : Fragment() {
    companion object {
        fun newInstance() = PlayListsFragment()
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }


    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlayListsViewModel by viewModel()
    private val playlistAdapter = PlayListAdapter {
        onPlaylistClickDebounce(it)
    }
    private lateinit var onPlaylistClickDebounce: (Playlist) -> Unit

    private lateinit var rvPlaylists: RecyclerView
    private lateinit var llEmptyPlaylists: LinearLayout
    private lateinit var bNewPlaylist: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()

        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        binding.recyclerViewPlaylists.layoutManager =
            GridLayoutManager(requireContext(), 2)
        binding.recyclerViewPlaylists.adapter = playlistAdapter

        bNewPlaylist.setOnClickListener {
            findNavController().navigate(R.id.action_mediaLibraryFragment_to_newPlaylistFragment)
        }

        onPlaylistClickDebounce = debounce<Playlist>(
            CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, false
        ) {}

    }

    private fun initViews() {
        llEmptyPlaylists = binding.linearLayoutEmptyPlaylists
        bNewPlaylist = binding.buttonNewPlaylist
        rvPlaylists = binding.recyclerViewPlaylists
    }

    private fun render(state: PlayListsState) {
        when (state) {
            is PlayListsState.Empty -> showEmpty()
            is PlayListsState.Content -> showContent(state.playlists)
        }
    }

    private fun showEmpty() {
        rvPlaylists.isVisible = false
        llEmptyPlaylists.isVisible = true
    }

    private fun showContent(playLists: List<Playlist>) {
        rvPlaylists.isVisible = true
        llEmptyPlaylists.isVisible = false
        playlistAdapter.playlists.clear()
        playlistAdapter.playlists.addAll(playLists)
        playlistAdapter.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        viewModel.fillData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}