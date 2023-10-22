package com.example.playlistmaker.search.data

import android.content.SharedPreferences
import com.example.playlistmaker.search.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistory(private val sharedPrefs: SharedPreferences) {

    companion object {
        const val SEARCH_HISTORY = "SEARCH_HISTORY"
    }

    fun readSearchHistory(): ArrayList<Track> {
        val json = sharedPrefs.getString(SEARCH_HISTORY, null)
        val type = object : TypeToken<ArrayList<Track>>() {}.type
        return Gson().fromJson(json, type) ?: ArrayList()
    }

    private fun writeSharePrefs(searchHistoryTrackList: List<Track>) {
        sharedPrefs.edit()
            .putString(SEARCH_HISTORY, Gson().toJson(searchHistoryTrackList))
            .apply()

    }

    fun addTrackToSearchHistory(track: Track) {
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

    fun clearSearchHistory() {
        writeSharePrefs(ArrayList())
    }
}