package com.bilals.elearningapp

import android.content.Context
import android.util.Log
import com.bilals.elearningapp.data.local.ElearningDatabase
import com.bilals.elearningapp.data.remote.FirebaseServiceSingleton
import kotlinx.coroutines.flow.first

class DatabaseSyncManager(context: Context) {
    // Initialize database with the provided context
    private val database = ElearningDatabase.getDatabase(context)
    private val categoryDao = database.courseCategoryDao()
    private val courseDao = database.courseDao()
    private val sectionDao = database.sectionDao()
    private val lectureDao = database.lectureDao()
    private val resourceDao = database.resourceDao()
    private val quizDao = database.quizDao()
    private val questionDao = database.questionDao()
    private val answerDao = database.answerDao()
    private val chatMessageDao = database.chatMessageDao()
    private val publicChatMessageDao = database.publicChatMessageDao()

    // Firebase service
    private val firebaseService = FirebaseServiceSingleton.instance


    suspend fun updateDatabaseFromFirebase(

    ) {
        syncCategories()

        val firebaseCategories = firebaseService.getAllCategories()
        for (category in firebaseCategories) {
            syncCourses(category.id)

            val firebaseCourses = firebaseService.getCoursesByCategory(category.id)
            for (course in firebaseCourses) {
                syncSections(course.id)
                syncChatMessages(course.id)

                val firebaseSections = firebaseService.getSectionsByCourse(course.id)
                for (section in firebaseSections) {
                    syncQuizzes(section.id)
                    syncLectures(section.id)
                    syncResources(section.id)

                    val firebaseQuizzes = firebaseService.getQuizzesBySection(section.id)
                    for (quiz in firebaseQuizzes) {
                        syncQuestions(quiz.id)

                        val firebaseQuestions = firebaseService.getQuestionsByQuiz(quiz.id)
                        for (question in firebaseQuestions) {
                            syncAnswers(question.id)
                        }
                    }
                }
            }
        }
    }


    suspend fun syncCategories(
    ) {
        // Fetch categories from Firebase
        val firebaseCategories = firebaseService.getAllCategories()
        Log.d(
            "DatabaseSync",
            "Fetched ${firebaseCategories.size} categories from Firebase: $firebaseCategories"
        )

        // Fetch categories from the local Room database
        val localCategories = categoryDao.getAllCategories().first()
        Log.d(
            "DatabaseSync",
            "Fetched ${localCategories.size} categories from Room: $localCategories"
        )

        // Filter categories to insert or update (new or changed)
        val categoriesToInsertOrUpdate = firebaseCategories.filter { firebaseCategory ->
            val localCategory = localCategories.find { it.id == firebaseCategory.id }
            val isNewOrChanged = localCategory == null || localCategory != firebaseCategory
            if (isNewOrChanged) {
                Log.d(
                    "DatabaseSync",
                    "Category ${firebaseCategory.id} (Name: ${firebaseCategory.name}) is new or changed."
                )
            }
            isNewOrChanged
        }

        // Log the categories that will be inserted or updated
        Log.d("DatabaseSync", "Categories to insert or update: ${categoriesToInsertOrUpdate.size}")

        // If there are categories to insert or update, perform the batch operation
        if (categoriesToInsertOrUpdate.isNotEmpty()) {
            categoryDao.insertCategories(categoriesToInsertOrUpdate) // Batch insert or update
            Log.d(
                "DatabaseSync",
                "Inserted/Updated ${categoriesToInsertOrUpdate.size} categories into Room."
            )
        } else {
            Log.d("DatabaseSync", "No new or changed categories to update.")
        }

        val categoryIdsInFirebase = firebaseCategories.map { it.id }
        val categoriesToDelete = localCategories.filter { it.id !in categoryIdsInFirebase }

        if (categoriesToDelete.isNotEmpty()) {
            categoryDao.deleteCategories(categoriesToDelete) //  Delete from Room
        }
    }


