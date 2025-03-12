package com.bilals.elearningapp.data.remote

import android.util.Log
import com.bilals.elearningapp.data.model.ChatMessage
import com.bilals.elearningapp.data.model.Course
import com.bilals.elearningapp.data.model.CourseCategory
import com.bilals.elearningapp.data.model.Lecture
import com.bilals.elearningapp.data.model.Resource
import com.bilals.elearningapp.data.model.Section
import com.bilals.elearningapp.data.model.quiz.Answer
import com.bilals.elearningapp.data.model.quiz.Question
import com.bilals.elearningapp.data.model.quiz.Quiz
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await


class FirebaseService {
    private val firestore = FirebaseFirestore.getInstance()

    fun listenForCategoryUpdates(onDataChange: (List<CourseCategory>) -> Unit) {
        firestore.collection("course_categories")
            .addSnapshotListener { snapshot, _ ->
                snapshot?.let {
                    val categories = it.documents.mapNotNull { doc ->
                        doc.toObject(CourseCategory::class.java)?.copy(id = doc.id)
                    }
                    onDataChange(categories)
                }
            }
    }

    fun listenForCourseUpdates(categoryId: String, onDataChange: (List<Course>) -> Unit) {
        firestore.collection("course_categories")  // 🔥 Top-level collection
            .document(categoryId)  // ✅ Select the correct category
            .collection("courses")  // 🔥 Access subcollection inside the category
            .addSnapshotListener { snapshot, _ ->
                snapshot?.let {
                    val courses = it.documents.mapNotNull { doc ->
                        doc.toObject(Course::class.java)?.copy(id = doc.id)
                    }
                    onDataChange(courses)  // ✅ Pass updated courses
                }
            }
    }

