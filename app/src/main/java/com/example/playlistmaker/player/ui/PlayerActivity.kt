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
import com.example.playlistmaker.player.view_model.PlayerScreenState
import com.example.playlistmaker.player.view_model.PlayerViewModel
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.SearchActivity.Companion.TRACK_KEY
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {
    companion object {
        private const val ZERO_PLAYBACK_TIME_VALUE = "00:00"
    }

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

        if (track.releaseDate.isNullOrEmpty()){
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
            render(it)
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
    }

    private fun render(state: PlayerScreenState) {
        when (state) {
            is PlayerScreenState.DefaultScreenState -> showDefaultScreen()
            is PlayerScreenState.PreparedScreenState -> showPreparedScreen()
            is PlayerScreenState.PlayingScreenState -> showPlayingScreen()
            is PlayerScreenState.PauseScreenState -> showPauseScreen()
            is PlayerScreenState.TimerState -> setPlaybackTime(state.playbackTimer)
        }
    }

    private fun showDefaultScreen() {
        ivPlayButton.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_play))
        ivPlayButton.isEnabled = false
    }

    private fun showPreparedScreen() {
        ivPlayButton.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_play))
        ivPlayButton.isEnabled = true
        tvPlayBackTime.text = ZERO_PLAYBACK_TIME_VALUE
    }

    private fun showPlayingScreen() {
        ivPlayButton.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_pause))
        ivPlayButton.isEnabled = true
    }

    private fun showPauseScreen() {
        ivPlayButton.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_play))
        ivPlayButton.isEnabled = true
    }

    private fun setPlaybackTime(time: String) {
        tvPlayBackTime.text = time

    }
}