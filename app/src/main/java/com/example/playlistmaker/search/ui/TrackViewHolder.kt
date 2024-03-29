package com.example.playlistmaker.search.ui

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

open class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    protected val cover: ImageView = itemView.findViewById(R.id.imageViewCover)
    protected val trackName: TextView = itemView.findViewById(R.id.textViewTrackName)
    protected val artistName: TextView = itemView.findViewById(R.id.textViewArtistName)
    protected val trackTime: TextView = itemView.findViewById(R.id.textViewTrackTime)

    open fun bind(item: Track) {
        Glide.with(itemView)
            .load(item.artworkUrl100)
            .placeholder(R.drawable.ic_placeholder)
            .centerCrop()
            .transform(RoundedCorners(itemView.resources.getDimensionPixelSize(R.dimen.rounded_corners_cover)))
            .into(cover)
        trackName.text = item.trackName
        artistName.text = item.artistName
        trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(item.trackTimeMillis)
    }
}