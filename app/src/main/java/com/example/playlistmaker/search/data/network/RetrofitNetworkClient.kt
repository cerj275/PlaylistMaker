package com.example.playlistmaker.search.data.network

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.dto.Response
import com.example.playlistmaker.search.data.dto.TracksSearchRequest
import org.koin.core.component.KoinComponent

class RetrofitNetworkClient(private val itunesService: ItunesApi) : NetworkClient, KoinComponent {

//    private val itunesBaseUrl = "https://itunes.apple.com"
//    private val retrofit = Retrofit.Builder()
//        .baseUrl(itunesBaseUrl)
//        .addConverterFactory(GsonConverterFactory.create())
//        .build()
//    private val itunesService = retrofit.create(ItunesApi::class.java)
    override fun doRequest(dto: Any): Response {
        if (!isConnected()) {
            return Response().apply { resultCode = -1 }
        }

        if (dto is TracksSearchRequest) {
            val resp = itunesService.searchTracks(dto.expression).execute()
            val body = resp.body() ?: Response()
            return body.apply { resultCode = resp.code() }
        } else {
            return Response().apply { resultCode = 400 }
        }
    }

    private fun isConnected(): Boolean {
        val connectivityManager: ConnectivityManager = getKoin().get()
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> return true
            }
        }
        return false
    }
}