    fun listenForSectionUpdates(courseId: String, onDataChange: (List<Section>) -> Unit) {
        firestore.collectionGroup("courses")  // 🔥 Search all "courses" subcollections
            .whereEqualTo(FieldPath.documentId(), courseId)  // ✅ Find course by ID
            .limit(1)  // 🚀 Only fetch one document (since ID is unique)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val courseDoc = querySnapshot.documents.firstOrNull()
                courseDoc?.reference?.collection("sections")  // 🔥 Get sections subcollection
                    ?.addSnapshotListener { snapshot, _ ->
                        snapshot?.let {
                            val sections = it.documents.mapNotNull { doc ->
                                doc.toObject(Section::class.java)?.copy(id = doc.id)
                            }
                            onDataChange(sections)  // ✅ Pass updated sections
                        }
                    }
            }
    }

    fun listenForLectureUpdates(sectionId: String, onDataChange: (List<Lecture>) -> Unit) {
        firestore.collectionGroup("sections") // 🔥 Search all sections
            .whereEqualTo(FieldPath.documentId(), sectionId)
            .limit(1)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val sectionDoc = querySnapshot.documents.firstOrNull()
                sectionDoc?.reference?.collection("lectures") // 🔥 Listen to lectures
                    ?.addSnapshotListener { snapshot, _ ->
                        snapshot?.let {
                            val lectures = it.documents.mapNotNull { doc ->
                                doc.toObject(Lecture::class.java)?.copy(id = doc.id)
                            }
                            onDataChange(lectures)
                        }
                    }
            }
    }

    fun listenForResourceUpdates(sectionId: String, onDataChange: (List<Resource>) -> Unit) {
        firestore.collectionGroup("sections")
            .whereEqualTo(FieldPath.documentId(), sectionId)
            .limit(1)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val sectionDoc = querySnapshot.documents.firstOrNull()
                sectionDoc?.reference?.collection("resources") // 🔥 Listen to resources
                    ?.addSnapshotListener { snapshot, _ ->
                        snapshot?.let {
                            val resources = it.documents.mapNotNull { doc ->
                                doc.toObject(Resource::class.java)?.copy(id = doc.id)
                            }
                            onDataChange(resources)
                        }
                    }
            }
    }

    fun listenForQuizUpdates(sectionId: String, onDataChange: (List<Quiz>) -> Unit) {
        firestore.collectionGroup("sections")
            .whereEqualTo(FieldPath.documentId(), sectionId)
            .limit(1)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val sectionDoc = querySnapshot.documents.firstOrNull()
                sectionDoc?.reference?.collection("quizzes") // 🔥 Listen to quizzes
                    ?.addSnapshotListener { snapshot, _ ->
                        snapshot?.let {
                            val quizzes = it.documents.mapNotNull { doc ->
                                doc.toObject(Quiz::class.java)?.copy(id = doc.id)
                            }
                            onDataChange(quizzes)
                        }
                    }
            }
    }

    suspend fun getAllCategories(): List<CourseCategory> {
        val snapshot = firestore.collection("course_categories").get().await()
        return snapshot.documents.mapNotNull { doc ->
            doc.toObject(CourseCategory::class.java)?.copy(id = doc.id)
        }
    }


    suspend fun getCoursesByCategory(categoryId: String): List<Course> {
        val snapshot = firestore.collection("course_categories")
            .document(categoryId)
            .collection("courses")
            .get().await()

        snapshot.documents.forEach { doc ->
            Log.d("FirebaseData", "Raw Data: ${doc.data}")
        }

        return snapshot.documents.mapNotNull { doc ->
            val course = doc.toObject(Course::class.java)
            Log.d("DeserializedCourse", "ID: ${doc.id}, isPublished: ${course?.isPublished}")
            course?.copy(id = doc.id)
        }
    }

    suspend fun updateCourse(categoryId: String, course: Course) {
        try {
            firestore.collection("course_categories")
                .document(categoryId)
                .collection("courses")
                .document(course.id)
                .set(course)
                .await()

            Log.d("CourseRepository", "Course updated successfully: ${course.id}")
        } catch (e: Exception) {
            Log.e("CourseRepository", "Error updating course", e)
        }
    }


    suspend fun getSectionsByCourse(courseId: String): List<Section> {
        val snapshot =
            firestore.collectionGroup("sections")  // 🔥 Searches all "sections" subcollections
                .whereEqualTo("courseId", courseId)  // 🔍 Find sections under any course
                .get()
                .await()

        return snapshot.toObjects(Section::class.java)
    }

    suspend fun addSection(section: Section) {
        // Find the course document first
        val snapshot = firestore.collectionGroup("courses")
            .whereEqualTo("id", section.courseId) // Ensure we're working with the correct course
            .get()
            .await()

        val courseDoc = snapshot.documents.firstOrNull()

        if (courseDoc != null) {
            // Get reference to the "sections" subcollection and add the section
            courseDoc.reference.collection("sections")
                .document(section.id)
                .set(section)
                .await()
        } else {
            throw Exception("Course not found for courseId: $section.courseId") // Handle missing course
        }
    }


    suspend fun getQuizzesBySection(sectionId: String): List<Quiz> {
        val snapshot = firestore.collectionGroup("quizzes")
            .whereEqualTo("sectionId", sectionId)
            .get()
            .await()

        return snapshot.toObjects(Quiz::class.java)
    }

    suspend fun addQuiz(quiz: Quiz) {
        // Find the section document first
        val snapshot = firestore.collectionGroup("sections")
            .whereEqualTo("id", quiz.sectionId) // Ensure we're working with the correct section
            .get()
            .await()

        val sectionDoc = snapshot.documents.firstOrNull()

        if (sectionDoc != null) {
            // Get reference to the "quizzes" subcollection and add the quiz
            sectionDoc.reference.collection("quizzes")
                .document(quiz.id)
                .set(quiz)
                .await()
        } else {
            throw Exception("Section not found for sectionId: $quiz.sectionId") // Handle missing section
        }
    }


    suspend fun getLecturesBySection(sectionId: String): List<Lecture> {
        val snapshot = firestore.collectionGroup("lectures")
            .whereEqualTo("sectionId", sectionId)
            .get()
            .await()

        return snapshot.toObjects(Lecture::class.java)
    }

    suspend fun addLecture(lecture: Lecture) {
        // Find the section document first
        val snapshot = firestore.collectionGroup("sections")
            .whereEqualTo("id", lecture.sectionId) // Ensure we're working with the correct section
            .get()
            .await()

        val sectionDoc = snapshot.documents.firstOrNull()

        if (sectionDoc != null) {
            // Get reference to the "lectures" subcollection and add the lecture
            sectionDoc.reference.collection("lectures")
                .document(lecture.id)
                .set(lecture)
                .await()
        } else {
            throw Exception("Section not found for sectionId: $lecture.sectionId") // Handle missing section
        }
    }


    suspend fun getLectureById(lectureId: String): Lecture? {
        val snapshot = firestore.collection("lectures")
            .document(lectureId)  // Fetching the lecture by its document ID
            .get()
            .await()

        // If the document exists, convert it to a Lecture object; otherwise, return null
        return if (snapshot.exists()) {
            snapshot.toObject(Lecture::class.java)
        } else {
            null
        }
    }


    suspend fun getResourcesBySection(sectionId: String): List<Resource> {
        val snapshot = firestore.collectionGroup("resources")
            .whereEqualTo("sectionId", sectionId)
            .get()
            .await()

        return snapshot.toObjects(Resource::class.java)
    }

    suspend fun addResource(resource: Resource) {
        // Find the section document first
        val snapshot = firestore.collectionGroup("sections")
            .whereEqualTo("id", resource.sectionId) // Ensure we're working with the correct section
            .get()
            .await()

        val sectionDoc = snapshot.documents.firstOrNull()

        if (sectionDoc != null) {
            // Get reference to the "resources" subcollection and add the resource
            sectionDoc.reference.collection("resources")
                .document(resource.id)
                .set(resource)
                .await()
        } else {
            throw Exception("Section not found for sectionId: $resource.sectionId") // Handle missing section
        }
    }


    suspend fun getQuestionsByQuiz(quizId: String): List<Question> {
        val snapshot = firestore.collectionGroup("questions")
            .whereEqualTo("quizId", quizId)
            .get()
            .await()

        return snapshot.toObjects(Question::class.java)
    }

    suspend fun addQuestion(question: Question) {
        // Find the quiz document first
        val snapshot = firestore.collectionGroup("quizzes")
            .whereEqualTo("id", question.quizId) // Ensure we're working with the correct quiz
            .get()
            .await()

        val quizDoc = snapshot.documents.firstOrNull()

        if (quizDoc != null) {
            // Get reference to the "questions" subcollection and add the question
            quizDoc.reference.collection("questions")
                .document(question.id)
                .set(question)
                .await()
        } else {
            throw Exception("Quiz not found for quizId: $question.quizId") // Handle missing quiz
        }
    }


    suspend fun getAnswersByQuestion(questionId: String): List<Answer> {
        val snapshot = firestore.collectionGroup("answers")
            .whereEqualTo("questionId", questionId)
            .get()
            .await()

        return snapshot.toObjects(Answer::class.java)
    }

    suspend fun addAnswer(answer: Answer) {
        // Find the question document first
        val snapshot = firestore.collectionGroup("questions")
            .whereEqualTo("id", answer.questionId) // Ensure we're working with the correct question
            .get()
            .await()

        val questionDoc = snapshot.documents.firstOrNull()

        if (questionDoc != null) {
            // Get reference to the "answers" subcollection and add the answer
            questionDoc.reference.collection("answers")
                .document(answer.id)
                .set(answer)
                .await()
        } else {
            throw Exception("Question not found for questionId: $answer.questionId") // Handle missing question
        }
    }


    suspend fun getMessages(courseId: String): List<ChatMessage> {
        val snapshot = firestore.collectionGroup("chat_messages")
            .whereEqualTo("courseId", courseId)
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .get()
            .await()

        return snapshot.documents.mapNotNull { it.toObject(ChatMessage::class.java) }
    }


    fun sendMessage(courseId: String, message: ChatMessage, onComplete: (Boolean) -> Unit) {
        // Log the start of the message sending process
        Log.d(
            "SendMessage",
            "Attempting to send message to courseId: $courseId with message id: ${message.id}"
        )

        firestore.collectionGroup("courses") // ✅ Find the correct course
            .whereEqualTo("id", courseId) // ✅ Ensure the right course
            .get()
            .addOnSuccessListener { snapshot ->
                val courseDoc = snapshot.documents.firstOrNull()

                // Log if the course was found
                if (courseDoc != null) {
                    Log.d("SendMessage", "Course found, uploading message...")

                    courseDoc.reference.collection("chat_messages") // ✅ Navigate to chat_messages
                        .document(message.id)
                        .set(message)
                        .addOnSuccessListener {
                            Log.d("SendMessage", "Message uploaded successfully to Firebase!")
                            onComplete(true) // Message uploaded successfully
                        }
                        .addOnFailureListener { exception ->
                            Log.e("SendMessage", "Error uploading message: ${exception.message}")
                            onComplete(false) // Upload failed
                        }
                } else {
                    Log.e("SendMessage", "Course not found for courseId: $courseId")
                    onComplete(false) // ❌ Course not found
                }
            }
            .addOnFailureListener { exception ->
                Log.e("SendMessage", "Error querying course: ${exception.message}")
                onComplete(false) // Query failed
            }
    }

    fun listenForMessages(
        courseId: String,
        onMessageChange: (ChatMessage, DocumentChange.Type) -> Unit
    ) {
        firestore.collectionGroup("courses")
            .whereEqualTo("id", courseId)
            .get()
            .addOnSuccessListener { snapshot ->
                val courseDoc = snapshot.documents.firstOrNull()
                if (courseDoc != null) {
                    courseDoc.reference.collection("chat_messages")
                        .orderBy("timestamp", Query.Direction.ASCENDING)
                        .addSnapshotListener { snapshot, _ ->
                            if (snapshot != null) {
                                snapshot.documentChanges.forEach { change ->
                                    val message = change.document.toObject(ChatMessage::class.java)
                                    onMessageChange(
                                        message,
                                        change.type
                                    ) // ✅ Callback instead of directly modifying Room
                                }
                            }
                        }
                }
            }
    }


}
