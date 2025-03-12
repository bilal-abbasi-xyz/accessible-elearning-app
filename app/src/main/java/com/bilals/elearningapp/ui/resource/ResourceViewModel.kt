package com.bilals.elearningapp.ui.resource

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.bilals.elearningapp.data.model.Resource
//import com.bilals.elearningapp.data.DummyDataProvider

class ResourceViewModel : ViewModel() {

    val resource = mutableStateOf<Resource?>(null)

    fun loadResource(resourceId: String) {

//        val foundResource = DummyDataProvider().getResourceById(resourceId)
//        resource.value = foundResource
    }
}