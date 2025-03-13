package com.bilals.elearningapp.data.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: String = "",
    val name: String = "",
    val email: String = "",
    val totalPoints: Int = 0,
    val activeRole: RoleType = RoleType.STUDENT,
    @Embedded val preferences: UserPreferences = UserPreferences()
)

data class UserPreferences(
    val themeColor: String = "#FFFFFF",
    val notificationsEnabled: Boolean = true
)

enum class RoleType { STUDENT, INSTRUCTOR }

