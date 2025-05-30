package com.bilals.elearningapp.ui.browsing.report

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.bilals.elearningapp.ui.uiComponents.AppBar
import com.bilals.elearningapp.ui.uiComponents.AppCard
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ReportScreen(navController: NavController) {
    val user = FirebaseAuth.getInstance().currentUser
    val context = LocalContext.current

    var showQuizReport by remember { mutableStateOf(false) }
    var showChatReport by remember { mutableStateOf(false) }
    var showCourseReport by remember { mutableStateOf(false) }

    if (user == null) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color(0xFFEAEAEA)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Log in to see your reports",
                fontSize = 18.sp,
                color = Color.Gray
            )
        }
        return
    }

    Scaffold(
        topBar = {
            AppBar(
                title = "Reports",
                onBackClick = { navController.popBackStack() }
            )
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFEAEAEA))
                    .padding(innerPadding)        // respect scaffold insets
                    .padding(horizontal = 16.dp), // overall horizontal inset
                verticalArrangement = Arrangement.Top,  // push children to top
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(24.dp))

                // bump from 64 to 80 dp, and a little extra spacing
                ReportButton("My Quizzes", height = 80.dp) { showQuizReport = true }
                Spacer(Modifier.height(16.dp))
                ReportButton("My Chats", height = 80.dp) { showChatReport = true }
                Spacer(Modifier.height(16.dp))
                ReportButton("My Courses", height = 80.dp) { showCourseReport = true }
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
                            Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(
                                12.dp,
                                Alignment.CenterVertically
                            )
                        ) {
                            // Full-width heading
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .clearAndSetSemantics { contentDescription = "Quiz Report" }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Quiz Report", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            }

                            // Rows read together
                            ReportRow("Number of attempted quizzes", "5")
                            ReportRow(
                                "Courses in which quiz is attempted",
                                "Programming\nData Structures\nAlgorithms"
                            )
                            ReportRow("Overall performance", "78%")

                            Spacer(Modifier.height(8.dp))

                            ReportCloseButton { showQuizReport = false }
                        }
                    }
                }
            }

            // ——— Chat Report Dialog ———
            if (showChatReport) {
                Dialog(onDismissRequest = { showChatReport = false }) {
                    Surface( /* same modifiers */) {
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .clearAndSetSemantics { contentDescription = "Chat Report" }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Chat Report", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            }

                            ReportRow("Number of chat messages sent", "42")
                            ReportRow("Most active in course forum for", "Data Structures")

                            Spacer(Modifier.height(8.dp))
                            ReportCloseButton { showChatReport = false }
                        }
                    }
                }
            }

            // ——— Course Report Dialog ———
            if (showCourseReport) {
                Dialog(onDismissRequest = { showCourseReport = false }) {
                    Surface( /* same modifiers */) {
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .clearAndSetSemantics { contentDescription = "Course Report" }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "Course Report",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            ReportRow("Number of courses published", "3")
                            ReportRow("Number of lectures created", "24")
                            ReportRow("Number of quizzes created", "7")
                            ReportRow("My courses", "Programming\nData Structures\nCompose UI")

                            Spacer(Modifier.height(8.dp))
                            ReportCloseButton { showCourseReport = false }
                        }
                    }
                }
            }
        }
    )
}
@Composable
private fun ReportButton(
    label: String,
    height: Dp = 64.dp,
    onClick: () -> Unit
) {
    AppCard(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clearAndSetSemantics {
                role = Role.Button
                contentDescription = label
            }
    ) {
        Box(Modifier.fillMaxSize(), Alignment.Center) {
            Text(label, fontSize = 18.sp, color = Color.White)
        }
    }
}


@Composable
private fun ReportCloseButton(onClick: () -> Unit) {
    AppCard(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clearAndSetSemantics {
                role = Role.Button
                contentDescription = "Close"
            }
    ) {
        Box(Modifier.fillMaxSize(), Alignment.Center) {
            Text("Close", fontSize = 18.sp, color = Color.White)
        }
    }
}

@Composable
private fun ReportRow(key: String, value: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .clearAndSetSemantics {
                // 4. Read both columns as one node
                contentDescription = "$key: $value"
            }
            .padding(vertical = 8.dp),
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
