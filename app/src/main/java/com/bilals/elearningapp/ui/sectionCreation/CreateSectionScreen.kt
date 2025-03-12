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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bilals.elearningapp.di.AppContainer
import com.bilals.elearningapp.navigation.ScreenRoutes
import com.bilals.elearningapp.tts.SpeechService
import com.bilals.elearningapp.ui.categoryList.gradientBackground
import com.bilals.elearningapp.ui.theme.AppTypography
import com.bilals.elearningapp.ui.uiComponents.AppBar
import com.bilals.elearningapp.ui.uiComponents.AppCard
import com.bilals.elearningapp.ui.uiComponents.BottomNavBar
import com.bilals.elearningapp.ui.uiComponents.SectionHeading



@Composable
fun CreateSectionScreen(
    navController: NavController,
    courseId: String,
    courseName: String,
    appContainer: AppContainer
) {
    val context = LocalContext.current
    val viewModel = remember { CreateSectionViewModel(appContainer.sectionRepository, appContainer.courseRepository, courseId) }
//    val courseViewModel = remember { CourseViewModel(appContainer.courseRepository) }

    val sections by viewModel.sections.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var newSectionName by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBackground())
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 100.dp) // Increased to fit Publish button
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AppBar(title = "Manage Sections in $courseName") { navController.popBackStack() }

            SectionHeading(text = "List of Created Sections")

            // Display List of Sections
            sections.forEach { section ->
                AppCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            navController.navigate(
                                ScreenRoutes.CreateSectionContent.createRoute(section.id, section.name)
                            )
                        }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val adjustedTextSize = if (section.name.length > 15) 12.sp else 20.sp

                        Text(
                            text = section.name,
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = adjustedTextSize),
                            color = Color.White,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Go to ${section.name}",
                            tint = Color.White
                        )
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            // Button to Create a New Section
            AppCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDialog = true }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Section", tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Create New Section",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
                }
            }

            // **Publish Course Button**
            AppCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        viewModel.publishCourse(courseId)
                        navController.popBackStack()
                    }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = "Publish Course", tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Publish Course",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
                }
            }
        }

        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            BottomNavBar(navController = navController)
        }
    }

    // Popup Dialog for Section Name Input
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Choose Section Name") },
            text = {
                TextField(
                    value = newSectionName,
                    onValueChange = { newSectionName = it },
                    label = { Text("Section Name") }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newSectionName.isNotBlank()) {
                            viewModel.createSection(newSectionName)
                            showDialog = false
                        }
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
