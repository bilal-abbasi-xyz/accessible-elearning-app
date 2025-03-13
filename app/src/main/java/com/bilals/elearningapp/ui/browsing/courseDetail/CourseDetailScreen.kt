package com.bilals.elearningapp.ui.contentCreation.browsing.courseDetail

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bilals.elearningapp.di.AppContainer
import com.bilals.elearningapp.navigation.ScreenRoutes
import com.bilals.elearningapp.tts.SpeechService
import com.bilals.elearningapp.ui.contentCreation.browsing.categoryList.gradientBackground
import com.bilals.elearningapp.ui.theme.AppTypography
import com.bilals.elearningapp.ui.uiComponents.AppBar
import com.bilals.elearningapp.ui.uiComponents.AppCard
import com.bilals.elearningapp.ui.uiComponents.BottomNavBar
import com.bilals.elearningapp.ui.uiComponents.SectionHeading


@Composable
fun CourseDetailScreen(
    navController: NavController,
    courseId: String,
    courseName: String,
    appContainer: AppContainer
) {
    val context = LocalContext.current
//    val database = ElearningDatabase.getDatabase(context)
//
//    val sectionDao = remember { database.sectionDao() }
//    val sectionRepo = remember { SectionRepository(sectionDao, context) }

    val viewModel = remember { CourseDetailViewModel(appContainer.sectionRepository, courseId) }

    val sections by viewModel.sections.collectAsState()
    val numOfSections = sections.size

    // Announce the sections count when it changes
    LaunchedEffect(numOfSections) {
        if (numOfSections > 0) {
            val announcement =
                "This screen leads to course forum and course sections."
            SpeechService.announce(context, announcement)
        }
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // AppBar
            AppBar(title = "$courseName") { navController.popBackStack() }

            // Button Row for "Course Forum" and "Course Feedback"
            val isEnabled = true// Your logic to determine if buttons should be enabled

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 0.dp),
                horizontalAlignment = Alignment.CenterHorizontally // Centers content horizontally
            ) {
                // Course Forum Card
                AppCard(
                    modifier = Modifier
                        .fillMaxWidth() // Ensure the AppCard takes up the full width
                        .padding(vertical = 8.dp) // Add vertical padding between cards
                        .clickable {
                            if (isEnabled) {
                                navController.navigate(
                                    ScreenRoutes.CourseForum.createRoute(
                                        courseId,
                                        courseName
                                    )
                                )
                            }
                        }
                        .semantics {
                            contentDescription = ""
                        } // ⛔ Prevents TalkBack from reading automatically

                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth() // Ensure the Row inside the card takes up the full width
                            .padding(16.dp), // Padding for content inside the row
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center // Centers the content inside the row
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Comment,
                            contentDescription = "Forum",
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Course Forum",
                            style = AppTypography.bodyMedium,
                            color = Color.White
                        )
                    }
                }

                // Course Feedback Card
//                AppCard(
//                    modifier = Modifier
//                        .fillMaxWidth() // Ensure the AppCard takes up the full width
//                        .padding(vertical = 8.dp) // Add vertical padding between cards
//                        .clickable {
//                            if (isEnabled) {
//                                // Navigate to Feedback
//                            }
//                        }
//                ) {
//                    Row(
//                        modifier = Modifier
//                            .fillMaxWidth() // Ensure the Row inside the card takes up the full width
//                            .padding(16.dp), // Padding for content inside the row
//                        verticalAlignment = Alignment.CenterVertically,
//                        horizontalArrangement = Arrangement.Center // Centers the content inside the row
//                    ) {
//                        Icon(
//                            imageVector = Icons.Default.Feedback,
//                            contentDescription = "Feedback",
//                            tint = Color.White
//                        )
//                        Spacer(modifier = Modifier.width(8.dp))
//                        Text(
//                            text = "Course Feedback",
//                            style = AppTypography.bodyMedium,
//                            color = Color.White
//                        )
//                    }
//                }
            }

//
//            // Button for "Course Participants"
//            AppCard(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .clickable {
//                        if (isEnabled) {
//                            // Navigate to Course Participants (adjust navigation as per requirement)
////                        navController.navigate(ScreenRoutes.CourseParticipants.createRoute(courseId))
//                        }
//                    }
//            ) {
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(16.dp),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.People,
//                        contentDescription = "Participants",
//                        tint = Color.White
//                    )
//                    Spacer(modifier = Modifier.width(5.dp))
//                    Text(
//                        text = "Course Participants",
//                        style = MaterialTheme.typography.bodyMedium,
//                        color = Color.White
//                    )
//                }
//            }

            Spacer(modifier = Modifier.height(16.dp))

//            // Divider before "Sections"
//            HorizontalDivider(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 16.dp),
//                thickness = 1.dp,
//                color = Color.Gray
//            )

            SectionHeading(text = "Sections")


            // Sections List
            sections.forEach { section ->
                AppCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            // Navigate to Section Detail screen
                            navController.navigate(
                                ScreenRoutes.SectionDetail.createRoute(
                                    section.id,
                                    section.name
                                )
                            )
                        }
                        .semantics {
                            contentDescription = ""
                        }

                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val adjustedTextSize = if (section.name.length > 20) 15.sp else 20.sp

                        Text(
                            text = section.name,
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = adjustedTextSize),
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

        }
        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            BottomNavBar(navController = navController)
        }
    }
}



