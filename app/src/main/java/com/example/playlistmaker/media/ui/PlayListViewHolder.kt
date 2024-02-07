package com.example.playlistmaker.media.ui

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.media.domain.model.Playlist

class PlayListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val ivCover: ImageView = itemView.findViewById(R.id.imageViewPlaylistCover)
    private val tvName: TextView = itemView.findViewById(R.id.textViewPlaylistName)
    private val tvNumberOfTracks: TextView = itemView.findViewById(R.id.textViewNumberOfTracks)

    fun bind(playlist: Playlist) {
        Glide.with(ivCover)
            .load(playlist.coverUri)
            .centerCrop()
            .transform(RoundedCorners(itemView.resources.getDimensionPixelSize(R.dimen.rounded_corners_big_cover)))
            .placeholder(R.drawable.ic_placeholder)
            .into(ivCover)
        tvName.text = playlist.name
        tvNumberOfTracks.text = itemView.resources.getQuantityString(
            R.plurals.number_of_tracks,
            playlist.numberOfTracks,
            playlist.numberOfTracks
        )
    }
}
