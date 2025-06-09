package com.saefulrdevs.mubeego.ui.main.profile

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.saefulrdevs.mubeego.R
import com.saefulrdevs.mubeego.core.domain.usecase.UserPreferencesUseCase
import com.saefulrdevs.mubeego.core.util.fetchUserDataFromFirestore
import com.saefulrdevs.mubeego.databinding.FragmentProfileBinding
import com.saefulrdevs.mubeego.ui.authentication.AuthActivity
import com.saefulrdevs.mubeego.ui.authentication.AuthViewModel
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import androidx.core.net.toUri

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val userPreferencesUseCase: UserPreferencesUseCase by inject()
    private val authViewModel: AuthViewModel by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = userPreferencesUseCase.getUser()
        user?.let {
            binding.tvProfileUsername.text = it.fullname
            binding.tvProfileEmail.text = it.email
        }

        binding.llSubscription.setOnClickListener {
            val user = userPreferencesUseCase.getUser()
            val uid = user?.uid
            if (uid != null) {
                lifecycleScope.launch {
                    val userData = fetchUserDataFromFirestore(uid)
                    val isPremium = userData?.isPremium == true
                    val dialogMsg = if (isPremium) "You are premium!" else "You are not premium."
                    val builder = AlertDialog.Builder(requireContext())
                        .setTitle("Subscription Status")
                        .setMessage(dialogMsg)
                    if (!isPremium) {
                        val uid = user.uid
                        val username = user.fullname
                        val email = user.email
                        val paymentUrl = "https://payment-gateway-ashen-zeta.vercel.app/${uid}/${username}/${email}"
                        builder.setPositiveButton("Open Payment Web") { d, _ ->
                            val intent = Intent(Intent.ACTION_VIEW)
                            intent.data = paymentUrl.toUri()
                            startActivity(intent)
                            d.dismiss()
                        }
                        builder.setNegativeButton("Cancel") { d, _ -> d.dismiss() }
                    } else {
                        builder.setPositiveButton("OK") { d, _ -> d.dismiss() }
                    }
                    builder.show()
                }
            }
        }

        binding.llSettings.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_settingsFragment)
        }
        binding.llMyAccount.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_profileUpdateFragment)
        }
        binding.llLogout.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Logout")
            builder.setMessage("Apakah Anda yakin ingin logout?")
            builder.setPositiveButton("Yes") { dialog, _ ->
                signOutGoogle(requireContext())
                authViewModel.signOut()
                dialog.dismiss()
                val intent = Intent(requireContext(), AuthActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            builder.setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss()
            }
            builder.show()
        }

        binding.llHelp.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = "https://mubee-go-about.vercel.app/".toUri()
            startActivity(intent)
        }
        binding.llAbout.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = "https://mubee-go-about.vercel.app/".toUri()
            startActivity(intent)
        }
    }

    private fun signOutGoogle(context: Context) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("284626842090-nlgcvgls4csstsbvrrosrs8k3c1n5upg.apps.googleusercontent.com")
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(context, gso)
        googleSignInClient.signOut()
            .addOnCompleteListener {
                Log.d("SignOut", "Google account signed out successfully.")
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