    suspend fun syncCourses(
        categoryId: String
    ) {
        // Fetch courses from Firebase for the given category
        val firebaseCourses = firebaseService.getCoursesByCategory(categoryId)
        Log.d(
            "DatabaseSync",
            "Fetched ${firebaseCourses.size} courses from Firebase for category $categoryId: $firebaseCourses"
        )

        // Fetch courses from the local Room database for the given category
        val localCourses = courseDao.getCoursesByCategory(categoryId).first()
        Log.d(
            "DatabaseSync",
            "Fetched ${localCourses.size} courses from Room for category $categoryId: $localCourses"
        )

        // Filter courses to insert or update (new or changed)
        val coursesToInsertOrUpdate = firebaseCourses.filter { firebaseCourse ->
            val localCourse = localCourses.find { it.id == firebaseCourse.id }
            val isNewOrChanged = localCourse == null || localCourse != firebaseCourse
            if (isNewOrChanged) {
                Log.d(
                    "DatabaseSync",
                    "Course ${firebaseCourse.id} (Name: ${firebaseCourse.name}) is new or changed for category $categoryId."
                )
            }
            isNewOrChanged
        }

        // Log the courses that will be inserted or updated
        Log.d(
            "DatabaseSync",
            "Courses to insert or update for category $categoryId: ${coursesToInsertOrUpdate.size}"
        )

        // If there are courses to insert or update, perform the batch operation
        if (coursesToInsertOrUpdate.isNotEmpty()) {
            courseDao.insertCourses(coursesToInsertOrUpdate) // Batch insert or update
            Log.d(
                "DatabaseSync",
                "Inserted/Updated ${coursesToInsertOrUpdate.size} courses into Room for category $categoryId."
            )
        } else {
            Log.d("DatabaseSync", "No new or changed courses to update for category $categoryId.")
        }

        val courseIdsInFirebase = firebaseCourses.map { it.id }
        val coursesToDelete = localCourses.filter { it.id !in courseIdsInFirebase }

        if (coursesToDelete.isNotEmpty()) {
            courseDao.deleteCourses(coursesToDelete) //  Delete from Room
        }
    }


    suspend fun syncSections(
        courseId: String
    ) {
        // Fetch sections from Firebase and local database
        val firebaseSections = firebaseService.getSectionsByCourse(courseId)
        val localSections = sectionDao.getSectionsByCourse(courseId).first()

        Log.d(
            "DatabaseSync",
            "Fetched ${firebaseSections.size} sections from Firebase for course $courseId"
        )
        Log.d("DatabaseSync", "Fetched ${localSections.size} local sections for course $courseId")

        // Identify sections to insert or update
        val sectionsToInsertOrUpdate = firebaseSections.filter { firebaseSection ->
            val localSection = localSections.find { it.id == firebaseSection.id }

            // Log comparison of each section between Firebase and local
            if (localSection == null) {
                Log.d("DatabaseSync", "Section ${firebaseSection.id} is new and will be inserted.")
            } else {
                if (localSection != firebaseSection) {
                    Log.d("DatabaseSync", "Section ${firebaseSection.id} has changes.")
                    // You can log specific changes here if needed (e.g., name, description, etc.)
                }
            }

            // Determine if the section needs to be inserted or updated
            localSection == null || localSection != firebaseSection
        }

        // Insert or update the identified sections
        if (sectionsToInsertOrUpdate.isNotEmpty()) {
            Log.d(
                "DatabaseSync",
                "Inserting/Updating ${sectionsToInsertOrUpdate.size} sections for course $courseId"
            )
            sectionDao.insertSections(sectionsToInsertOrUpdate)
        } else {
            Log.d("DatabaseSync", "No sections to update/insert for course $courseId")
        }

        val sectionIdsInFirebase = firebaseSections.map { it.id }
        val sectionsToDelete = localSections.filter { it.id !in sectionIdsInFirebase }

        if (sectionsToDelete.isNotEmpty()) {
            sectionDao.deleteSections(sectionsToDelete) //  Delete from Room
        }

    }

