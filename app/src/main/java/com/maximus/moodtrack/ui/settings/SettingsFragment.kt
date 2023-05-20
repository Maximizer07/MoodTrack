package com.maximus.moodtrack.ui.settings

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText
import com.maximus.moodtrack.R
import com.maximus.moodtrack.data.model.User
import com.maximus.moodtrack.databinding.FragmentSettingsBinding
import com.maximus.moodtrack.ui.auth.AuthViewModel
import com.maximus.moodtrack.ui.auth.SettingsViewModel
import com.maximus.moodtrack.util.UiState
import com.maximus.moodtrack.util.toast
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private lateinit var btnOpenPopup: Button
    private var dialog: Dialog? = null
    private var isButtonClicked = false
    lateinit var binding: FragmentSettingsBinding
    val authViewModel: AuthViewModel by viewModels()
    val settingsViewModel: SettingsViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        binding.btnNotification.setOnClickListener {
//            if (settingsViewModel.isNotificationsEnabled()) {
//                settingsViewModel.disableNotifications()
//            } else {
//                settingsViewModel.enableNotifications()
//            }
//        }

//        settingsViewModel.notificationEnabledLiveData.observe(viewLifecycleOwner) { isEnabled ->
//            updateNotificationButton(isEnabled)
//        }


        observer()

        fillUserData()

        settingsViewModel.popupVisible.observe(viewLifecycleOwner, Observer { isOpen ->
            if (isOpen) {
                openPopup()
            } else {
                closePopup()
            }
        })


        binding.btnEditProfile.setOnClickListener {
            settingsViewModel.openPopup()
        }

        authViewModel.bottomNavVisible.observe(viewLifecycleOwner) { visible ->
            val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)
            bottomNavigationView.visibility = if (visible) View.VISIBLE else View.GONE
        }

        binding.btnLogout.setOnClickListener {
            authViewModel.logout {
                authViewModel.setBottomNavVisible(false)
                findNavController().navigate(R.id.action_settingsFragment_to_loginFragment)
            }
        }

    }

    private fun openPopup() {
        if (dialog == null) {
            dialog = Dialog(requireContext(), R.style.DialogTheme)
            dialog?.setContentView(R.layout.pop_up_userdata)
            dialog?.window?.setWindowAnimations(R.style.DialogAnimation)
//            dialog?.setCanceledOnTouchOutside(false)
            val nicknameTextInputEditText =
                dialog?.findViewById<TextInputEditText>(R.id.editNickname)
            val nameTextTextInputEditText = dialog?.findViewById<TextInputEditText>(R.id.editName)
            nicknameTextInputEditText?.setText(binding.nickname.text)
            nameTextTextInputEditText?.setText(binding.name.text)


            val btnSave = dialog?.findViewById<Button>(R.id.btn_save)
            val btnClose = dialog?.findViewById<ImageView>(R.id.btnClose)

            var nickname = ""
            var name = ""

            fun validation(): Boolean {
                var isValid = true
                nickname =
                    dialog?.findViewById<TextInputEditText>(R.id.editNickname)?.text.toString()
                name = dialog?.findViewById<TextInputEditText>(R.id.editName)?.text.toString()
                if (nickname.isEmpty()) {
                    isValid = false
                    toast(getString(R.string.enter_nickname))
                } else if (name.isEmpty()) {
                    isValid = false
                    toast(getString(R.string.enter_last_name))
                }
                return isValid
            }

            btnSave?.setOnClickListener {
                if (validation()) {
                    authViewModel.getSession { user ->
                        if (user != null) {
                            authViewModel.updateUserInfo(
                                User(
                                    name = name,
                                    nickname = nickname,
                                    id = user.id,
                                    email = user.email
                                )
                            )
                        }
                    }
                    settingsViewModel.closePopup()
                }

            }

            dialog?.setOnCancelListener {
                settingsViewModel.closePopup()
            }

            btnClose?.setOnClickListener {
                settingsViewModel.closePopup()
            }
            dialog?.show()


        }
    }

    private fun closePopup() {
        if (dialog != null) {
            dialog?.dismiss()
            dialog = null
        }
    }

    fun fillUserData() {
        authViewModel.getSession { user ->
            if (user != null) {
                binding.name.text = user.name
                binding.nickname.text = user.nickname
                binding.email.text = user.email
            }
        }
    }

    fun observer() {
        authViewModel.edit.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Failure -> {
                    toast(state.error)
                }

                is UiState.Success -> {
                    fillUserData()
                    toast(state.data)
                }
            }
        }
    }

//    private fun updateNotificationButton(isEnabled: Boolean) {
//        if (isEnabled) {
//            if (isButtonClicked) toast("Уведомления включены!")
//            binding.btnNotification.text = "Выключить уведомления"
//            binding.btnNotification.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.notificationEnabledColor)
//        } else {
//            if (isButtonClicked) toast("Уведомления выключены!")
//            binding.btnNotification.text = "Включить уведомления"
//            binding.btnNotification.backgroundTintList = ContextCompat.getColorStateList(requireContext(),R.color.notificationDisabledColor)
//        }
//        isButtonClicked = true
//    }
}
