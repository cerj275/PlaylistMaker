package com.example.playlistmaker.search.data

import android.content.SharedPreferences
import com.example.playlistmaker.search.domain.api.SearchHistory
import com.example.playlistmaker.search.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistoryImpl(private val sharedPrefs: SharedPreferences, private val gson: Gson) : SearchHistory {

    companion object {
        const val SEARCH_HISTORY = "SEARCH_HISTORY"
    }

    override fun readSearchHistory(): ArrayList<Track> {
        val json = sharedPrefs.getString(SEARCH_HISTORY, null)
        val type = object : TypeToken<ArrayList<Track>>() {}.type
        return gson.fromJson(json, type) ?: ArrayList()
    }

    private fun writeSharePrefs(searchHistoryTrackList: List<Track>) {
        sharedPrefs.edit()
            .putString(SEARCH_HISTORY, Gson().toJson(searchHistoryTrackList))
            .apply()

    }

    override fun addTrackToSearchHistory(track: Track) {
        val searchHistoryTrackList = readSearchHistory()
        if (searchHistoryTrackList.contains(track)) {
            searchHistoryTrackList.remove(track)
            searchHistoryTrackList.add(0, track)
        } else {
            if (searchHistoryTrackList.size == 10) {
                searchHistoryTrackList.removeAt(9)
                searchHistoryTrackList.add(0, track)
            } else {
                searchHistoryTrackList.add(0, track)
            }
        }
        writeSharePrefs(searchHistoryTrackList)
    }

    override fun clearSearchHistory() {
        writeSharePrefs(ArrayList())
    }
}