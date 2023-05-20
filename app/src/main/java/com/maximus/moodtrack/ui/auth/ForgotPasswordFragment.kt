package com.maximus.moodtrack.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.maximus.moodtrack.R
import com.maximus.moodtrack.databinding.FragmentForgotPasswordBinding
import com.maximus.moodtrack.util.*
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ForgotPasswordFragment : Fragment() {

    val TAG: String = "ForgotPasswordFragment"
    lateinit var binding: FragmentForgotPasswordBinding
    val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentForgotPasswordBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observer()
        binding.forgotPassBtn.setOnClickListener {
            if (validation()) {
                viewModel.forgotPassword(binding.emailEt.text.toString())
            }
        }

        binding.loginLabel.setOnClickListener {
            findNavController().navigate(R.id.action_forgotPassword_to_loginFragment)
        }
    }

    private fun observer() {
        viewModel.forgotPassword.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.forgotPassBtn.setText("")
                    binding.forgotPassProgress.show()
                }

                is UiState.Failure -> {
                    binding.forgotPassBtn.setText("Send")
                    binding.forgotPassProgress.hide()
                    toast(state.error)
                }

                is UiState.Success -> {
                    binding.forgotPassBtn.setText("Send")
                    binding.forgotPassProgress.hide()
                    toast(state.data)
                }
            }
        }
    }

    fun validation(): Boolean {
        var isValid = true

        if (binding.emailEt.text.isNullOrEmpty()) {
            isValid = false
            toast(getString(R.string.enter_email))
        } else {
            if (!binding.emailEt.text.toString().isValidEmail()) {
                isValid = false
                toast(getString(R.string.invalid_email))
            }
        }

        return isValid
    }


}