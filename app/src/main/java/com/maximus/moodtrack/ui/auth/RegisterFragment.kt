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
import com.maximus.moodtrack.data.model.User
import com.maximus.moodtrack.databinding.FragmentRegisterBinding
import com.maximus.moodtrack.util.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    val TAG: String = "RegisterFragment"
    lateinit var binding: FragmentRegisterBinding
    val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observer()
        binding.registerBtn.setOnClickListener {
            if (validation()) {
                viewModel.register(
                    email = binding.emailEt.text.toString(),
                    password = binding.passEt.text.toString(),
                    user = getUserObj()
                )
            }
        }

        viewModel.bottomNavVisible.observe(viewLifecycleOwner) { visible ->
            val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)
            bottomNavigationView.visibility = if (visible) View.VISIBLE else View.GONE
        }

        binding.loginLabel.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }

    fun observer() {
        viewModel.register.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.registerBtn.setText("")
                    binding.registerProgress.show()
                }

                is UiState.Failure -> {
                    binding.registerBtn.setText("Register")
                    binding.registerProgress.hide()
                    toast(state.error)
                }

                is UiState.Success -> {
                    binding.registerBtn.setText("Register")
                    binding.registerProgress.hide()
                    toast(state.data)
                    viewModel.setBottomNavVisible(true)
                    findNavController().navigate(R.id.action_registerFragment_to_home_navigation)
                }
            }
        }
    }

    fun getUserObj(): User {
        return User(
            id = "",
            name = binding.nameEt.text.toString(),
            nickname = binding.nicknameEt.text.toString(),
            email = binding.emailEt.text.toString(),
        )
    }

    fun validation(): Boolean {
        var isValid = true


        if (binding.nameEt.text.isNullOrEmpty()) {
            isValid = false
            toast(getString(R.string.enter_last_name))
            return isValid
        }

        if (binding.nicknameEt.text.isNullOrEmpty()) {
            isValid = false
            toast(getString(R.string.enter_nickname))
            return isValid
        }

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

}