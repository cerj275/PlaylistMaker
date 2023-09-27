package com.example.playlistmaker.ui.player

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.PlayerState
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.search.SearchActivity.Companion.TRACK_KEY
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {
    companion object {
        private const val ZERO_PLAYBACK_TIME_VALUE = "00:00"
        private const val UPDATE_PLAYBACK_TIME_VALUE = 300L
    }

    private val playerInteractor = Creator.provideGetPlayerInteractor()

    private val handler = Handler(Looper.getMainLooper())

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
        tvYear.text = track.releaseDate.substring(0, 4)
        tvGenre.text = track.primaryGenreName
        tvCountry.text = track.country

        if (track.collectionName.isNullOrEmpty()) {
            tvAlbum.visibility = View.GONE
            tvTrackAlbum.visibility = View.GONE
        } else {
            tvTrackAlbum.text = track.collectionName
        }

        preparePlayer(track.previewUrl)

        ivPlayButton.setOnClickListener {
            playbackControl()
        }

    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        playerInteractor.releasePlayer()
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

    private fun preparePlayer(url: String) {
        playerInteractor.preparePlayer(url)
        setOnCompletionListener()
    }

    private fun startPlayer() {
        playerInteractor.startPlayer()
        ivPlayButton.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_pause))
        handler.post(updatePlaybackTime())
    }

    private fun pausePlayer() {
        playerInteractor.pausePlayer()
        ivPlayButton.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_play))
        handler.removeCallbacks(updatePlaybackTime())
    }

    private fun playbackControl() {
        when (playerInteractor.getPlayerState()) {
            PlayerState.STATE_PLAYING -> {
                pausePlayer()
            }

            PlayerState.STATE_PREPARED, PlayerState.STATE_PAUSED -> {
                startPlayer()
            }

            PlayerState.STATE_DEFAULT -> {
                startPlayer()
                setOnCompletionListener()
            }
        }
    }

    private fun updatePlaybackTime(): Runnable {
        return object : Runnable {
            override fun run() {
                if (playerInteractor.getPlayerState() == PlayerState.STATE_PLAYING) {
                    tvPlayBackTime.text = SimpleDateFormat(
                        "mm:ss",
                        Locale.getDefault()
                    ).format(playerInteractor.getCurrentPosition())
                    handler.postDelayed(this, UPDATE_PLAYBACK_TIME_VALUE)
                }
            }
        }
    }

    private fun setOnCompletionListener() {
        playerInteractor.setOnCompletionListener {
            tvPlayBackTime.text = ZERO_PLAYBACK_TIME_VALUE
            ivPlayButton.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_play))
            handler.removeCallbacks(updatePlaybackTime())
        }
    }
}