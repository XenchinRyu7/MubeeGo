package com.saefulrdevs.mubeego.ui.authentication.signup

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.saefulrdevs.mubeego.R
import com.saefulrdevs.mubeego.core.data.Resource
import com.saefulrdevs.mubeego.databinding.FragmentSignUpBinding
import com.saefulrdevs.mubeego.ui.authentication.AuthViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SignUp : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by viewModel()

    private val googleSignInOptions: GoogleSignInOptions by lazy {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("284626842090-nlgcvgls4csstsbvrrosrs8k3c1n5upg.apps.googleusercontent.com")
            .requestEmail()
            .build()
    }

    private var pendingGoogleIdToken: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)

        setupUI()
        observeViewModel()

        return binding.root
    }

    private fun setupUI() {
        binding.registerButton.setOnClickListener {
            val fullName = binding.edInputFullName.text.toString().trim()
            val email = binding.edInputEmail.text.toString().trim()
            val password = binding.edInputSetPassword.text.toString().trim()

            if (fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Semua kolom harus diisi!", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches() || email.contains(" ")) {
                Toast.makeText(requireContext(), "Format email tidak valid!", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            authViewModel.signUpWithEmail(fullname = fullName, email = email, password = password)
        }

        binding.apply {
            googleLogin.setOnClickListener {
                signInWithGoogle()
            }
            backButton.setOnClickListener {
                findNavController().navigate(R.id.navigation_signin)
            }
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            authViewModel.authState.collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                    }
                    is Resource.Success -> {
                        binding.loading.visibility = View.GONE
                        Toast.makeText(requireContext(), "Registrasi berhasil! Silakan cek email untuk verifikasi.", Toast.LENGTH_SHORT)
                            .show()
                        val fullName = binding.edInputFullName.text.toString().trim()
                        val bundle = Bundle().apply { putString("fullname", fullName) }
                        findNavController().navigate(R.id.navigation_email_verification, bundle)
                    }
                    is Resource.Error -> {
                        binding.loading.visibility = View.GONE
                        if (resource.message?.startsWith("collision:") == true) {
                            val email = resource.message?.removePrefix("collision:") ?: ""
                            showLinkAccountDialog(email)
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Error: ${resource.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    private fun showLinkAccountDialog(email: String) {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Akun sudah terdaftar")
            .setMessage("Email ini sudah terdaftar. Ingin menghubungkan akun Google ke akun ini?\nSilakan login manual dulu, lalu klik tombol Google lagi.")
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun signInWithGoogle() {
        val signInIntent =
            GoogleSignIn.getClient(requireActivity(), googleSignInOptions).signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    private val googleSignInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    Log.d(
                        "GoogleSignIn",
                        "Akun Google dipilih: ${account?.email}, ID Token: ${account?.idToken}"
                    )
                    account?.idToken?.let { idToken ->
                        pendingGoogleIdToken = idToken
                        authViewModel.signInWithGoogle(idToken)
                    } ?: Log.e("GoogleSignIn", "ID Token kosong!")
                } catch (e: ApiException) {
                    Log.e("GoogleSignIn", "Google Sign-In gagal", e)
                    Toast.makeText(
                        requireContext(),
                        "Google Sign-In gagal: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Log.e(
                    "GoogleSignIn",
                    "Google Sign-In gagal dengan resultCode: ${result.resultCode}"
                )
            }
        }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}