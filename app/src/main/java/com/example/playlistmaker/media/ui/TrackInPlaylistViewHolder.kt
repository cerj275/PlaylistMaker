package com.example.playlistmaker.media.ui

import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.TrackViewHolder
import java.text.SimpleDateFormat
import java.util.Locale

class TrackInPlaylistViewHolder(itemView: View) : TrackViewHolder(itemView) {

//    private val cover: ImageView = itemView.findViewById(R.id.imageViewCover)
//    private val trackName: TextView = itemView.findViewById(R.id.textViewTrackName)
//    private val artistName: TextView = itemView.findViewById(R.id.textViewArtistName)
//    private val trackTime: TextView = itemView.findViewById(R.id.textViewTrackTime)

    override fun bind(item: Track) {
        Glide.with(itemView)
            .load(item.artworkUrl60)
            .placeholder(R.drawable.ic_placeholder)
            .centerCrop()
            .transform(RoundedCorners(itemView.resources.getDimensionPixelSize(R.dimen.rounded_corners_cover)))
            .into(cover)
        trackName.text = item.trackName
        artistName.text = item.artistName
        trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(item.trackTimeMillis)
    }
}