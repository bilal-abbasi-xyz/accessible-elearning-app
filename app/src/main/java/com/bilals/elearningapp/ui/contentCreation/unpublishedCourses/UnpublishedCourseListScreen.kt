package com.bilals.elearningapp.ui.contentCreation.unpublishedCourses

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.bilals.elearningapp.data.model.Course
import com.bilals.elearningapp.serviceLocator.AppContainer
import com.bilals.elearningapp.navigation.ScreenRoutes
import com.bilals.elearningapp.tts.SpeechService
import com.bilals.elearningapp.ui.contentCreation.browsing.categoryList.gradientBackground
import com.bilals.elearningapp.ui.uiComponents.AppBar
import com.bilals.elearningapp.ui.uiComponents.AppCard
import com.bilals.elearningapp.ui.theme.AppTypography


@Composable
fun UnpublishedCourseListScreen(
    navController: NavController,
    appContainer: AppContainer
) {

    LaunchedEffect(Unit) {
        // Collect categories and sync courses for each category
        appContainer.categoryRepository.allCategories.collect { categories ->
            categories.forEach { category ->
                // Call syncCourses for each category
                appContainer.courseRepository.syncCourses(categoryId = category.id)
            }
        }
    }
    val context = LocalContext.current

    // Fetch all courses using appContainer
    val allCourses by appContainer.courseRepository.getAllCourses().collectAsState(initial = emptyList())
    Log.d("AllCourses", allCourses.joinToString { it.toString() })

    // Filter courses where isPublished is false
    val unpublishedCourses = allCourses.filter { !it.isPublished!! }

    LaunchedEffect(unpublishedCourses) {
        if (unpublishedCourses.isNotEmpty()) {
            SpeechService.announce(context, "List of unpublished courses. ${unpublishedCourses.size} items available.")
        }
    }

    Log.d("unpublishedCoursesList", unpublishedCourses.toString())

    Box(modifier = Modifier
        .fillMaxSize()
        .background(gradientBackground())) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 60.dp)
                .verticalScroll(rememberScrollState()), // Enable scrolling
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AppBar(title = "Courses Available for Creation") { navController.popBackStack() }
            Spacer(modifier = Modifier.height(24.dp))

            // Display only unpublished courses
            unpublishedCourses.filter { !it.isPublished!! }.forEach { course ->
                CourseCard(course = course, navController = navController)
            }
        }

        // Bottom Navigation Bar
//        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
//            BottomNavBar(navController)
//        }
    }
}



@Composable
fun CourseCard(course: Course, navController: NavController) {
    AppCard(onClick = {
        navController.navigate(ScreenRoutes.CreateSection.createRoute(course.id, course.name))
    }) {  //  Using AppCard with course's onClick navigation
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Display course name (no icon needed)
            Text(
                text = course.name,
                style = AppTypography.bodyLarge,
                color = Color.White,
                modifier = Modifier.weight(1f)
            )
            // Optional: Add an arrow or any other visual cue for navigation
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Go to ${course.name}",
                tint = Color.White
            )
        }
    }
}

