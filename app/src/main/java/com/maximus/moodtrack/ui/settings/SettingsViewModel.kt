package com.maximus.moodtrack.ui.auth

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.maximus.moodtrack.NotificationHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val notificationHelper: NotificationHelper,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {
    private val NOTIFICATION_ENABLED_KEY = "notification_enabled"
    private val _popupVisible: MutableLiveData<Boolean> = MutableLiveData()
    val popupVisible: LiveData<Boolean> get() = _popupVisible

    private val _notificationEnabledLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val notificationEnabledLiveData: LiveData<Boolean> get() = _notificationEnabledLiveData


    init {
        val isNotificationEnabled = sharedPreferences.getBoolean(NOTIFICATION_ENABLED_KEY, false)
        _notificationEnabledLiveData.value = isNotificationEnabled
    }

    fun openPopup() {
        _popupVisible.value = true
    }

    fun closePopup() {
        _popupVisible.value = false
    }


    fun enableNotifications() {
        notificationHelper.scheduleNotification()
        _notificationEnabledLiveData.value = true
        sharedPreferences.edit().putBoolean(NOTIFICATION_ENABLED_KEY, true).apply()
    }

    fun disableNotifications() {
        notificationHelper.cancelNotification()
        _notificationEnabledLiveData.value = false
        sharedPreferences.edit().putBoolean(NOTIFICATION_ENABLED_KEY, false).apply()
    }

    fun isNotificationsEnabled(): Boolean {
        return _notificationEnabledLiveData.value ?: false
    }


}