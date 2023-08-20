package com.example.playlistmaker

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    private var searchText: String = ""
    private val itunesBaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val itunesService = retrofit.create(ItunesApi::class.java)
    private val trackList = ArrayList<Track>()
    private val trackAdapter = TrackAdapter {
        if (searchHistory.add(it)) {
            searchHistoryAdapter.notifyDataSetChanged()
            searchHistory.writeSharePrefs()
            startPlayerActivity(it)
        } else {
            searchHistoryAdapter.notifyItemInserted(0)
            searchHistory.writeSharePrefs()
            startPlayerActivity(it)
        }
    }
    private var searchHistoryAdapter = TrackAdapter {
        startPlayerActivity(it)
    }
    private lateinit var inputEditText: EditText
    private lateinit var llNothingFound: LinearLayout
    private lateinit var llNoInternet: LinearLayout
    private lateinit var bRefreshSearch: Button
    private lateinit var llSearchHistoryList: LinearLayout
    private lateinit var searchHistory: SearchHistory
    private lateinit var bClearSearchHistory: Button
    private lateinit var flSearch: FrameLayout


    companion object {
        private const val SEARCH_TEXT = "SEARCH_TEXT"
        const val TRACK_KEY = "track_key"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        searchHistory = SearchHistory(getSharedPreferences(SEARCH_HISTORY, MODE_PRIVATE))
        if (savedInstanceState != null) {
            searchText = savedInstanceState.getString(SEARCH_TEXT).toString()
        }
        val backToolBar = findViewById<ImageButton>(R.id.buttonBack)
        backToolBar.setOnClickListener {
            finish()
        }
        inputEditText = findViewById(R.id.editTextSearch)

        val clearSearchButton = findViewById<ImageView>(R.id.imageViewClearIcon)
        clearSearchButton.setOnClickListener {
            inputEditText.setText("")
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(inputEditText.windowToken, 0)
            trackList.clear()
            trackAdapter.notifyDataSetChanged()
            setSearchHistoryVisible()
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearSearchButton.visibility = clearButtonVisibility(s)
                if (inputEditText.hasFocus() && s?.isEmpty() == true) {
                    setSearchHistoryVisible()
                }
                setSearchVisible()
            }

            override fun afterTextChanged(s: Editable?) {
                val searchEditText = findViewById<EditText>(R.id.editTextSearch)
                searchText = searchEditText.text.toString()
            }
        }
        llNothingFound = findViewById(R.id.linearLayoutNothingFound)
        llNoInternet = findViewById(R.id.linearLayoutNoInternet)
        llSearchHistoryList = findViewById(R.id.searchHistoryLinearLayout)
        flSearch = findViewById(R.id.frameLayoutSearch)

        inputEditText.addTextChangedListener(simpleTextWatcher)

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchTrack()
                true
            }
            false
        }

        bRefreshSearch = findViewById<Button>(R.id.buttonRefreshSearch).apply {
            setOnClickListener { searchTrack() }
        }
        bClearSearchHistory = findViewById(R.id.clearSearchHistoryButton)
        bClearSearchHistory.setOnClickListener {
            searchHistory.clearSearchHistory()
            searchHistoryAdapter.notifyDataSetChanged()
            setSearchVisible()
        }

        trackAdapter.trackList = trackList
        val trackList = findViewById<RecyclerView>(R.id.recyclerViewSearch)
        trackList.adapter = trackAdapter

        searchHistoryAdapter.trackList = searchHistory.searchHistoryTrackList
        val searchHistoryList = findViewById<RecyclerView>(R.id.recyclerViewSearchHistory)
        searchHistoryList.adapter = searchHistoryAdapter

        if (searchHistory.searchHistoryTrackList.isNotEmpty()) {
            setSearchHistoryVisible()
        }
    }

    private fun searchTrack() {
        itunesService.search(inputEditText.text.toString())
            .enqueue(object : Callback<ItunesResponse> {

                override fun onResponse(
                    call: Call<ItunesResponse>,
                    response: Response<ItunesResponse>
                ) {
                    if (response.code() == 200) {
                        trackList.clear()
                        if (response.body()?.results?.isNotEmpty() == true) {
                            trackList.addAll(response.body()?.results!!)
                            trackAdapter.notifyDataSetChanged()
                            llNothingFound.visibility = View.GONE
                            llNoInternet.visibility = View.GONE
                        } else {
                            trackAdapter.trackList.clear()
                            trackAdapter.notifyDataSetChanged()
                            llNothingFound.visibility = View.VISIBLE
                            llNoInternet.visibility = View.GONE
                        }
                    }
                }

                override fun onFailure(call: Call<ItunesResponse>, t: Throwable) {
                    trackAdapter.trackList.clear()
                    trackAdapter.notifyDataSetChanged()
                    llNothingFound.visibility = View.GONE
                    llNoInternet.visibility = View.VISIBLE
                }
            })
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

    private fun setSearchHistoryVisible() {
        llSearchHistoryList.visibility = View.VISIBLE
        flSearch.visibility = View.GONE
    }

    private fun setSearchVisible() {
        flSearch.visibility = View.VISIBLE
        llSearchHistoryList.visibility = View.GONE
    }

    private fun startPlayerActivity(track: Track) {
        val intent = Intent(this, PlayerActivity::class.java)
        intent.putExtra(TRACK_KEY, track)
        startActivity(intent)
    }

}