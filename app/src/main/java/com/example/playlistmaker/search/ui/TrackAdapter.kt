package com.example.playlistmaker.search.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.models.Track

class TrackAdapter(
    val clickListener: (track: Track) -> Unit,
    val longClickListener: ((track: Track) -> Unit)?
) : RecyclerView.Adapter<TrackViewHolder>() {

    var trackList = ArrayList<Track>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.track_list_item, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
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