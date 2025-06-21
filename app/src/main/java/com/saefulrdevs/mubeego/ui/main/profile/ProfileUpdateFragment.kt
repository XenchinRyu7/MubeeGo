package com.saefulrdevs.mubeego.ui.main.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.saefulrdevs.mubeego.core.util.fetchUserDataFromFirestore
import com.saefulrdevs.mubeego.databinding.FragmentProfileUpdateBinding
import kotlinx.coroutines.launch
import com.saefulrdevs.mubeego.R
import com.saefulrdevs.mubeego.core.util.updateUserFullnameInFirestore


class ProfileUpdateFragment : Fragment() {

    private var _binding: FragmentProfileUpdateBinding? = null
    private val binding get() = _binding!!

    private val googleSignInOptions: GoogleSignInOptions by lazy {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("284626842090-nlgcvgls4csstsbvrrosrs8k3c1n5upg.apps.googleusercontent.com")
            .requestEmail()
            .build()
    }
    private val googleLinkLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == android.app.Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    FirebaseAuth.getInstance().currentUser?.linkWithCredential(credential)
                        ?.addOnCompleteListener { linkTask ->
                            if (linkTask.isSuccessful) {
                                Toast.makeText(requireContext(), "Akun Google berhasil di-link!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(requireContext(), "Gagal link akun: ${linkTask.exception?.localizedMessage}", Toast.LENGTH_SHORT).show()
                            }
                        }
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Google Sign-In gagal: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileUpdateBinding.inflate(inflater, container, false)
        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid
        Log.d("ProfileUpdate", "user=$user, photoUrl=${user?.photoUrl}, providerData=${user?.providerData}")
        if (uid != null) {
            lifecycleScope.launch {
                val userData = fetchUserDataFromFirestore(uid)
                userData?.let {
                    binding.etUsername.setText(it.fullname)
                    binding.etEmail.setText(it.email)
                }
            }
        }
        val isGoogleUser = user?.providerData?.any { it.providerId == "google.com" } == true
        Log.d("ProfileUpdate", "isGoogleUser=$isGoogleUser, photoUrl=${user?.photoUrl}")
        if (isGoogleUser && user.photoUrl != null) {
            Glide.with(this)
                .load(user.photoUrl)
                .into(binding.imgProfile)
            binding.tvChangePicture.visibility = View.GONE
        } else {
            binding.tvChangePicture.visibility = View.VISIBLE
        }
        binding.btnUpdate.setOnClickListener {
            val newFullname = binding.etUsername.text.toString().trim()
            if (newFullname.isEmpty()) {
                Toast.makeText(requireContext(), "Fullname tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                lifecycleScope.launch {
                    val success = updateUserFullnameInFirestore(user.uid, newFullname)
                    if (success) {
                        Toast.makeText(requireContext(), "Nama berhasil diubah", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Gagal mengubah nama", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.tvForgotPassword.setOnClickListener {
            findNavController().navigate(R.id.navigation_forgot_password)
        }

        binding.btnLinkGoogle.setOnClickListener {
            val signInIntent = GoogleSignIn.getClient(requireActivity(), googleSignInOptions).signInIntent
            googleLinkLauncher.launch(signInIntent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}