package com.bilals.elearningapp.data.remote

object FirebaseServiceSingleton {
    val instance: FirebaseService by lazy {
        FirebaseService() // Create the FirebaseService instance lazily
    }
}
