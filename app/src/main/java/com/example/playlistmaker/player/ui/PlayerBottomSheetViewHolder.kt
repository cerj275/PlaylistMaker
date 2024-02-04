package com.example.playlistmaker.player.ui

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.media.domain.model.Playlist

class PlayerBottomSheetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val ivCover: ImageView = itemView.findViewById(R.id.imageViewPlayerPlaylistCover)
    private val tvName: TextView = itemView.findViewById(R.id.textViewPlayerPlaylistName)
    private val tvNumberOfTracks: TextView =
        itemView.findViewById(R.id.textViewPlayerNumberOfTracks)

    fun bind(playlist: Playlist) {
        Glide.with(ivCover)
            .load(playlist.coverUri)
            .centerCrop()
            .transform(RoundedCorners(itemView.resources.getDimensionPixelSize(R.dimen.rounded_corners_cover)))
            .placeholder(R.drawable.ic_placeholder)
            .into(ivCover)
        tvName.text = playlist.name
        tvNumberOfTracks.text = numberOfTracksToString(playlist.numberOfTracks)
    }

    private fun numberOfTracksToString(numberOfTracks: Int): String {
        return if (numberOfTracks % 100 in 5..20) {
            "$numberOfTracks треков"
        } else {
            if (numberOfTracks % 10 == 1) {
                "$numberOfTracks трек"
            } else if (numberOfTracks % 10 in 2..4) {
                "$numberOfTracks трека"
            } else {
                "$numberOfTracks треков"
            }
        }
    }
}