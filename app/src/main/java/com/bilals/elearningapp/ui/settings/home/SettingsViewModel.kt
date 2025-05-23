package com.bilals.elearningapp.ui.settings.home

//import com.bilals.elearningapp.data.DummyDataProvider
//import com.bilals.elearningapp.navigation.NavDataManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SettingsViewModel : ViewModel() {

    // LiveData for the switch role text
    private val _switchRoleText = MutableLiveData<String>()
    val switchRoleText: LiveData<String> get() = _switchRoleText

    // Function to set the switch role text
    fun setSwitchRoleText(roleText: String) {
        _switchRoleText.value = roleText
    }
}
