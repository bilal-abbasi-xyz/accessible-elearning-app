package com.bilals.elearningapp.data.model

import com.bilals.elearningapp.data.model.user.User
import java.io.Serializable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.google.firebase.firestore.PropertyName

@Entity(
        tableName = "courses",
        foreignKeys = [
            ForeignKey(
                entity = CourseCategory::class,
                parentColumns = ["id"],
                childColumns = ["categoryId"],
                onDelete = ForeignKey.CASCADE
            )
        ]
    )
data class Course(
    @PrimaryKey val id: String = "",
    val name: String = "",
    val categoryId: String = "",
    val instructorId: String = "",
    val maxPoints: Int = 0,

    @get:PropertyName("isPublished") @set:PropertyName("isPublished")
    var isPublished: Boolean = false // ✅ Fix: Firestore will now properly map this
) {
    constructor() : this("", "", "", "", 0, false) // ✅ Firestore needs this empty constructor
}