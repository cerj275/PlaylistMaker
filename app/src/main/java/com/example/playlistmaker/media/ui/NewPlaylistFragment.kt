package com.example.playlistmaker.media.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.example.playlistmaker.media.view_model.NewPlaylistViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.koin.androidx.viewmodel.ext.android.viewModel

class NewPlaylistFragment : Fragment() {
    private var _binding: FragmentNewPlaylistBinding? = null
    private val binding get() = _binding!!
    private val viewModel: NewPlaylistViewModel by viewModel()

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

        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        initViews()

        if (etPlaylistName.text!!.isEmpty()) {
            bCreate.isEnabled = false
        }

        etPlaylistName.doOnTextChanged { text, start, before, count ->
            text.let { bCreate.isEnabled = !text.isNullOrEmpty() }
            viewModel.getName(text)
        }
        etPlaylistDescription.doOnTextChanged { text, start, before, count ->
            viewModel.getDescription(text)
        }

        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    ivAddCover.setImageURI(uri)
                    viewModel.getCoverUri(uri)
                }
            }

        ivAddCover.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        ibBack.setOnClickListener {
            showDialog()
        }
        bCreate.setOnClickListener {
            viewModel.createPlaylist()
            Toast.makeText(
                requireContext(),
                resources.getString(R.string.playlist_created, etPlaylistName.text),
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

    override fun onDestroyView() {
        super.onDestroyView()
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        _binding = null
    }
}