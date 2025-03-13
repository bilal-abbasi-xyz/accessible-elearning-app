package com.bilals.elearningapp.data.repository

import com.bilals.elearningapp.data.local.UserDao
import com.bilals.elearningapp.data.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class UserRepository( private val userDao: UserDao) {
    private val firestore = FirebaseFirestore.getInstance()

    fun getUserById(userId: String, onComplete: (User?) -> Unit) {
        firestore.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)
                if (user != null) {
                    CoroutineScope(Dispatchers.IO).launch {
                        userDao.insertUser(user)  // Cache in Room
                    }
                }
                onComplete(user)
            }
            .addOnFailureListener {
                onComplete(null)
            }
    }

    fun getUserFromRoom(userId: String): Flow<User> {
        return userDao.getUserById(userId)
    }
}
