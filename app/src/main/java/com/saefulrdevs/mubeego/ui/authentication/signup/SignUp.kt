package com.saefulrdevs.mubeego.ui.authentication.signup

import android.app.Activity
import android.os.Bundle
import android.util.Log
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

    private lateinit var binding: FragmentSignUpBinding

    private val authViewModel: AuthViewModel by viewModel()

    private val googleSignInOptions: GoogleSignInOptions by lazy {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("284626842090-nlgcvgls4csstsbvrrosrs8k3c1n5upg.apps.googleusercontent.com")
            .requestEmail()
            .build()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)

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
                Toast.makeText(requireContext(), "Semua kolom harus diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            authViewModel.signUpWithEmail(email, password, fullName)
        }

        binding.googleLogin.setOnClickListener {
            signInWithGoogle()
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            authViewModel.authState.collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
//                        binding.loading.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.loading.visibility = View.GONE
                        Toast.makeText(requireContext(), "Registrasi berhasil!", Toast.LENGTH_SHORT).show()
//                        findNavController().navigate(R.id.action_signUpFragment_to_homeFragment)
                    }
                    is Resource.Error -> {
                        binding.loading.visibility = View.GONE
                        Toast.makeText(requireContext(), "Error: ${resource.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun signInWithGoogle() {
        val signInIntent = GoogleSignIn.getClient(requireActivity(), googleSignInOptions).signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    private val googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                Log.d("GoogleSignIn", "Akun Google dipilih: ${account?.email}, ID Token: ${account?.idToken}")

                account?.idToken?.let { idToken ->
                    authViewModel.signInWithGoogle(idToken)
                } ?: Log.e("GoogleSignIn", "ID Token kosong!")

            } catch (e: ApiException) {
                Log.e("GoogleSignIn", "Google Sign-In gagal", e)
                Toast.makeText(requireContext(), "Google Sign-In gagal: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Log.e("GoogleSignIn", "Google Sign-In dibatalkan atau gagal")
            Log.e("GoogleSignIn", "Google Sign-In gagal dengan resultCode: ${result.resultCode}")
        }
    }

}