package com.example.playlistmaker.player.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.media.domain.model.Playlist

class PlayerBottomSheetAdapter(private val clickListener: (playList: Playlist) -> Unit) :
    RecyclerView.Adapter<PlayerBottomSheetViewHolder>() {

    val playlists = ArrayList<Playlist>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerBottomSheetViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.player_playlist_item, parent, false)
        return PlayerBottomSheetViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlayerBottomSheetViewHolder, position: Int) {
        holder.bind(playlists[position])
        holder.itemView.setOnClickListener { clickListener.invoke(playlists[position]) }
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

}

