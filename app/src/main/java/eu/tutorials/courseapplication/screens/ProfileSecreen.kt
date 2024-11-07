package eu.tutorials.courseapplication.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import eu.tutorials.courseapplication.CourseDto
import eu.tutorials.courseapplication.MainViewModel
import eu.tutorials.courseapplication.Status

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewState: MainViewModel.CoursesState,
    onLogoutClick : ()-> Unit
){
    Box(modifier = modifier){
        when{
            viewState.loading -> { CircularProgressIndicator(modifier.align((Alignment.Center)))
            }
            viewState.error != null -> { Text(text = "${viewState.error}")
                println(viewState.error)
            }
            else -> {
                ProfileShowScreen(viewState = viewState, onLogoutClick)
            }
        }
    }
}

@Composable
fun ProfileShowScreen(viewState: MainViewModel.CoursesState, onLogoutClick : ()-> Unit) {
    Column(
        modifier = Modifier
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = rememberAsyncImagePainter(viewState.studentDetails.profileImageUrl),
            contentDescription = "Profile Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(156.dp)
                .clip(CircleShape) // clip to the circle shape
                .border(4.dp, Color.Gray, CircleShape)//optional
                .padding(top = 12.dp)
        )
        Text(
            text = "${viewState.studentDetails.firstName} ${viewState.studentDetails.lastName}",
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
            Text(
                text = viewState.studentDetails.email,
                color = Color.Gray,
                style = TextStyle(
                    fontWeight = FontWeight.Light,
                    textAlign = TextAlign.Center,
                    fontSize = 24.sp
                )
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(18.dp) // Rozmiar lampki
                    .background(if (viewState.studentDetails.status == Status.ACTIVE) Color.Green else Color.Red)
                    .padding(end = 8.dp)
                    .clip(RoundedCornerShape(32.dp))
            )
            Text(
                text = if (viewState.studentDetails.status == Status.ACTIVE) "ACTIVE" else "INACTIVE",
                color = if (viewState.studentDetails.status == Status.ACTIVE) Color.Green else Color.Red,
                fontSize = 32.sp
            )
        }
        Text(
            text = "Logout",
            color = Color.Red,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onLogoutClick() }  // tutaj dodajemy kliknięcie
        )

    }
}
