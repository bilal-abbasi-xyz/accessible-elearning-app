package com.bilals.elearningapp.ui.contentCreation.browsing.categoryList

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.bilals.elearningapp.data.local.ElearningDatabase
import com.bilals.elearningapp.data.repository.CourseCategoryRepository
import com.bilals.elearningapp.di.AppContainer
import com.bilals.elearningapp.navigation.ScreenRoutes
import com.bilals.elearningapp.tts.SpeechService
import com.bilals.elearningapp.ui.theme.AppTypography
import com.bilals.elearningapp.ui.uiComponents.AppBar
import com.bilals.elearningapp.ui.uiComponents.AppCard
import com.bilals.elearningapp.ui.uiComponents.BottomNavBar


@Composable
fun CategoryListScreen(
    navController: NavController,
    appContainer: AppContainer
) {
    val context = LocalContext.current

//    val database = ElearningDatabase.getDatabase(context)
//
//
//    // Get the DAO from the database instance
//    val courseCategoryDao = remember { database.courseCategoryDao() }
//
//    val repository = remember { CourseCategoryRepository(courseCategoryDao, context) }
    val viewModel = remember { CategoryListViewModel(appContainer.categoryRepository, navController) }

    val categories by viewModel.courseCategories.collectAsState()

    LaunchedEffect(Unit) {
        SpeechService.announce(
            context,
            "Category List Screen. Pick a category to view coursss."
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBackground())
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 60.dp)
                .verticalScroll(rememberScrollState()), // Enable scrolling
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AppBar(title = "Course Categories") { navController.popBackStack() }
            Spacer(modifier = Modifier.height(24.dp))

            categories.forEach { category ->
                CategoryCard(category.name, Icons.AutoMirrored.Filled.MenuBook) {
                    navController.navigate(
                        ScreenRoutes.CourseList.createRoute(
                            category.id,
                            category.name
                        )
                    )
                }
            }
        }
        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            BottomNavBar(navController)
        }
    }
}


@Composable
fun gradientBackground() = Brush.verticalGradient(listOf(Color(0xFFEAEAEA), Color(0xFFCCCCCC)))

@Composable
fun CategoryCard(category: String, icon: ImageVector, onClick: () -> Unit) {
    AppCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .semantics { contentDescription = "" } // ⛔ Prevents TalkBack from reading automatically

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = category,
                style = AppTypography.bodyLarge,
                color = Color.White,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "",
                tint = Color.White
            )
        }
    }
}


