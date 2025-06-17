package com.saefulrdevs.mubeego.ui.authentication.verification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.saefulrdevs.mubeego.R
import com.saefulrdevs.mubeego.databinding.FragmentEmailVerificationBinding
import com.saefulrdevs.mubeego.ui.authentication.AuthViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import kotlin.getValue

class EmailVerificationFragment : Fragment() {
    private var _binding: FragmentEmailVerificationBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by activityViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEmailVerificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnBackToLogin.setOnClickListener {
            findNavController().navigate(R.id.navigation_signin)
        }
        binding.btnCheckVerification.setOnClickListener {
            val user = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
            user?.reload()?.addOnCompleteListener { reloadTask ->
                if (reloadTask.isSuccessful && user.isEmailVerified) {
                    val fullname = arguments?.getString("fullname") ?: ""
                    requireActivity().let { act ->
                        authViewModel.createUserFirestoreAfterVerified(fullname)
                        viewLifecycleOwner.lifecycleScope.launch {
                            authViewModel.authState.collect { resource ->
                                if (resource is com.saefulrdevs.mubeego.core.data.Resource.Success) {
                                    findNavController().navigate(R.id.navigation_signin)
                                } else if (resource is com.saefulrdevs.mubeego.core.data.Resource.Error) {
                                    android.widget.Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "Email belum diverifikasi!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
