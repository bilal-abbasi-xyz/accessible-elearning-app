package com.bilals.elearningapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bilals.elearningapp.data.model.ChatMessage
import com.bilals.elearningapp.data.model.Course
import com.bilals.elearningapp.data.model.CourseCategory
import com.bilals.elearningapp.data.model.Lecture
import com.bilals.elearningapp.data.model.Resource
import com.bilals.elearningapp.data.model.Section
import com.bilals.elearningapp.data.model.quiz.Answer
import com.bilals.elearningapp.data.model.quiz.Question
import com.bilals.elearningapp.data.model.quiz.Quiz
import com.bilals.elearningapp.data.model.user.User

@Database(
    entities = [
        CourseCategory::class, Course::class, Section::class, Lecture::class, Resource::class,
        Quiz::class, Question::class, Answer::class, ChatMessage::class, User::class
    ],
    version = 1
)
abstract class ElearningDatabase : RoomDatabase() {
    abstract fun courseDao(): CourseDao
    abstract fun courseCategoryDao(): CourseCategoryDao
    abstract fun sectionDao(): SectionDao
    abstract fun quizDao(): QuizDao
    abstract fun lectureDao(): LectureDao
    abstract fun resourceDao(): ResourceDao
    abstract fun questionDao(): QuestionDao
    abstract fun answerDao(): AnswerDao
    abstract fun chatMessageDao(): ChatMessageDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: ElearningDatabase? = null

        fun getDatabase(context: android.content.Context): ElearningDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = androidx.room.Room.databaseBuilder(
                    context.applicationContext,
                    ElearningDatabase::class.java,
                    "elearning_database"
                )
                    .fallbackToDestructiveMigration()  // Automatically reset the database on version changes
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
