package com.example.playlistmaker.media.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import androidx.core.net.toUri
import com.example.playlistmaker.media.domain.api.NewPlaylistRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID.randomUUID

class NewPlaylistRepositoryImpl(private val context: Context) : NewPlaylistRepository {
    companion object {
        private const val COVERS = "Covers"
    }

    override suspend fun saveImageToPrivateStorage(uri: Uri): Uri {
        val mediaName = randomUUID().toString()
        val filePath =
            File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), COVERS)

        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        val file = File(filePath, mediaName)
        val inputStream = context.contentResolver.openInputStream(uri)
        val outputStream = withContext(Dispatchers.IO) { FileOutputStream(file) }

        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)

        return file.toUri()
    }
}