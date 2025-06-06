package com.saefulrdevs.mubeego.ui.main.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.saefulrdevs.mubeego.core.util.fetchUserDataFromFirestore
import com.saefulrdevs.mubeego.databinding.FragmentProfileUpdateBinding
import kotlinx.coroutines.launch


class ProfileUpdateFragment : Fragment() {

    private var _binding: FragmentProfileUpdateBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileUpdateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            lifecycleScope.launch {
                val user = fetchUserDataFromFirestore(uid)
                user?.let {
                    binding.etUsername.setText(it.fullname)
                    binding.etEmail.setText(it.email)
                }
            }
        }

        binding.btnUpdate.setOnClickListener {
            binding.etUsername.text.toString()
            binding.etPassword.text.toString()
            // TODO: Implement update logic (update username and password in Firestore/FirebaseAuth)
            Toast.makeText(requireContext(), "Update clicked", Toast.LENGTH_SHORT).show()
        }
    }
}