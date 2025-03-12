package com.bilals.elearningapp.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
//import com.bilals.elearningapp.data.DummyDataProvider
import com.bilals.elearningapp.data.model.user.UserRole
//import com.bilals.elearningapp.navigation.NavDataManager
import com.bilals.elearningapp.navigation.ScreenRoutes

class SettingsViewModel : ViewModel() {

    // LiveData for the switch role text
    private val _switchRoleText = MutableLiveData<String>()
    val switchRoleText: LiveData<String> get() = _switchRoleText

    // Function to set the switch role text
    fun setSwitchRoleText(roleText: String) {
        _switchRoleText.value = roleText
    }
}
