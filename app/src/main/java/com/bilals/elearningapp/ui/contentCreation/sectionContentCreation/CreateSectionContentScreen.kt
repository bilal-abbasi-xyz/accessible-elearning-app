
package com.bilals.elearningapp.ui.contentCreation.sectionContentCreation
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.bilals.elearningapp.serviceLocator.AppContainer
import com.bilals.elearningapp.navigation.ScreenRoutes
import com.bilals.elearningapp.ui.contentCreation.browsing.categoryList.gradientBackground
import com.bilals.elearningapp.ui.contentCreation.browsing.sectionDetail.SectionItems
import com.bilals.elearningapp.ui.uiComponents.AppBar
import com.bilals.elearningapp.ui.uiComponents.AppCard

@Composable
fun CreateSectionContentScreen(
    navController: NavController,
    sectionId: String,
    sectionName: String,
    appContainer: AppContainer
) {
    val context = LocalContext.current

    val viewModel = remember {
        SectionContentViewModel(
            appContainer.quizRepository,
            appContainer.lectureRepository,
            appContainer.resourceRepository,
            sectionId
        )
    }

    val quizzes by viewModel.quizzes.collectAsState()
    val lectures by viewModel.lectures.collectAsState()
    val resources by viewModel.resources.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var itemType by remember { mutableStateOf("") }
    var newItemName by remember { mutableStateOf("") }

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
            // AppBar
            AppBar(title = "$sectionName") { navController.popBackStack() }

            // Quizzes Section
            SectionItems(
                items = quizzes,
                sectionName = "Quizzes",
                onItemClick = { quiz ->
                    navController.navigate(ScreenRoutes.CreateQuiz.createRoute(quiz.id, quiz.name))
                },
                itemName = { quiz -> quiz.name }
            )

            CreateButton(label = "Create New Quiz") {
                itemType = "Quiz"
                showDialog = true
            }

            // Lectures Section
            SectionItems(
                items = lectures,
                sectionName = "Lectures",
                onItemClick = {
                    lecture ->
                    Log.d("lecture", "lecture in createsectionccontent:  ${lecture.id}")
                    navController.navigate(ScreenRoutes.CreateLecture.createRoute(lecture.id, lecture.name, lecture.sectionId))
                },
                itemName = { lecture -> lecture.name }
            )

            CreateButton(label = "Create New Lecture") {
                itemType = "Lecture"
                showDialog = true
            }

//            // Resources Section
//            SectionItems(
//                items = resources,
//                sectionName = "Resources",
//                onItemClick = { resource ->
//                    navController.navigate(ScreenRoutes.ViewResource.createRoute(resource.id, resource.name))
//                },
//                itemName = { resource -> resource.name }
//            )
//
//            CreateButton(label = "Create New Resource") {
//                itemType = "Resource"
//                showDialog = true
//            }
        }

        // Bottom Navigation Bar
//        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
//            BottomNavBar(navController = navController)
//        }
    }

    // **Popup Dialog for Quiz, Lecture, and Resource Name Input**
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Enter $itemType Name") },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TextField(
                        value = newItemName,
                        onValueChange = { newItemName = it },
                        label = { Text("$itemType Name") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Buttons in one column
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = {
                                if (newItemName.isNotBlank()) {
                                    when (itemType) {
                                        "Quiz" -> viewModel.createNewQuiz(newItemName)
                                        "Lecture" -> viewModel.createNewLecture(newItemName)
                                        "Resource" -> viewModel.createNewResource(newItemName)
                                    }
                                    showDialog = false
                                    newItemName = ""
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Save")
                        }

                        OutlinedButton(
                            onClick = { showDialog = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Cancel")
                        }
                    }
                }
            },
            confirmButton = {}, // Moved to `text`
            dismissButton = {}
        )
    }

}

@Composable
fun CreateButton(label: String, onClick: () -> Unit) {
    AppCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = label, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
