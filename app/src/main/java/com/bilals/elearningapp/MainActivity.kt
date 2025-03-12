package com.bilals.elearningapp

import CreateSectionScreen
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bilals.elearningapp.data.model.user.RoleType
import com.bilals.elearningapp.data.repository.AuthRepository
import com.bilals.elearningapp.di.AppContainer
import com.bilals.elearningapp.navigation.ScreenRoutes
import com.bilals.elearningapp.stt.STTManager
import com.bilals.elearningapp.stt.SpeechInputHandler
import com.bilals.elearningapp.stt.VoiceCommandProcessor
import com.bilals.elearningapp.tts.TTSManager
import com.bilals.elearningapp.ui.auth.SessionManager
import com.bilals.elearningapp.ui.auth.login.LogInScreen
import com.bilals.elearningapp.ui.auth.signup.SignUpScreen
import com.bilals.elearningapp.ui.auth.signup.SignUpViewModel
import com.bilals.elearningapp.ui.categoryList.CategoryListScreen
import com.bilals.elearningapp.ui.courseDetail.CourseDetailScreen
import com.bilals.elearningapp.ui.courseForum.CourseForumScreen
import com.bilals.elearningapp.ui.courseList.CourseListScreen
import com.bilals.elearningapp.ui.createSectionScreen.CreateSectionContentScreen
import com.bilals.elearningapp.ui.home.HomeScreen
import com.bilals.elearningapp.ui.instructor.InstructorHomeScreen
import com.bilals.elearningapp.ui.lecture.ViewLectureScreen
import com.bilals.elearningapp.ui.profileSettings.ProfileSettingsScreen
import com.bilals.elearningapp.ui.quiz.AttemptQuizScreen
import com.bilals.elearningapp.ui.resource.ViewResourceScreen
import com.bilals.elearningapp.ui.sectionDetail.SectionDetailScreen
import com.bilals.elearningapp.ui.settings.SettingsScreen
import com.bilals.elearningapp.ui.settings.VoiceSettingsScreen
import com.bilals.elearningapp.ui.theme.AppTheme
import com.bilals.elearningapp.ui.uiSettings.UISettingsScreen
import com.bilals.elearningapp.ui.unpublishedCourses.UnpublishedCourseListScreen
import com.bilals.elearningapp.ui.welcomeScreen.WelcomeScreen
import com.google.firebase.auth.FirebaseAuth
import kotlin.math.absoluteValue

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                showToast("Permission Granted!")
            } else {
                showToast("Permission Denied!")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val firebaseAuth = FirebaseAuth.getInstance()

        val authRepository = AuthRepository(firebaseAuth)

        val signUpViewModel = SignUpViewModel(authRepository)

        requestMicrophonePermission()

        initializeManagers()

        setContent {
            AppTheme {
                AppContent(context = this)
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun requestMicrophonePermission() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        } else {
            showToast("Permission already granted.")
        }
    }

    private fun initializeManagers() {
        TTSManager.initialize(this)
        STTManager.initialize(this)
    }

    override fun onDestroy() {
        super.onDestroy()

        TTSManager.shutdown()
        STTManager.shutdown()
    }
}

@Composable
fun AppContent(context: Context) {
    val context = LocalContext.current

    val appContainer = AppContainer(context) // Initialize DI container

    val navController = rememberNavController()
    val commandProcessor = VoiceCommandProcessor(
        navController, appContainer.categoryRepository, appContainer.courseRepository,
        appContainer.sectionRepository, appContainer.quizRepository, appContainer.lectureRepository,
        appContainer.resourceRepository
    )

    val recognizedText = remember { mutableStateOf("") }
    val speechInputHandler =
        remember { SpeechInputHandler(context, navController, recognizedText, commandProcessor) }
    HandleSwipeGestures(speechInputHandler)
    RecognizedTextDisplay(recognizedText, context)
    SessionManager.saveActiveRole(RoleType.STUDENT, context)
    AppNavHost(navController, appContainer)
}

@Composable
fun HandleSwipeGestures(speechInputHandler: SpeechInputHandler) {

    Box(modifier = Modifier
        .fillMaxSize()
        .pointerInput(Unit) {
            detectHorizontalDragGestures { change, dragAmount ->

                if (dragAmount.absoluteValue > 50 && !speechInputHandler.isListening) {
                    speechInputHandler.startListening()
                }
            }
        })
}

@Composable
fun RecognizedTextDisplay(recognizedText: MutableState<String>, context: Context) {

    Spacer(modifier = Modifier.height(16.dp))

    LaunchedEffect(recognizedText.value) {
        if (recognizedText.value.isNotEmpty()) {
            val briefText = recognizedText.value.split(" ").take(3).joinToString(" ")
            Toast.makeText(context, "You said: $briefText", Toast.LENGTH_SHORT).show()
        }
    }

//    Text(
//        text = if (recognizedText.value.isNotEmpty()) {
//            recognizedText.value
//        } else {
//            "Say something..."
//        },
//        fontSize = 24.sp,
//        fontWeight = FontWeight.Normal,
//    )
}

