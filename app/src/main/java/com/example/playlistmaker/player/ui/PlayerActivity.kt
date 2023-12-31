package com.example.playlistmaker.player.ui

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.player.view_model.PlayerScreenState.Companion.PLAY
import com.example.playlistmaker.player.view_model.PlayerViewModel
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.SearchFragment.Companion.TRACK_KEY
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    private val viewModel: PlayerViewModel by viewModel {
        parametersOf(track)
    }

    private lateinit var track: Track
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
    private lateinit var tvPlayBackTime: TextView
    private lateinit var ivPlayButton: ImageView
    private lateinit var ivFavoriteButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        initViews()

        ibBackButton.setOnClickListener {
            finish()
        }
        track = intent.getParcelableExtra<Track>(TRACK_KEY) as Track

        Glide.with(this)
            .load(track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"))
            .placeholder(R.drawable.ic_placeholder)
            .transform(RoundedCorners(ivAlbumCover.resources.getDimensionPixelSize(R.dimen.rounded_corners_big_cover)))
            .into(ivAlbumCover)

        tvTrackName.text = track.trackName
        tvArtistName.text = track.artistName
        tvDuration.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)

        if (track.releaseDate.isNullOrEmpty()) {
            tvYear.text = ""
        } else {
            tvYear.text = track.releaseDate?.substring(0, 4)
        }
        tvGenre.text = track.primaryGenreName
        tvCountry.text = track.country

        if (track.collectionName.isNullOrEmpty()) {
            tvAlbum.visibility = View.GONE
            tvTrackAlbum.visibility = View.GONE
        } else {
            tvTrackAlbum.text = track.collectionName
        }

        viewModel.observeState().observe(this) {
            ivPlayButton.isEnabled = it.isPlayButtonEnabled
            tvPlayBackTime.text = it.progress
            setPlayButtonImage(it.buttonText)
        }
        viewModel.observeFavorite().observe(this) { isFavorite ->
            setFavoriteButtonImage(isFavorite)
        }

        ivFavoriteButton.setOnClickListener {
            viewModel.onFavoriteClicked()
        }

        viewModel.preparePlayer()
        ivPlayButton.setOnClickListener {
            viewModel.playbackControl()
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.releasePlayer()
    }

    private fun initViews() {
        ivAlbumCover = findViewById(R.id.cover)
        tvTrackName = findViewById(R.id.track_name)
        tvArtistName = findViewById(R.id.artist_name)
        tvDuration = findViewById(R.id.track_duration)
        tvAlbum = findViewById(R.id.album_title)
        tvTrackAlbum = findViewById(R.id.track_album)
        tvYear = findViewById(R.id.track_year)
        tvGenre = findViewById(R.id.track_genre)
        tvCountry = findViewById(R.id.track_country)
        tvPlayBackTime = findViewById(R.id.playback_time)
        ibBackButton = findViewById(R.id.buttonBack)
        ivPlayButton = findViewById(R.id.play_button)
        ivFavoriteButton = findViewById(R.id.favorite_button)
    }

    private fun setPlayButtonImage(buttonText: String) {
        if (buttonText == PLAY) {
            ivPlayButton.setImageDrawable(
                AppCompatResources.getDrawable(
                    this,
                    R.drawable.ic_play
                )
            )
        } else {
            ivPlayButton.setImageDrawable(
                AppCompatResources.getDrawable(
                    this,
                    R.drawable.ic_pause
                )
            )
        }
    }

    private fun setFavoriteButtonImage(isFavorite: Boolean) {
        if (isFavorite) {
            ivFavoriteButton.setImageDrawable(
                AppCompatResources.getDrawable(
                    this, R.drawable.ic_not_favorite
                )
            )
        } else {
            ivFavoriteButton.setImageDrawable(
                AppCompatResources.getDrawable(
                    this, R.drawable.ic_favorite
                )
            )
        }
    }
}