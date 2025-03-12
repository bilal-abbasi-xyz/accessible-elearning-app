package com.bilals.elearningapp.ui.publicForum

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
//import com.bilals.elearningapp.data.model.Message
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PublicForumScreen(navController: NavHostController) {
//    val viewModel: PublicForumViewModel = viewModel()
//    var newMessage by remember { mutableStateOf("") }
//
//    Column(modifier = Modifier.fillMaxSize()) {
//        LazyColumn(
//            modifier = Modifier
//                .weight(1f)
//                .padding(8.dp)
//        ) {
//            items(viewModel.messages) { message ->
//                ChatMessageItem(message)
//            }
//        }
//
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(8.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            OutlinedTextField(
//                value = newMessage,
//                onValueChange = { newMessage = it },
//                label = { Text("Enter message") },
//                modifier = Modifier.weight(1f)
//            )
//            Spacer(modifier = Modifier.width(8.dp))
//            Button(onClick = {
//                viewModel.sendMessage(newMessage)
//                newMessage = ""
//            }) {
//                Text("Send")
//            }
//        }
//    }
//}
//
//@Composable
//fun ChatMessageItem(message: Message) {
//    val isSender =
//        message.sender == "You"
//    val bubbleColor = if (isSender) Color(0xFFDCF8C6) else Color(0xFFECE5DD)
//    val textColor = if (isSender) Color.Black else Color.Black
//
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(vertical = 4.dp)
//            .padding(start = if (isSender) 64.dp else 8.dp, end = if (isSender) 8.dp else 64.dp)
//    ) {
//        Row(
//            horizontalArrangement = if (isSender) Arrangement.End else Arrangement.Start,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Box(
//                modifier = Modifier
//                    .background(bubbleColor, shape = RoundedCornerShape(16.dp))
//                    .padding(12.dp)
//            ) {
//                Text(
//                    text = message.message,
//                    style = TextStyle(color = textColor, fontWeight = FontWeight.Normal),
//                    modifier = Modifier.padding(8.dp)
//                )
//            }
//        }
//
//        Text(
//            text = formatTimestamp(message.timestamp),
//            modifier = Modifier
//                .align(if (isSender) Alignment.End else Alignment.Start)
//                .padding(horizontal = 12.dp)
//                .padding(top = 4.dp), color = Color.Gray
//        )
//    }
//}
//
//fun formatTimestamp(timestamp: Long): String {
//    val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
//    return dateFormat.format(Date(timestamp))
//}
//
//@Preview(showBackground = true)
//@Composable
//fun PublicForumScreenPreview() {
//    val navController = rememberNavController()
//    PublicForumScreen(navController)
}