package com.example.playlistmaker.media.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.models.Track

class TrackInPlaylistAdapter(
    val clickListener: (track: Track) -> Unit,
    val longClickListener: ((track: Track) -> Unit)?
) : RecyclerView.Adapter<TrackInPlaylistViewHolder>() {

    var trackList = ArrayList<Track>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackInPlaylistViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.track_list_item, parent, false)
        return TrackInPlaylistViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackInPlaylistViewHolder, position: Int) {
        holder.bind(trackList[position])
        holder.itemView.setOnClickListener { clickListener.invoke(trackList[position]) }
        holder.itemView.setOnLongClickListener {
            longClickListener?.invoke(trackList[position])
            return@setOnLongClickListener true
        }
    }

    override fun getItemCount(): Int {
        return trackList.size
    }
}