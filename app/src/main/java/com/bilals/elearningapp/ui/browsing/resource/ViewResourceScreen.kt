package com.bilals.elearningapp.ui.contentCreation.browsing.resource

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
//import com.bilals.elearningapp.navigation.NavDataManager

@Composable
fun ViewResourceScreen(navController: NavController, resourceId: String, resourceName: String) {

//    val resourceId = NavDataManager.selectedResourceId.value

    val resourceViewModel: ResourceViewModel = viewModel()

//    LaunchedEffect(resourceId) {
//        if (resourceId != null) {
//            resourceViewModel.loadResource(resourceId)
//        }
//    }

    val resource = resourceViewModel.resource.value

    Column(
        modifier = Modifier.padding(16.dp)
    ) {

        if (resource != null) {
            Text(
                text = resource.name,
                style = androidx.compose.material3.MaterialTheme.typography.headlineMedium
            )

            Text(
                text = "File Path: ${resource.filePath}",
                style = androidx.compose.material3.MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 8.dp)
            )

            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(resource.filePath))
                    navController.context.startActivity(intent)
                },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(text = "Open Resource")
            }
        } else {
            Text(text = "No resource uploaded yet", modifier = Modifier.padding(top = 16.dp))
        }

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(text = "Back")
        }
    }
}