    suspend fun syncLecture(lectureId: String) {
        // Fetch the lecture from Firebase and local database
        val firebaseLecture = firebaseService.getLectureById(lectureId)
        val localLecture = lectureDao.getLectureById(lectureId)

        Log.d("DatabaseSync", "Fetched lecture with ID $lectureId from Firebase")
        Log.d("DatabaseSync", "Fetched local lecture with ID $lectureId")

        // If the lecture doesn't exist locally, it needs to be inserted
        if (localLecture == null) {
            Log.d("DatabaseSync", "Lecture $lectureId is new and will be inserted.")
            if (firebaseLecture != null) {
                lectureDao.insertLecture(firebaseLecture)
            }
        } else {
            // Log and update the lecture if there are differences
            var updated = false

            if (firebaseLecture != null) {
                if (localLecture.content != firebaseLecture.content) {
                    Log.d(
                        "DatabaseSync",
                        "Lecture $lectureId has content change: ${localLecture.content} -> ${firebaseLecture.content}"
                    )
                    updated = true
                }
            }
            if (firebaseLecture != null) {
                if (localLecture.name != firebaseLecture.name) {
                    Log.d(
                        "DatabaseSync",
                        "Lecture $lectureId has name change: ${localLecture.name} -> ${firebaseLecture.name}"
                    )
                    updated = true
                }
            }
            if (firebaseLecture != null) {
                if (localLecture.sectionId != firebaseLecture.sectionId) {
                    Log.d(
                        "DatabaseSync",
                        "Lecture $lectureId has sectionId change: ${localLecture.sectionId} -> ${firebaseLecture.sectionId}"
                    )
                    updated = true
                }
            }

            // Update the lecture if needed
            if (updated) {
                Log.d("DatabaseSync", "Updating lecture $lectureId")
                if (firebaseLecture != null) {
                    lectureDao.insertLecture(firebaseLecture)
                }
            } else {
                Log.d("DatabaseSync", "Lecture $lectureId is already up to date")
            }
        }

        // Check if the lecture is deleted in Firebase (exists in local but not in Firebase)
        if (firebaseLecture == null) {
            Log.d("DatabaseSync", "Lecture $lectureId exists locally but not in Firebase. Deleting it.")
            if (localLecture != null) {
                lectureDao.deleteLecture(localLecture)
            }
        }
    }


    suspend fun syncLectures(
        sectionId: String
    ) {
        val firebaseLectures = firebaseService.getLecturesBySection(sectionId)
        val localLectures = lectureDao.getLecturesBySection(sectionId).first()
        val lecturesToInsertOrUpdate = firebaseLectures.filter { firebaseLecture ->
            val localLecture = localLectures.find { it.id == firebaseLecture.id }

            // Determine if the lecture needs to be updated or inserted
            localLecture == null ||
                    localLecture.content != firebaseLecture.content ||
                    localLecture.name != firebaseLecture.name ||
                    localLecture.sectionId != firebaseLecture.sectionId
        }

        // Insert or update the identified lectures
        if (lecturesToInsertOrUpdate.isNotEmpty()) {

            lectureDao.insertLectures(lecturesToInsertOrUpdate)
        } else {
            Log.d("DatabaseSync", "No lectures to update/insert for section $sectionId")
        }

        val lectureIdsInFirebase = firebaseLectures.map { it.id }
        val lecturesToDelete = localLectures.filter { it.id !in lectureIdsInFirebase }

        if (lecturesToDelete.isNotEmpty()) {
            lectureDao.deleteLectures(lecturesToDelete) //  Delete from Room
        }

    }


