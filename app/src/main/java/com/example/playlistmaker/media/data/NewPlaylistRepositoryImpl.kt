package com.example.playlistmaker.media.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import com.example.playlistmaker.media.domain.api.NewPlaylistRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class NewPlaylistRepositoryImpl(private val context: Context) : NewPlaylistRepository {

    override suspend fun saveImageToPrivateStorage(uri: Uri, fileName: String) {
        val filePath = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), COVERS)
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        val file = File(filePath, fileName)
        val inputStream = context.contentResolver.openInputStream(uri)
        val outputStream = withContext(Dispatchers.IO) { FileOutputStream(file) }
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
    }

    companion object {
        const val COVERS = "Covers"
    }
}