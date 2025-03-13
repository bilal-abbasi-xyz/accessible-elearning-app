package com.bilals.elearningapp.ui.contentCreation.courseCreation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
//import com.bilals.elearningapp.navigation.NavDataManager
import com.bilals.elearningapp.navigation.ScreenRoutes

@Composable
fun CourseCreationScreen(navController: NavController, viewModel: CourseCreationViewModel = viewModel()) {

    val sections by viewModel.sections.observeAsState(emptyList())
    val courseName by viewModel.courseName.observeAsState("")
    var newSectionName by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = courseName,
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        BasicTextField(
            value = newSectionName,
            onValueChange = { newSectionName = it },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (newSectionName.isNotBlank()) {
                    viewModel.addSection(newSectionName)
                    newSectionName = "" // Clear input
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Add New Section")
        }

        Spacer(modifier = Modifier.height(16.dp))

        sections.forEach { section ->
            Button(
                onClick = {
//                    NavDataManager.setSectionId(section.id)
                    navController.navigate(ScreenRoutes.SectionCreation.route)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = section.name)
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
