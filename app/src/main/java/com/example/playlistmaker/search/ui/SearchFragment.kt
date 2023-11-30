package com.example.playlistmaker.search.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.player.ui.PlayerActivity
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.view_model.SearchScreenState
import com.example.playlistmaker.search.view_model.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    companion object {
        private const val SEARCH_TEXT = "SEARCH_TEXT"
        const val TRACK_KEY = "track_key"
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by viewModel()

    private val searchRunnable = Runnable { searchTrack() }
    private val handler = Handler(Looper.getMainLooper())
    private var isClickAllowed = true
    private var searchText: String = ""
    private val trackAdapter = TrackAdapter {
        if (clickDebounce()) {
            viewModel.onTrackPressed(it)
            startPlayerActivity(it)
        }
    }
    private val searchHistoryAdapter = TrackAdapter {
        if (clickDebounce()) {
            viewModel.onTrackPressed(it)
            startPlayerActivity(it)
        }
    }

    private lateinit var inputEditText: EditText
    private lateinit var llNothingFound: LinearLayout
    private lateinit var llNoInternet: LinearLayout
    private lateinit var bRefreshSearch: Button
    private lateinit var llSearchHistoryList: LinearLayout
    private lateinit var bClearSearchHistory: Button
    private lateinit var flSearch: FrameLayout
    private lateinit var pbSearchLoading: ProgressBar
    private lateinit var rvSearch: RecyclerView
    private lateinit var rvSearchHistoryList: RecyclerView
    private lateinit var bClearSearch: ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()

        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        if (savedInstanceState != null) {
            searchText = savedInstanceState.getString(SEARCH_TEXT).toString()
        }

        bClearSearch.setOnClickListener {
            inputEditText.setText("")
            val inputMethodManager =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(inputEditText.windowToken, 0)
            trackAdapter.trackList.clear()
            trackAdapter.notifyDataSetChanged()
        }


        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                bClearSearch.isVisible = !s.isNullOrEmpty()
                viewModel.onTextChanged(s.toString())
                searchDebounce()
            }

            override fun afterTextChanged(s: Editable?) {
                searchText = inputEditText.text.toString()
            }
        }

        inputEditText.addTextChangedListener(simpleTextWatcher)

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchTrack()
                true
            }
            false
        }

        inputEditText.setOnFocusChangeListener { _, hasFocus ->
            viewModel.onFocusChanged(hasFocus, inputEditText.text.toString())
        }

        bRefreshSearch.setOnClickListener {
            viewModel.refreshSearchButton()
        }
        bClearSearchHistory.setOnClickListener {
            viewModel.clearSearchHistory()
        }

        rvSearch.adapter = trackAdapter
        rvSearchHistoryList.adapter = searchHistoryAdapter

        viewModel.setShowingHistoryContent()
    }

    override fun onPause() {
        super.onPause()
        binding.editTextSearch.text.clear()
        viewModel.onPause()
    }
    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }

    private fun initViews() {
        llNothingFound = binding.linearLayoutNothingFound
        llNoInternet = binding.linearLayoutNoInternet
        llSearchHistoryList = binding.searchHistoryLinearLayout
        flSearch = binding.frameLayoutSearch
        bRefreshSearch = binding.buttonRefreshSearch
        bClearSearchHistory = binding.clearSearchHistoryButton
        rvSearch = binding.recyclerViewSearch
        rvSearchHistoryList = binding.recyclerViewSearchHistory
        bClearSearch = binding.imageViewClearIcon
        inputEditText = binding.editTextSearch
        pbSearchLoading = binding.progressBarSearchLoading


    }

    private fun render(state: SearchScreenState) {
        when (state) {
            is SearchScreenState.Loading -> showProgressBar()
            is SearchScreenState.SearchContent -> showContent(state.tracks)
            is SearchScreenState.HistoryContent -> showHistoryContent(state.tracks)
            is SearchScreenState.EmptySearch -> showEmptySearch()
            is SearchScreenState.Error -> showSearchError()
            is SearchScreenState.EmptyScreen -> showEmptyScreen()
        }
    }


    private fun searchTrack() {
        if (searchText.isNotEmpty()) {
            viewModel.searchRequest(searchText)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, searchText)
    }

    private fun startPlayerActivity(track: Track) {
        val intent = Intent(requireContext(), PlayerActivity::class.java)
        intent.putExtra(TRACK_KEY, track)
        startActivity(intent)
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun showProgressBar() {
        flSearch.visibility = View.VISIBLE
        rvSearch.visibility = View.GONE
        pbSearchLoading.visibility = View.VISIBLE
        llNothingFound.visibility = View.GONE
        llNoInternet.visibility = View.GONE
        llSearchHistoryList.visibility = View.GONE
    }

    private fun showContent(tracks: List<Track>) {
        flSearch.visibility = View.VISIBLE
        rvSearch.visibility = View.VISIBLE
        pbSearchLoading.visibility = View.GONE
        llNoInternet.visibility = View.GONE
        llNothingFound.visibility = View.GONE
        llSearchHistoryList.visibility = View.GONE
        trackAdapter.trackList.clear()
        trackAdapter.trackList.addAll(tracks)
        trackAdapter.notifyDataSetChanged()
    }

    private fun showHistoryContent(searchHistory: List<Track>) {
        flSearch.visibility = View.GONE
        pbSearchLoading.visibility = View.GONE
        llNoInternet.visibility = View.GONE
        llNothingFound.visibility = View.GONE
        llSearchHistoryList.visibility = View.VISIBLE
        searchHistoryAdapter.trackList = searchHistory as ArrayList<Track>
        searchHistoryAdapter.notifyDataSetChanged()
    }

    private fun showEmptySearch() {
        pbSearchLoading.visibility = View.GONE
        llNoInternet.visibility = View.GONE
        llNothingFound.visibility = View.VISIBLE
        llSearchHistoryList.visibility = View.GONE
        trackAdapter.trackList.clear()
        trackAdapter.notifyDataSetChanged()

    }

    private fun showSearchError() {
        pbSearchLoading.visibility = View.GONE
        llNoInternet.visibility = View.VISIBLE
        llSearchHistoryList.visibility = View.GONE
        trackAdapter.trackList.clear()
        trackAdapter.notifyDataSetChanged()
        searchText = inputEditText.toString()
    }

    private fun showEmptyScreen() {
        flSearch.visibility = View.GONE
        pbSearchLoading.visibility = View.GONE
        llNoInternet.visibility = View.GONE
        llNothingFound.visibility = View.GONE
        llSearchHistoryList.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}