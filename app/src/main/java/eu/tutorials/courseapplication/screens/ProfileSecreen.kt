package eu.tutorials.courseapplication.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import eu.tutorials.courseapplication.MainViewModel
import eu.tutorials.courseapplication.Status
import eu.tutorials.courseapplication.Student

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewState: MainViewModel.CoursesState,
    onLogoutClick: () -> Unit,
    studentViewState: Student,
    onIconUpdate: (String) -> Unit
) {
    Box(
        modifier = modifier.background(
            Brush.verticalGradient(
                colors = listOf(Color(0xFF7B1FA2), Color.Magenta)
            )
        )
    ) {
        when {
            viewState.loading -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(48.dp)
                        .align(Alignment.Center),
                    strokeWidth = 4.dp,
                    strokeCap = StrokeCap.Butt,
                    color = Color.Magenta
                )
            }

            viewState.error != null -> {
                Text(
                    text = "${viewState.error}",
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            else -> {
                ProfileShowScreen(viewState, onLogoutClick, studentViewState, onIconUpdate)
            }
        }
    }
}

@Composable
fun ProfileShowScreen(
    viewState: MainViewModel.CoursesState,
    onLogoutClick: () -> Unit,
    studentViewState: Student,
    onIconUpdate: (String) -> Unit
) {
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            onIconUpdate(uri.toString())
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Image(
            painter = rememberAsyncImagePainter(studentViewState.profileImageUrl),
            contentDescription = "Profile Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(224.dp)
                .clip(CircleShape)
                .border(3.dp, Color.White, CircleShape)
                .clickable { imagePickerLauncher.launch("image/*") }
        )
        Text(
            text = "${studentViewState.firstName} ${studentViewState.lastName}",
            color = Color.White,
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                fontSize = 32.sp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = "Email Icon",
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = studentViewState.email,
                color = Color(0xFFDDDDDD),
                style = TextStyle(
                    fontWeight = FontWeight.Light,
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp
                )
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(if (studentViewState.status == Status.ACTIVE) Color.Green else Color.Red)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (studentViewState.status == Status.ACTIVE) "ACTIVE" else "INACTIVE",
                color = if (studentViewState.status == Status.ACTIVE) Color.Green else Color.Red,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "Logout",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onLogoutClick() }
                .background(
                    Color.Red
                )
                .padding(8.dp)
                .clip(RoundedCornerShape(8.dp))
        )
    }
}
