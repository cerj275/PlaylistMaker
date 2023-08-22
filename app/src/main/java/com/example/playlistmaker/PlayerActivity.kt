package com.example.playlistmaker

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.SearchActivity.Companion.TRACK_KEY
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    private lateinit var ibBackButton: ImageButton
    private lateinit var ivAlbumCover: ImageView
    private lateinit var tvTrackName: TextView
    private lateinit var tvArtistName: TextView
    private lateinit var tvDuration: TextView
    private lateinit var tvAlbum: TextView
    private lateinit var tvTrackAlbum: TextView
    private lateinit var tvYear: TextView
    private lateinit var tvGenre: TextView
    private lateinit var tvCountry: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        ivAlbumCover = findViewById(R.id.cover)
        tvTrackName = findViewById(R.id.track_name)
        tvArtistName = findViewById(R.id.artist_name)
        tvDuration = findViewById(R.id.track_duration)
        tvAlbum = findViewById(R.id.album_title)
        tvTrackAlbum = findViewById(R.id.track_album)
        tvYear = findViewById(R.id.track_year)
        tvGenre = findViewById(R.id.track_genre)
        tvCountry = findViewById(R.id.track_country)

        ibBackButton = findViewById(R.id.buttonBack)
        ibBackButton.setOnClickListener {
            finish()
        }
        val track = intent.getParcelableExtra<Track>(TRACK_KEY) as Track

        Glide.with(this)
            .load(track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"))
            .placeholder(R.drawable.ic_placeholder)
            .transform(RoundedCorners(ivAlbumCover.resources.getDimensionPixelSize(R.dimen.rounded_corners_big_cover)))
            .into(ivAlbumCover)


        tvTrackName.text = track.trackName
        tvArtistName.text = track.artistName
        tvDuration.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
        tvYear.text = track.releaseDate.substring(0, 4)
        tvGenre.text = track.primaryGenreName
        tvCountry.text = track.country

        if (track.collectionName.isNullOrEmpty()) {
            tvAlbum.visibility = View.GONE
            tvTrackAlbum.visibility = View.GONE
        } else {
            tvTrackAlbum.text = track.collectionName
        }
    }
}