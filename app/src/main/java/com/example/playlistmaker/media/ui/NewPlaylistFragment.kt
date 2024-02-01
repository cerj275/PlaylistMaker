package com.example.playlistmaker.media.ui

import android.Manifest
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.media.view_model.NewPlaylistViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream
import java.util.UUID.randomUUID

class NewPlaylistFragment : Fragment() {
    private var _binding: FragmentNewPlaylistBinding? = null
    private val binding get() = _binding!!
    private val viewModel: NewPlaylistViewModel by viewModel()
    private var coverUri: Uri? = null

    private lateinit var ibBack: ImageButton
    private lateinit var etPlaylistName: TextInputEditText
    private lateinit var etPlaylistDescription: TextInputEditText
    private lateinit var ivAddCover: ImageView
    private lateinit var tilName: TextInputLayout
    private lateinit var tilDescription: TextInputLayout
    private lateinit var bCreate: Button
    private lateinit var confirmDialog: MaterialAlertDialogBuilder

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()

        if (etPlaylistName.text!!.isEmpty()) {
            bCreate.isEnabled = false
        }

        etPlaylistName.doOnTextChanged { text, start, before, count ->
            text.let { bCreate.isEnabled = !text.isNullOrEmpty() }
        }

        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    val mediaName = randomUUID().toString()
                    ivAddCover.setImageURI(uri)
                    coverUri = saveImageToPrivateStorage(uri, mediaName)
                }
            }

        val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    pickMedia.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
            }

        ivAddCover.setOnClickListener {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        ibBack.setOnClickListener {
            showDialog()
        }
        bCreate.setOnClickListener {
            viewModel.createPlaylist(
                Playlist(
                    0,
                    etPlaylistName.text.toString(),
                    etPlaylistDescription.text.toString(),
                    coverUri.toString(),
                    mutableListOf(),
                    0
                )
            )
            Toast.makeText(
                requireContext(), "Плейлсит ${binding.editTextPlaylistName.text} создан",
                Toast.LENGTH_SHORT
            ).show()
            findNavController().navigateUp()
        }

        confirmDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.title_dialog_new_playlist))
            .setMessage(getString(R.string.message_dialog_new_playlist))
            .setNeutralButton(getString(R.string.cancel)) { dialog, which -> }
            .setPositiveButton(getString(R.string.finish)) { dialog, which ->
                findNavController().navigateUp()
            }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    showDialog()
                }
            })
    }

    private fun initViews() {
        ibBack = binding.buttonBack
        etPlaylistName = binding.editTextPlaylistName
        etPlaylistDescription = binding.editTextPlaylistDescription
        ivAddCover = binding.imageViewAddCover
        tilName = binding.textInputLayoutName
        tilDescription = binding.textInputLayoutDescription
        bCreate = binding.buttonCreateNewPlaylist

    }

    private fun showDialog() {
        if (ivAddCover.background != null || etPlaylistName.text!!.isNotEmpty() || etPlaylistDescription.text!!.isNotEmpty()) {
            confirmDialog.show()
        } else {
            findNavController().navigateUp()
        }
    }

    private fun saveImageToPrivateStorage(uri: Uri, mediaName: String): Uri {
        val filePath =
            File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), COVERS)

        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        val file = File(filePath, mediaName)
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)

        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)

        return file.toUri()
    }

    companion object {
        const val COVERS = "Covers"
    }
}