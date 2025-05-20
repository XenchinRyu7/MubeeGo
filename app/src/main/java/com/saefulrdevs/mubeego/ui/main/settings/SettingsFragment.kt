package com.saefulrdevs.mubeego.ui.main.settings

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import android.widget.Spinner
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.saefulrdevs.mubeego.R
import com.saefulrdevs.mubeego.core.domain.usecase.UserPreferencesUseCase
import com.saefulrdevs.mubeego.databinding.FragmentSettingsBinding
import org.koin.android.ext.android.inject

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val userPreferencesUseCase: UserPreferencesUseCase by inject()

    private val requestNotificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                // Jika tidak diizinkan, arahkan ke pengaturan aplikasi
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", requireContext().packageName, null)
                }
                startActivity(intent)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Setup theme spinner
        val spinner = binding.spinnerTheme
        val themeOptions = resources.getStringArray(R.array.theme_options)
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, themeOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        val savedThemeMode = userPreferencesUseCase.getThemeMode()
        spinner.setSelection(savedThemeMode)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                userPreferencesUseCase.setThemeMode(position)
                when (position) {
                    0 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    2 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        // Enable notification switch
        binding.switchNotification.isChecked = userPreferencesUseCase.isNotificationEnabled()
        binding.switchNotification.setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
            if (isChecked) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                        requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        userPreferencesUseCase.setNotificationEnabled(true)
                    }
                } else {
                    userPreferencesUseCase.setNotificationEnabled(true)
                }
            } else {
                userPreferencesUseCase.setNotificationEnabled(false)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
