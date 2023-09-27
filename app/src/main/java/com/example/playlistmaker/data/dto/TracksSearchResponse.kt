package com.example.playlistmaker.data.dto

import com.example.playlistmaker.domain.models.Track

class TracksSearchResponse(
    val resultCount: Int,
    val results: Array<Track>
) : Response()
