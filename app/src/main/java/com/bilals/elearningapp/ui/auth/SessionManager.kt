package com.bilals.elearningapp.ui.auth

import android.content.Context
import com.bilals.elearningapp.data.model.user.RoleType

object SessionManager {

    private const val PREF_NAME = "user_prefs"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_ACTIVE_ROLE = "active_role"

    fun saveUserIdToPreferences(userId: String, context: Context) {
        val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPref.edit().putString(KEY_USER_ID, userId).apply()
    }

    fun getUserIdFromPreferences(context: Context): String? {
        val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPref.getString(KEY_USER_ID, null)
    }

    fun saveActiveRole(role: RoleType, context: Context) {
        val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPref.edit().putString(KEY_ACTIVE_ROLE, role.name).apply()
    }

    fun getActiveRole(context: Context): RoleType {
        val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return when (sharedPref.getString(KEY_ACTIVE_ROLE, RoleType.STUDENT.name)) {
            RoleType.INSTRUCTOR.name -> RoleType.INSTRUCTOR
            else -> RoleType.STUDENT
        }
    }

    fun clearUserIdFromPreferences(context: Context) {
        val sharedPref = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        sharedPref.edit().remove("user_id").apply()
    }
}