    suspend fun syncResources(
        sectionId: String
    ) {
        // Fetch resources from Firebase and local database
        val firebaseResources = firebaseService.getResourcesBySection(sectionId)
        val localResources = resourceDao.getResourcesBySection(sectionId).first()

        Log.d(
            "DatabaseSync",
            "Fetched ${firebaseResources.size} resources from Firebase for section $sectionId"
        )
        Log.d(
            "DatabaseSync",
            "Fetched ${localResources.size} local resources for section $sectionId"
        )

        // Identify resources to insert or update
        val resourcesToInsertOrUpdate = firebaseResources.filter { firebaseResource ->
            val localResource = localResources.find { it.id == firebaseResource.id }

            // Log the comparison of the resource between Firebase and local
            if (localResource == null) {
                Log.d(
                    "DatabaseSync",
                    "Resource ${firebaseResource.id} is new and will be inserted."
                )
            } else {
                if (localResource.name != firebaseResource.name) {
                    Log.d(
                        "DatabaseSync",
                        "Resource ${firebaseResource.id} has a name change: ${localResource.name} -> ${firebaseResource.name}"
                    )
                }
                if (localResource.sectionId != firebaseResource.sectionId) {
                    Log.d(
                        "DatabaseSync",
                        "Resource ${firebaseResource.id} has a sectionId change: ${localResource.sectionId} -> ${firebaseResource.sectionId}"
                    )
                }
                if (localResource.filePath != firebaseResource.filePath) {
                    Log.d(
                        "DatabaseSync",
                        "Resource ${firebaseResource.id} has a filePath change: ${localResource.filePath} -> ${firebaseResource.filePath}"
                    )
                }
            }

            // Determine if the resource needs to be updated or inserted
            localResource == null ||
                    localResource.name != firebaseResource.name ||
                    localResource.sectionId != firebaseResource.sectionId ||
                    localResource.filePath != firebaseResource.filePath
        }

        // Insert or update the identified resources
        if (resourcesToInsertOrUpdate.isNotEmpty()) {
            Log.d(
                "DatabaseSync",
                "Inserting/Updating ${resourcesToInsertOrUpdate.size} resources for section $sectionId"
            )
            resourceDao.insertResources(resourcesToInsertOrUpdate)
        } else {
            Log.d("DatabaseSync", "No resources to update/insert for section $sectionId")
        }

        val resourceIdsInFirebase = firebaseResources.map { it.id }
        val resourcesToDelete = localResources.filter { it.id !in resourceIdsInFirebase }

        if (resourcesToDelete.isNotEmpty()) {
            resourceDao.deleteResources(resourcesToDelete) //  Delete from Room
        }
    }


    suspend fun syncQuizzes(
        sectionId: String
    ) {
        // Fetch quizzes from Firebase for the given section
        val firebaseQuizzes = firebaseService.getQuizzesBySection(sectionId)
        Log.d(
            "DatabaseSync",
            "Fetched ${firebaseQuizzes.size} quizzes from Firebase for section $sectionId: $firebaseQuizzes"
        )

        // Fetch quizzes from the local Room database for the given section
        val localQuizzes = quizDao.getQuizzesBySection(sectionId).first()
        Log.d(
            "DatabaseSync",
            "Fetched ${localQuizzes.size} quizzes from Room for section $sectionId: $localQuizzes"
        )

        // Filter quizzes to insert or update (new or changed)
        val quizzesToInsertOrUpdate = firebaseQuizzes.filter { firebaseQuiz ->
            val localQuiz = localQuizzes.find { it.id == firebaseQuiz.id }
            val isNewOrChanged = localQuiz == null || localQuiz != firebaseQuiz
            if (isNewOrChanged) {
                Log.d(
                    "DatabaseSync",
                    "Quiz ${firebaseQuiz.id} (Name: ${firebaseQuiz.name}) is new or changed for section $sectionId."
                )
            }
            isNewOrChanged
        }

        // Log the quizzes that will be inserted or updated
        Log.d(
            "DatabaseSync",
            "Quizzes to insert or update for section $sectionId: ${quizzesToInsertOrUpdate.size}"
        )

        // If there are quizzes to insert or update, perform the batch operation
        if (quizzesToInsertOrUpdate.isNotEmpty()) {
            quizDao.insertQuizzes(quizzesToInsertOrUpdate) // Batch insert or update
            Log.d(
                "DatabaseSync",
                "Inserted/Updated ${quizzesToInsertOrUpdate.size} quizzes into Room for section $sectionId."
            )
        } else {
            Log.d("DatabaseSync", "No new or changed quizzes to update for section $sectionId.")
        }

        val quizIdsInFirebase = firebaseQuizzes.map { it.id }
        val quizzesToDelete = localQuizzes.filter { it.id !in quizIdsInFirebase }

        if (quizzesToDelete.isNotEmpty()) {
            quizDao.deleteQuizzes(quizzesToDelete) //  Delete from Room
        }
    }

