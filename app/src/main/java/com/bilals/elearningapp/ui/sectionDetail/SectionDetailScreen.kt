package com.bilals.elearningapp.ui.sectionDetail

//import com.bilals.elearningapp.navigation.NavDataManager
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bilals.elearningapp.data.local.ElearningDatabase
import com.bilals.elearningapp.di.AppContainer
import com.bilals.elearningapp.navigation.ScreenRoutes
import com.bilals.elearningapp.ui.categoryList.gradientBackground
import com.bilals.elearningapp.ui.uiComponents.AppBar
import com.bilals.elearningapp.ui.uiComponents.AppCard
import com.bilals.elearningapp.ui.uiComponents.BottomNavBar
import com.bilals.elearningapp.ui.uiComponents.SectionHeading

@Composable
fun SectionDetailScreen(
    navController: NavController,
    sectionId: String,
    sectionName: String,
    appContainer: AppContainer
) {
    val context = LocalContext.current
    val database = ElearningDatabase.getDatabase(context)

//    val lectureDao = remember { database.lectureDao() }
//    val lectureRepo = remember { LectureRepository(lectureDao, context) }
//
//    val quizDao = remember { database.quizDao() }
//    val quizRepo = remember { QuizRepository(quizDao, context) }
//
//    val resourceDao = remember { database.resourceDao() }
//    val resourceRepo = remember { ResourceRepository(resourceDao, context) }

    val viewModel =
        remember {
            SectionDetailViewModel(
                appContainer.lectureRepository,
                appContainer.quizRepository,
                appContainer.resourceRepository,
                sectionId
            )
        }

    val lectures by viewModel.lectures.collectAsState()
    val quizzes by viewModel.quizzes.collectAsState()
    val resources by viewModel.resources.collectAsState()

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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // AppBar
            AppBar(title = "$sectionName") { navController.popBackStack() }

            // Quizzes Section
            SectionItems(
                items = quizzes,
                sectionName = "Quizzes",
                onItemClick = { quiz ->
                    navController.navigate(ScreenRoutes.AttemptQuiz.createRoute(quiz.id, quiz.name))
                },
                itemName = { quiz -> quiz.name }
            )

            // Lectures Section
            SectionItems(
                items = lectures,
                sectionName = "Lectures",
                onItemClick = { lecture ->
                    navController.navigate(
                        ScreenRoutes.ViewLecture.createRoute(
                            lecture.id,
                            lecture.name
                        )
                    )
                },
                itemName = { lecture -> lecture.name }
            )

            // Resources Section
            SectionItems(
                items = resources,
                sectionName = "Resources",
                onItemClick = { resource ->
                    navController.navigate(
                        ScreenRoutes.ViewResource.createRoute(
                            resource.id,
                            resource.name
                        )
                    )
                },
                itemName = { resource -> resource.name }
            )
        }

        // Bottom Navigation Bar
        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            BottomNavBar(navController = navController)
        }
    }
}

@Composable
fun <T> SectionItems(
    items: List<T>,
    sectionName: String,
    onItemClick: (T) -> Unit,
    itemName: (T) -> String
) {
    if (items.isEmpty()) return // Don't show empty sections

    Spacer(modifier = Modifier.height(16.dp))

    // Section Heading
    SectionHeading(text = sectionName)

    val rows = items.chunked(2) // Group items into pairs of two

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        rows.forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowItems.forEach { item ->
                    Box(
                        modifier = Modifier
                            .weight(1f) // Each item takes equal width
                    ) {
                        SectionItemCard(
                            text = itemName(item),
                            onClick = { onItemClick(item) }
                        )
                    }
                }

                // If only one item in last row, add empty space to balance
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun SectionItemCard(text: String, onClick: () -> Unit) {
    val cardWidth = LocalConfiguration.current.screenWidthDp.dp / 2 - 24.dp // Two items per row
    val cardHeight = cardWidth / 1.618f // Golden ratio height

    AppCard(
        modifier = Modifier
            .width(cardWidth)
            .height(cardHeight)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            val adjustedTextSize = if (text.length > 15) 12.sp else 20.sp

            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = adjustedTextSize),
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
    }
}
