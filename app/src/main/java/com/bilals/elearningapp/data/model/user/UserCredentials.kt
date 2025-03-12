package com.bilals.elearningapp.data.model.user

import java.io.Serializable

data class UserCredentials(
    val email: String,           // Email used for login
    val password: String,        // Plain password (should be hashed in production)
) : Serializable
