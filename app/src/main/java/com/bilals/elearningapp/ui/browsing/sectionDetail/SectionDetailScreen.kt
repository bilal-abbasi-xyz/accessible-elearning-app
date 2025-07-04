package com.bilals.elearningapp.ui.contentCreation.browsing.sectionDetail

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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bilals.elearningapp.serviceLocator.AppContainer
import com.bilals.elearningapp.navigation.ScreenRoutes
import com.bilals.elearningapp.ui.contentCreation.browsing.categoryList.gradientBackground
import com.bilals.elearningapp.ui.uiComponents.AppBar
import com.bilals.elearningapp.ui.uiComponents.AppCard
import com.bilals.elearningapp.ui.uiComponents.SectionHeading

@Composable
fun SectionDetailScreen(
    navController: NavController,
    sectionId: String,
    sectionName: String,
    appContainer: AppContainer
) {
    val context = LocalContext.current

    // 1) remove resourceRepo and resources state entirely

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


    // 1. Hardcoded URL-name pairs
    val allVideos = listOf(
        "https://archive.org/download/how-to-calculate-faster-than-a-calculator-mental-math-1/How%20to%20Calculate%20Faster%20than%20a%20Calculator%20-%20Mental%20Math%20%231.mp4" to "Mental Math Tricks",
        "https://ia600801.us.archive.org/35/items/neural-networks-explained-in-5-minutes/Neural%20Networks%20Explained%20in%205%20minutes.mp4" to "Neural Networks in 5 Minutes",
        "https://ia800408.us.archive.org/12/items/what-is-cyber-security-how-it-works-cyber-security-in-7-minutes-cyber-security-simplilearn/Computer%20Science%20Basics_%20Should%20I%20Learn%20to%20Code_.mp4" to "Learn to Code",
        "https://ia600408.us.archive.org/12/items/what-is-cyber-security-how-it-works-cyber-security-in-7-minutes-cyber-security-simplilearn/What%20Is%20AI_%20_%20Artificial%20Intelligence%20_%20What%20is%20Artificial%20Intelligence_%20_%20AI%20In%205%20Mins%20_Simplilearn.mp4" to "What Is A.I?",
        "https://ia800408.us.archive.org/12/items/what-is-cyber-security-how-it-works-cyber-security-in-7-minutes-cyber-security-simplilearn/What%20Is%20Cyber%20Security%20_%20How%20It%20Works_%20_%20Cyber%20Security%20In%207%20Minutes%20_%20Cyber%20Security%20_%20Simplilearn.mp4" to "Cyber Security in 7 Minutes"
    )

//// 2. Shuffle and pick any 2
//    val selectedVideos = allVideos.shuffled().take(2)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBackground())
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 60.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AppBar(title = sectionName) { navController.popBackStack() }

            // Quizzes Section (unchanged)
            SectionItems(
                items = quizzes,
                sectionName = "Quizzes",
                onItemClick = { quiz ->
                    navController.navigate(
                        ScreenRoutes.AttemptQuiz.createRoute(quiz.id, quiz.name)
                    )
                },
                itemName = { it.name }
            )

            // Lectures Section (unchanged)
            SectionItems(
                items = lectures,
                sectionName = "Lectures",
                onItemClick = { lecture ->
                    navController.navigate(
                        ScreenRoutes.ViewLecture.createRoute(lecture.id, lecture.name)
                    )
                },
                itemName = { it.name }
            )

            // 3) Videos Section (new)
            SectionItems(
                items = allVideos,
                sectionName = "Videos",
                onItemClick = { (url, _) ->
                    navController.navigate(
                        ScreenRoutes.VideoScreen.createRoute(url)
                    )
                },
                itemName = { (_, name) -> name }
            )

        }

//        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
//            BottomNavBar(navController = navController)
//        }
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
            .semantics { contentDescription = "" }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            val adjustedTextSize = if (text.length > 35) 12.sp else 20.sp

            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = adjustedTextSize),
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
    }
}
