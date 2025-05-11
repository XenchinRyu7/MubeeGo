package com.saefulrdevs.mubeego.ui.main.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.saefulrdevs.mubeego.R
import com.saefulrdevs.mubeego.databinding.FragmentProfileBinding
import org.koin.android.ext.android.inject

class ProfileFragment : Fragment() {

    private var _binding : FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val userPreferencesUseCase: com.saefulrdevs.mubeego.core.domain.usecase.UserPreferencesUseCase by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Setup theme spinner
        val spinner = binding.spinnerTheme
        val themeOptions = resources.getStringArray(R.array.theme_options)
        val adapter = android.widget.ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, themeOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        // Set spinner to current mode dari preferences
        val savedThemeMode = userPreferencesUseCase.getThemeMode()
        spinner.setSelection(savedThemeMode)

        spinner.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                userPreferencesUseCase.setThemeMode(position)
                when (position) {
                    0 -> androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    1 -> androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO)
                    2 -> androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES)
                }
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }
    }
}