    suspend fun syncQuestions(
        quizId: String
    ) {
        // Fetch questions from Firebase for the given quiz
        val firebaseQuestions = firebaseService.getQuestionsByQuiz(quizId)
        Log.d(
            "DatabaseSync",
            "Fetched ${firebaseQuestions.size} questions from Firebase for quiz $quizId: $firebaseQuestions"
        )

        // Fetch questions from the local Room database for the given quiz
        val localQuestions = questionDao.getQuestionsByQuiz(quizId).first()
        Log.d(
            "DatabaseSync",
            "Fetched ${localQuestions.size} questions from Room for quiz $quizId: $localQuestions"
        )

        // Filter questions to insert or update (new or changed)
        val questionsToInsertOrUpdate = firebaseQuestions.filter { firebaseQuestion ->
            val localQuestion = localQuestions.find { it.id == firebaseQuestion.id }
            val isNewOrChanged = localQuestion == null || localQuestion != firebaseQuestion
            if (isNewOrChanged) {
                Log.d(
                    "DatabaseSync",
                    "Question ${firebaseQuestion.id} (Text: ${firebaseQuestion.text}) is new or changed for quiz $quizId."
                )
            }
            isNewOrChanged
        }

        // Log the questions that will be inserted or updated
        Log.d(
            "DatabaseSync",
            "Questions to insert or update for quiz $quizId: ${questionsToInsertOrUpdate.size}"
        )

        // If there are questions to insert or update, perform the batch operation
        if (questionsToInsertOrUpdate.isNotEmpty()) {
            questionDao.insertQuestions(questionsToInsertOrUpdate) // Batch insert or update
            Log.d(
                "DatabaseSync",
                "Inserted/Updated ${questionsToInsertOrUpdate.size} questions into Room for quiz $quizId."
            )
        } else {
            Log.d("DatabaseSync", "No new or changed questions to update for quiz $quizId.")
        }

        val questionIdsInFirebase = firebaseQuestions.map { it.id }
        val questionsToDelete = localQuestions.filter { it.id !in questionIdsInFirebase }


        if (questionsToDelete.isNotEmpty()) {
            questionDao.deleteQuestions(questionsToDelete) //  Delete from Room
        }
    }

