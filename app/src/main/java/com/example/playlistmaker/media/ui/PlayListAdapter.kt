package com.example.playlistmaker.media.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.media.domain.model.Playlist

class PlayListAdapter(private val clickListener: (playList: Playlist) -> Unit) :
    RecyclerView.Adapter<PlayListViewHolder>() {

    val playlists = ArrayList<Playlist>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayListViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.playlist_item, parent, false)
        return PlayListViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlayListViewHolder, position: Int) {
        holder.bind(playlists[position])
        holder.itemView.setOnClickListener { clickListener.invoke(playlists[position]) }
    }

    override fun getItemCount(): Int {
        return playlists.size
    }
}