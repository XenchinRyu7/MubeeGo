package com.saefulrdevs.mubeego.ui.authentication.forgot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.saefulrdevs.mubeego.R
import com.saefulrdevs.mubeego.databinding.FragmentForgotPasswordConfirmationBinding

class ForgotPasswordConfirmation : Fragment() {
    private var _binding: FragmentForgotPasswordConfirmationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForgotPasswordConfirmationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnBackToLogin.setOnClickListener {
            findNavController().navigate(R.id.navigation_signin)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
