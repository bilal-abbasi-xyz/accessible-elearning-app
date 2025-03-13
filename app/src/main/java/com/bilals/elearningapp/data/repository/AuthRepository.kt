package com.bilals.elearningapp.data.repository

import com.bilals.elearningapp.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository(private val auth: FirebaseAuth) {

    fun registerUser(email: String, password: String, name: String, onComplete: (Boolean) -> Unit) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                val user = authResult.user
                if (user != null) {
                    val userData = User(
                        id = user.uid,
                        name = name,
                        email = email
                    )
                    FirebaseFirestore.getInstance().collection("users").document(user.uid).set(userData)
                        .addOnSuccessListener { onComplete(true) }
                        .addOnFailureListener { onComplete(false) }
                }
            }
            .addOnFailureListener { onComplete(false) }
    }


    suspend fun loginWithEmailAndPassword(email: String, password: String): Result<String> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            Result.success(result.user?.uid ?: "Unknown User")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
