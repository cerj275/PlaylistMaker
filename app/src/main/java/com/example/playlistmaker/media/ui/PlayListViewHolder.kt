package com.example.playlistmaker.media.ui

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.playlistmaker.R
import com.example.playlistmaker.media.domain.model.Playlist

class PlayListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val cover: ImageView = itemView.findViewById(R.id.imageViewPlaylistCover)
    private val name: TextView = itemView.findViewById(R.id.textViewPlaylistName)
    private val numberOfTracks: TextView = itemView.findViewById(R.id.textViewNumberOfTracks)

    fun bind(playlist: Playlist) {
        Glide.with(cover)
            .load(playlist.coverUri)
            .fitCenter()
            .apply(
                RequestOptions
                    .bitmapTransform(
                        RoundedCorners(
                            itemView
                                .resources
                                .getDimensionPixelSize(R.dimen.rounded_corners_cover)
                        )
                    )
            )
            .placeholder(R.drawable.ic_placeholder)
            .into(cover)
        name.text = playlist.name
    }
}