package com.example.playlistmaker.presentation

import android.content.SharedPreferences
import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

const val SEARCH_HISTORY = "search_history"

class SearchHistory(private val sharedPrefs: SharedPreferences) {

     var searchHistoryTrackList: ArrayList<Track> = readSearchHistory()

    private fun readSearchHistory(): ArrayList<Track> {
        val json = sharedPrefs.getString(SEARCH_HISTORY, null) ?: return ArrayList()
        val type = object : TypeToken<ArrayList<Track>>() {}.type
        return Gson().fromJson(json, type)
    }

    fun writeSharePrefs() {
        sharedPrefs.edit()
            .putString(SEARCH_HISTORY, Gson().toJson(searchHistoryTrackList))
            .apply()

    }

    fun add(track: Track): Boolean {
        if (searchHistoryTrackList.contains(track)) {
            searchHistoryTrackList.remove(track)
            searchHistoryTrackList.add(0, track)
            return true
        } else {
            if (searchHistoryTrackList.size == 10) {
                searchHistoryTrackList.removeAt(9)
                searchHistoryTrackList.add(0, track)
                return true
            } else {
                searchHistoryTrackList.add(0, track)
                return false
            }
        }
    }

    fun clearSearchHistory() {
        searchHistoryTrackList.clear()
        sharedPrefs.edit()
            .clear()
            .apply()
    }

}