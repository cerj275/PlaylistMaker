package com.example.playlistmaker.search.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.player.ui.PlayerActivity
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.view_model.SearchScreenState
import com.example.playlistmaker.search.view_model.SearchViewModel

class SearchActivity : AppCompatActivity() {
    companion object {
        private const val SEARCH_TEXT = "SEARCH_TEXT"
        const val TRACK_KEY = "track_key"
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private val searchRunnable = Runnable { searchTrack() }
    private val handler = Handler(Looper.getMainLooper())
    private var isClickAllowed = true
    private var searchText: String = ""
    private val trackList = ArrayList<Track>()
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

    private lateinit var viewModel: SearchViewModel
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
    private lateinit var bBackToolBar: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        initViews()

        viewModel =
            ViewModelProvider(this, SearchViewModel.getModelFactory())[SearchViewModel::class.java]
        viewModel.observeState().observe(this) {
            render(it)
        }

        if (savedInstanceState != null) {
            searchText = savedInstanceState.getString(SEARCH_TEXT).toString()
        }
        bBackToolBar.setOnClickListener {
            finish()
        }

        bClearSearch.setOnClickListener {
            inputEditText.setText("")
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(inputEditText.windowToken, 0)
            trackList.clear()
            trackAdapter.notifyDataSetChanged()
        }


        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                bClearSearch.visibility = clearButtonVisibility(s)
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
            viewModel.refreshSearchButton(searchText)
        }
        bClearSearchHistory.setOnClickListener {
            viewModel.clearSearchHistory()
        }

        rvSearch.adapter = trackAdapter
        rvSearchHistoryList.adapter = searchHistoryAdapter

        viewModel.setShowingHistoryContent()


    }


    private fun initViews() {
        llNothingFound = findViewById(R.id.linearLayoutNothingFound)
        llNoInternet = findViewById(R.id.linearLayoutNoInternet)
        llSearchHistoryList = findViewById(R.id.searchHistoryLinearLayout)
        flSearch = findViewById(R.id.frameLayoutSearch)
        bRefreshSearch = findViewById(R.id.buttonRefreshSearch)
        bClearSearchHistory = findViewById(R.id.clearSearchHistoryButton)
        rvSearch = findViewById(R.id.recyclerViewSearch)
        rvSearchHistoryList = findViewById(R.id.recyclerViewSearchHistory)
        bClearSearch = findViewById(R.id.imageViewClearIcon)
        inputEditText = findViewById(R.id.editTextSearch)
        pbSearchLoading = findViewById(R.id.progressBarSearchLoading)
        bBackToolBar = findViewById<ImageButton>(R.id.buttonBack)


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

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun startPlayerActivity(track: Track) {
        val intent = Intent(this, PlayerActivity::class.java)
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
        trackList.clear()
        trackList.addAll(tracks)
        trackAdapter.trackList = trackList
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

}