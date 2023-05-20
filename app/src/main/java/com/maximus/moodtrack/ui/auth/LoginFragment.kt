package com.maximus.moodtrack.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.maximus.moodtrack.R
import com.maximus.moodtrack.databinding.FragmentLoginBinding
import com.maximus.moodtrack.util.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    val TAG: String = "LoginFragment"
    lateinit var binding: FragmentLoginBinding
    val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observer()
        binding.loginBtn.setOnClickListener {
            if (validation()) {
                viewModel.login(
                    email = binding.emailEt.text.toString(),
                    password = binding.passEt.text.toString()
                )
            }
        }

        binding.forgotPassLabel.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_forgotPasswordFragment)
        }

        binding.registerLabel.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }


        viewModel.bottomNavVisible.observe(viewLifecycleOwner) { visible ->
            val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)
            bottomNavigationView.visibility = if (visible) View.VISIBLE else View.GONE
        }

    }

    fun observer() {
        viewModel.login.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.loginBtn.setText("")
                    binding.loginProgress.show()
                }

                is UiState.Failure -> {
                    binding.loginBtn.setText("Login")
                    binding.loginProgress.hide()
                    toast(state.error)
                }

                is UiState.Success -> {
                    binding.loginBtn.setText("Login")
                    binding.loginProgress.hide()
                    toast(state.data)
                    viewModel.setBottomNavVisible(true)
                    findNavController().navigate(R.id.action_loginFragment_to_home_navigation)
                }
            }
        }
    }

    fun validation(): Boolean {
        var isValid = true

        if (binding.emailEt.text.isNullOrEmpty()) {
            isValid = false
            toast(getString(R.string.enter_email))
            return isValid
        } else {
            if (!binding.emailEt.text.toString().isValidEmail()) {
                isValid = false
                toast(getString(R.string.invalid_email))
                return isValid
            }
        }
        if (binding.passEt.text.isNullOrEmpty()) {
            isValid = false
            toast(getString(R.string.enter_password))
            return isValid
        } else {
            if (binding.passEt.text.toString().length < 8) {
                isValid = false
                toast(getString(R.string.invalid_password))
                return isValid
            }
        }
        return isValid
    }

    override fun onStart() {
        super.onStart()
        viewModel.getSession { user ->
            if (user != null) {
                viewModel.setBottomNavVisible(true)
                findNavController().navigate(R.id.action_loginFragment_to_home_navigation)
            }
        }
    }

}