    suspend fun syncAnswers(
        questionId: String
    ) {
        // Fetch answers from Firebase for the given question
        val firebaseAnswers = firebaseService.getAnswersByQuestion(questionId)
        Log.d(
            "DatabaseSync",
            "Fetched ${firebaseAnswers.size} answers from Firebase for question $questionId: $firebaseAnswers"
        )

        // Fetch answers from the local Room database for the given question
        val localAnswers = answerDao.getAnswersByQuestion(questionId).first()
        Log.d(
            "DatabaseSync",
            "Fetched ${localAnswers.size} answers from Room for question $questionId: $localAnswers"
        )

        // Filter answers to insert or update (new or changed)
        val answersToInsertOrUpdate = firebaseAnswers.filter { firebaseAnswer ->
            val localAnswer = localAnswers.find { it.id == firebaseAnswer.id }
            val isNewOrChanged = localAnswer == null || localAnswer != firebaseAnswer
            if (isNewOrChanged) {
                Log.d(
                    "DatabaseSync",
                    "Answer ${firebaseAnswer.id} (Text: ${firebaseAnswer.text}) is new or changed for question $questionId."
                )
            }
            isNewOrChanged
        }

        // Log the answers that will be inserted or updated
        Log.d(
            "DatabaseSync",
            "Answers to insert or update for question $questionId: ${answersToInsertOrUpdate.size}"
        )

        // If there are answers to insert or update, perform the batch operation
        if (answersToInsertOrUpdate.isNotEmpty()) {
            answerDao.insertAnswers(answersToInsertOrUpdate) // Batch insert or update
            Log.d(
                "DatabaseSync",
                "Inserted/Updated ${answersToInsertOrUpdate.size} answers into Room for question $questionId."
            )
        } else {
            Log.d("DatabaseSync", "No new or changed answers to update for question $questionId.")
        }

        val answerIdsInFirebase = firebaseAnswers.map { it.id }
        val answersToDelete = localAnswers.filter { it.id !in answerIdsInFirebase }

        if (answersToDelete.isNotEmpty()) {
            answerDao.deleteAnswers(answersToDelete) //  Delete from Room
        }
    }


    suspend fun syncChatMessages(courseId: String) {
        val firebaseMessages = firebaseService.getMessages(courseId) //  Fetch from Firebase
        val localMessages = chatMessageDao.getMessages(courseId).first() //  Fetch from Room

        // Find messages that need to be inserted or updated
        val messagesToInsertOrUpdate = firebaseMessages.filter { firebaseMessage ->
            val localMessage = localMessages.find { it.id == firebaseMessage.id }
            localMessage == null || localMessage != firebaseMessage
        }

        // Insert or update messages in the local database
        if (messagesToInsertOrUpdate.isNotEmpty()) {
            chatMessageDao.insertMessages(messagesToInsertOrUpdate) //  Insert new/updated messages
        }

        // Find messages that are locally stored but not present in Firebase (these should be deleted)
        val messagesToDelete = localMessages.filter { localMessage ->
            firebaseMessages.none { it.id == localMessage.id }
        }

        // Delete messages from the local database that are not in Firebase
        if (messagesToDelete.isNotEmpty()) {
            // Get the list of ids of the messages to be deleted
            val messageIdsToDelete = messagesToDelete.map { it.id }
            chatMessageDao.deleteMessages(messageIdsToDelete) //  Remove deleted messages from Room
        }
    }

    suspend fun syncPublicChatMessages() {
        val firebaseMessages = firebaseService.getPublicMessages() //  Fetch from Firebase
        val localMessages = publicChatMessageDao.getMessages().first() //  Fetch from Room

        // Find messages that need to be inserted or updated
        val messagesToInsertOrUpdate = firebaseMessages.filter { firebaseMessage ->
            val localMessage = localMessages.find { it.id == firebaseMessage.id }
            localMessage == null || localMessage != firebaseMessage
        }

        // Insert or update messages in the local database
        if (messagesToInsertOrUpdate.isNotEmpty()) {
            publicChatMessageDao.insertMessages(messagesToInsertOrUpdate) //  Insert new/updated messages
        }

        // Find messages that are locally stored but not present in Firebase (delete them)
        val messagesToDelete = localMessages.filter { localMessage ->
            firebaseMessages.none { it.id == localMessage.id }
        }

        // Delete messages from the local database that are not in Firebase
        if (messagesToDelete.isNotEmpty()) {
            val messageIdsToDelete = messagesToDelete.map { it.id }
            publicChatMessageDao.deleteMessages(messageIdsToDelete) //  Remove deleted messages from Room
        }
    }

}