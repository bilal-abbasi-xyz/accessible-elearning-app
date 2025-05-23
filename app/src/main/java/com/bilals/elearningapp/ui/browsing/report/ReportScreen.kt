package com.bilals.elearningapp.ui.browsing.report

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.google.firebase.auth.FirebaseAuth

import HomeViewModel
import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.bilals.elearningapp.navigation.ScreenRoutes
import com.bilals.elearningapp.SessionManager
import com.bilals.elearningapp.ui.uiComponents.AppBar
import com.bilals.elearningapp.ui.uiComponents.AppCard
import com.bilals.elearningapp.ui.uiComponents.HorizontalDividerWithDots

@Composable
fun ReportScreen(navController: NavController) {
    val user = FirebaseAuth.getInstance().currentUser
    val context = LocalContext.current

    var showQuizReport by remember { mutableStateOf(false) }
    var showChatReport by remember { mutableStateOf(false) }
    var showCourseReport by remember { mutableStateOf(false) }

    if (user == null) {
        // Not logged in
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFEAEAEA)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Log in to see your reports",
                fontSize = 18.sp,
                color = Color.Gray
            )
        }
        return
    }

    // Logged in
    val username = user.displayName ?: user.email ?: "User"

    AppBar(title = "Reports for $username") {
        navController.popBackStack()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEAEAEA))
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        Spacer(modifier = Modifier.height(24.dp))

        // Three centered buttons
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AppCard(
                onClick = { showQuizReport = true },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(64.dp)
            ) {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Text("My Quizzes", fontSize = 18.sp, color = Color.White)
                }
            }

            AppCard(
                onClick = { showChatReport = true },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(64.dp)
            ) {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Text("My Chats", fontSize = 18.sp, color = Color.White)
                }
            }

            AppCard(
                onClick = { showCourseReport = true },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(64.dp)
            ) {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Text("My Courses", fontSize = 18.sp, color = Color.White)
                }
            }
        }
    }

    // ——— Quiz Report Dialog ———
    if (showQuizReport) {
        Dialog(onDismissRequest = { showQuizReport = false }) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                tonalElevation = 8.dp,
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .wrapContentHeight()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)
                ) {
                    Text(
                        text = "Quiz Report",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    ReportRow("No. of attempted quizzes", "5")
                    ReportRow(
                        "Courses in which quiz is attempted",
                        "Programming\nData Structures\nAlgorithms"
                    )
                    ReportRow("Overall performance", "78%")

                    Spacer(modifier = Modifier.height(8.dp))

                    AppCard(
                        onClick = { showQuizReport = false },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        Box(Modifier.fillMaxSize(), Alignment.Center) {
                            Text("Close", fontSize = 18.sp, color = Color.White)
                        }
                    }
                }
            }
        }
    }

    // ——— Chat Report Dialog ———
    if (showChatReport) {
        Dialog(onDismissRequest = { showChatReport = false }) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                tonalElevation = 8.dp,
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .wrapContentHeight()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)
                ) {
                    Text(
                        text = "Chat Report",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    ReportRow("No. of chat messages sent", "42")
                    ReportRow("Most active in course forum for", "Data Structures")

                    Spacer(modifier = Modifier.height(8.dp))

                    AppCard(
                        onClick = { showChatReport = false },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        Box(Modifier.fillMaxSize(), Alignment.Center) {
                            Text("Close", fontSize = 18.sp, color = Color.White)
                        }
                    }
                }
            }
        }
    }

    // ——— Course Report Dialog ———
    if (showCourseReport) {
        Dialog(onDismissRequest = { showCourseReport = false }) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                tonalElevation = 8.dp,
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .wrapContentHeight()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)
                ) {
                    Text(
                        text = "Course Report",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    ReportRow("No. of courses published", "3")
                    ReportRow("No. of lectures created", "24")
                    ReportRow("No. of quizzes created", "7")
                    ReportRow(
                        "My courses",
                        "Programming\nData Structures\nCompose UI"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    AppCard(
                        onClick = { showCourseReport = false },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        Box(Modifier.fillMaxSize(), Alignment.Center) {
                            Text("Close", fontSize = 18.sp, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReportRow(key: String, value: String) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = key,
            fontSize = 16.sp,
            color = Color.DarkGray,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            fontSize = 16.sp,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(2f)
        )
    }
}

