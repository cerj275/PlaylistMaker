package com.example.playlistmaker.settings.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import com.example.playlistmaker.settings.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvShare = binding.textViewShare
        val tvSupport = binding.textViewSupport
        val tvTermsOfUse = binding.textViewTermsOfUse
        val smThemeSwitcher = binding.themeSwitcher

        tvShare.setOnClickListener {
            viewModel.shareLink()
        }

        tvSupport.setOnClickListener {
            viewModel.sendMailToSupport()
        }

        tvTermsOfUse.setOnClickListener {
            viewModel.openUserAgreement()
        }

        smThemeSwitcher.isChecked = viewModel.getDarkThemeIsEnabled()

        smThemeSwitcher.setOnCheckedChangeListener { switcher, isChecked ->
            viewModel.themeSwitched(isChecked)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}