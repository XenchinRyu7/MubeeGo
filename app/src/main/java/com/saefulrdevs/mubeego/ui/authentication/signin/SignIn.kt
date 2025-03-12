package com.saefulrdevs.mubeego.ui.authentication.signin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.saefulrdevs.mubeego.R
import com.saefulrdevs.mubeego.databinding.FragmentSignInBinding
import com.saefulrdevs.mubeego.ui.authentication.signup.SignUp

class SignIn : Fragment() {

    private lateinit var binding: FragmentSignInBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignInBinding.inflate(inflater, container, false)

        binding.tvSignUpClick.setOnClickListener {
            moveToRegister()
        }

            return binding.root
        }

        private fun moveToRegister() {
            parentFragmentManager.beginTransaction()
                .replace(
                    R.id.fragment_container,
                    SignUp(),
                    SignUp::class.java.simpleName
                )
                .commit()
        }
    }