package com.saefulrdevs.mubeego.ui.authentication.forgot

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.util.Patterns
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.saefulrdevs.mubeego.R
import com.saefulrdevs.mubeego.core.data.Resource
import com.saefulrdevs.mubeego.databinding.FragmentForgotPasswordBinding
import com.saefulrdevs.mubeego.ui.authentication.AuthViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ForgotPassword : Fragment() {

    private var _binding: FragmentForgotPasswordBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by activityViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnSendEmail.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches() || email.contains(" ")) {
                binding.tvError.text = "Format email tidak valid!"
                binding.tvError.visibility = View.VISIBLE
                return@setOnClickListener
            }
            binding.tvError.visibility = View.GONE
            authViewModel.sendPasswordResetEmail(email)
        }
        binding.tvLogin.setOnClickListener {
            findNavController().navigate(R.id.navigation_signin)
        }
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
        lifecycleScope.launch {
            authViewModel.resetPasswordState.collectLatest { resource ->
                resource?.let {
                    when (it) {
                        is Resource.Loading -> {
                            binding.btnSendEmail.isEnabled = false
                        }
                        is Resource.Success -> {
                            binding.btnSendEmail.isEnabled = true
                            findNavController().navigate(R.id.action_forgotPassword_to_forgotPasswordConfirmation)
                        }
                        is Resource.Error -> {
                            binding.btnSendEmail.isEnabled = true
                            binding.tvError.text = it.message
                            binding.tvError.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }
}