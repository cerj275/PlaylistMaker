package com.example.playlistmaker

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
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
    private val trackAdapter = TrackAdapter()
    private lateinit var inputEditText: EditText
    private lateinit var nothingFound: LinearLayout
    private lateinit var noInternet: LinearLayout
    private lateinit var refreshSearch: Button

    companion object {
        private const val SEARCH_TEXT = "SEARCH_TEXT"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
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
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearSearchButton.visibility = clearButtonVisibility(s)
            }

            override fun afterTextChanged(s: Editable?) {
                val searchEditText = findViewById<EditText>(R.id.editTextSearch)
                searchText = searchEditText.text.toString()
            }
        }

        nothingFound = findViewById(R.id.linearLayoutNothingFound)
        noInternet = findViewById(R.id.linearLayoutNoInternet)

        inputEditText.addTextChangedListener(simpleTextWatcher)

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchTrack()
                true
            }
            false
        }

        refreshSearch = findViewById<Button>(R.id.buttonRefreshSearch_).apply {
            setOnClickListener { searchTrack() }
        }
        trackAdapter.trackList = trackList
        val track = findViewById<RecyclerView>(R.id.recyclerViewSearch)
        track.adapter = trackAdapter


    }

    private fun searchTrack() {
        itunesService.search(inputEditText.text.toString()).enqueue(object : Callback<ItunesResponse> {

            override fun onResponse(
                call: Call<ItunesResponse>,
                response: Response<ItunesResponse>
            ) {
                if (response.code() == 200) {
                    trackList.clear()
                    if (response.body()?.results?.isNotEmpty() == true) {
                        trackList.addAll(response.body()?.results!!)
                        trackAdapter.notifyDataSetChanged()

                        nothingFound.visibility = View.GONE
                        noInternet.visibility = View.GONE
                    } else {
                        trackAdapter.trackList.clear()
                        trackAdapter.notifyDataSetChanged()
                        nothingFound.visibility = View.VISIBLE
                        noInternet.visibility = View.GONE
                    }
                }
            }

            override fun onFailure(call: Call<ItunesResponse>, t: Throwable) {
                trackAdapter.trackList.clear()
                trackAdapter.notifyDataSetChanged()
                nothingFound.visibility = View.GONE
                noInternet.visibility = View.VISIBLE
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
}