package com.bilals.elearningapp.navigation

import android.net.Uri

sealed class ScreenRoutes(val route: String) {


    object WelcomeScreen : ScreenRoutes("welcomeScreen")
    object Home : ScreenRoutes("home")
    object Report : ScreenRoutes("report")
    object CategoryList : ScreenRoutes("categoryList")
    object CourseList : ScreenRoutes("courseList/{categoryId}/{categoryName}") {
        fun createRoute(categoryId: String, categoryName: String) =
            "courseList/$categoryId/$categoryName"
    }

    object CourseDetail : ScreenRoutes("courseDetail/{courseId}/{courseName}") {
        fun createRoute(courseId: String, courseName: String) = "courseDetail/$courseId/$courseName"
    }

    object SectionDetail : ScreenRoutes("sectionDetail/{sectionId}/{sectionName}") {
        fun createRoute(sectionId: String, sectionName: String) =
            "sectionDetail/$sectionId/$sectionName"
    }

    object ViewLecture : ScreenRoutes("viewLecture/{lectureId}/{lectureName}") {
        fun createRoute(lectureId: String, lectureName: String) =
            "viewLecture/$lectureId/$lectureName"
    }

    object VideoScreen : ScreenRoutes("videoScreen?videoUrl={videoUrl}") {
        fun createRoute(videoUrl: String) =
            "videoScreen?videoUrl=${Uri.encode(videoUrl)}"
    }
    object AttemptQuiz : ScreenRoutes("attemptQuiz/{quizId}/{quizName}") {
        fun createRoute(quizId: String, quizName: String) = "attemptQuiz/$quizId/$quizName"
    }

    object ViewResource : ScreenRoutes("viewResource/{resourceId}/{resourceName}") {
        fun createRoute(resourceId: String, resourceName: String) =
            "viewResource/$resourceId/$resourceName"
    }


    object CourseForum : ScreenRoutes("courseForum/{courseId}/{courseName}") {
        fun createRoute(courseId: String, courseName: String) = "courseForum/$courseId/$courseName"
    }
    object PublicForum : ScreenRoutes("publicForum")

    object Settings : ScreenRoutes("settings")
    object Training : ScreenRoutes("training")
    object VoiceSettings : ScreenRoutes("voiceSettings")
    object ProfileSettings : ScreenRoutes("profileSettings")
    object UISettings : ScreenRoutes("uiSettings")
    object Login : ScreenRoutes("login")
    object SignUp : ScreenRoutes("signUp")
    object InstructorHome : ScreenRoutes("instructorHome")
    object CourseCreation : ScreenRoutes("courseCreation")
    object SectionCreation : ScreenRoutes("sectionCreation")
    object UnpublishedCourseList : ScreenRoutes("unpublishedCourseList")
    object CreateSection : ScreenRoutes("createSection/{courseId}/{courseName}") {
        fun createRoute(courseId: String, courseName: String) =
            "createSection/$courseId/$courseName"
    }

    object CreateSectionContent : ScreenRoutes("createSectionContent/{sectionId}/{sectionName}") {
        fun createRoute(sectionId: String, sectionName: String) =
            "createSectionContent/$sectionId/$sectionName"
    }

    object CreateQuiz : ScreenRoutes("createQuiz/{quizId}/{quizName}") {
        fun createRoute(quizId: String, quizName: String) = "createQuiz/$quizId/$quizName"
    }


    object CreateLecture : ScreenRoutes("createLecture/{lectureId}/{lectureName}/{sectionId}") {
        fun createRoute(lectureId: String, lectureName: String, sectionId: String) = "createLecture/$lectureId/$lectureName/$sectionId"
    }

}