@Composable
fun AppNavHost(navController: NavHostController, appContainer: AppContainer) {

    NavHost(
        navController = navController, startDestination = ScreenRoutes.WelcomeScreen.route
    ) {
        composable(ScreenRoutes.WelcomeScreen.route) {
            WelcomeScreen(navController = navController)
        }
        composable(ScreenRoutes.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(ScreenRoutes.CategoryList.route) {
            CategoryListScreen(navController = navController, appContainer)
        }
        composable(ScreenRoutes.CourseList.route) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId") ?: ""
            val categoryName = backStackEntry.arguments?.getString("categoryName") ?: ""

            CourseListScreen(
                navController = navController,
                categoryId = categoryId,
                categoryName = categoryName,
                appContainer
            )
        }
        composable(ScreenRoutes.CourseDetail.route) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getString("courseId") ?: ""
            val courseName = backStackEntry.arguments?.getString("courseName") ?: ""
            CourseDetailScreen(
                navController = navController,
                courseId = courseId,
                courseName = courseName,
                appContainer
            )
        }
        composable(ScreenRoutes.CourseForum.route) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getString("courseId") ?: ""
            val courseName = backStackEntry.arguments?.getString("courseName") ?: ""
            CourseForumScreen(
                navController = navController,
                courseId = courseId,
                courseName = courseName,
                appContainer
            )
        }
        composable(ScreenRoutes.SectionDetail.route) { backStackEntry ->
            val sectionId = backStackEntry.arguments?.getString("sectionId") ?: ""
            val sectionName = backStackEntry.arguments?.getString("sectionName") ?: ""
            SectionDetailScreen(
                navController = navController,
                sectionId = sectionId,
                sectionName = sectionName,
                appContainer
            )
        }

        composable(ScreenRoutes.ViewLecture.route) { backStackEntry ->
            val lectureId = backStackEntry.arguments?.getString("lectureId") ?: ""
            val lectureName = backStackEntry.arguments?.getString("lectureName") ?: ""
            ViewLectureScreen(
                navController = navController, lectureId = lectureId, lectureName = lectureName
            )
        }

        composable(ScreenRoutes.AttemptQuiz.route) { backStackEntry ->
            val quizId = backStackEntry.arguments?.getString("quizId") ?: ""
            val quizName = backStackEntry.arguments?.getString("quizName") ?: ""
            AttemptQuizScreen(
                navController = navController, quizId = quizId, quizName = quizName
            )
        }

        composable(ScreenRoutes.ViewResource.route) { backStackEntry ->
            val resourceId = backStackEntry.arguments?.getString("resourceId") ?: ""
            val resourceName = backStackEntry.arguments?.getString("resourceName") ?: ""
            ViewResourceScreen(
                navController = navController, resourceId = resourceId, resourceName = resourceName
            )
        }

        composable(ScreenRoutes.Settings.route) {
            SettingsScreen(navController = navController)
        }
        composable(ScreenRoutes.ProfileSettings.route) {
            ProfileSettingsScreen(navController = navController)
        }
        composable(ScreenRoutes.UISettings.route) {
            UISettingsScreen(navController = navController)
        }
        composable(ScreenRoutes.VoiceSettings.route) {
            VoiceSettingsScreen(navController = navController)
        }
        composable(ScreenRoutes.Login.route) {
            LogInScreen(navController = navController)
        }
        composable(ScreenRoutes.SignUp.route) {
            SignUpScreen(navController = navController)
        }
        composable(ScreenRoutes.InstructorHome.route) {
            InstructorHomeScreen(navController = navController)
        }
        composable(ScreenRoutes.UnpublishedCourseList.route) {
            UnpublishedCourseListScreen(navController = navController, appContainer)

        }
        composable(ScreenRoutes.CreateSection.route) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getString("courseId") ?: ""
            val courseName = backStackEntry.arguments?.getString("courseName") ?: ""
            CreateSectionScreen(
                navController = navController,
                courseId = courseId,
                courseName = courseName,
                appContainer
            )
        }

        composable(ScreenRoutes.CreateSectionContent.route) { backStackEntry ->
            val sectionId = backStackEntry.arguments?.getString("sectionId") ?: ""
            val sectionName = backStackEntry.arguments?.getString("sectionName") ?: ""
            CreateSectionContentScreen(
                navController = navController,
                sectionId = sectionId,
                sectionName = sectionName,
                appContainer
            )
        }
    }
}