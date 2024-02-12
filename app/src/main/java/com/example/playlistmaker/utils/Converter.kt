package com.example.playlistmaker.utils

import java.text.SimpleDateFormat
import java.util.Locale

class Converter {

    fun convertTimeMillisToMinutes(timeInMillis: Long): Int =
        SimpleDateFormat("mm", Locale.getDefault()).format(timeInMillis).toInt()

}