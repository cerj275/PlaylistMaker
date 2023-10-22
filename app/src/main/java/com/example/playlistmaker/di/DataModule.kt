package com.example.playlistmaker.di

import android.content.Context
import android.net.ConnectivityManager
import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.SearchHistoryImpl
import com.example.playlistmaker.search.data.network.ItunesApi
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.domain.api.SearchHistory
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {
    val itunesBaseUrl = "https://itunes.apple.com"

    single<ItunesApi> {
        Retrofit.Builder()
            .baseUrl(itunesBaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ItunesApi::class.java)
    }

    factory { Gson() }

    single<NetworkClient> {
        RetrofitNetworkClient(get())
    }

    single { androidContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager}

    single<SearchHistory> {
        SearchHistoryImpl(get(),get())
    }
}