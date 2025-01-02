package eu.tutorials.courseapplication.screens

import android.app.AlertDialog
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
import eu.tutorials.courseapplication.Student

@Composable
fun CourseDetailsScreen(
    viewState: MainViewModel.CoursesState,
    modifier: Modifier = Modifier,
    onSignUpClick: (String) -> Unit,
    studentViewState: Student,
    context: Context
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFE1BEE7), Color(0xFF8E24AA))
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
                    strokeCap = StrokeCap.Round,
                    color = Color(0xFF8E24AA) // Magenta
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
                ShowCourseDetailsScreen(viewState, onSignUpClick, studentViewState, context)
            }
        }
    }
}

@Composable
fun ShowCourseDetailsScreen(
    viewState: MainViewModel.CoursesState,
    onSignUpClick: (String) -> Unit,
    studentViewState: Student,
    context: Context
) {
    val builder: AlertDialog.Builder = AlertDialog.Builder(context)
    builder
        .setMessage("Do you want enroll course ${viewState.courseDetails.name}?")
        .setTitle("Confirm enrollment")
        .setPositiveButton("Yes") { dialog, _ ->
            onSignUpClick(viewState.courseDetails.code)
            dialog.dismiss()
        }
        .setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }

    val dialog: AlertDialog = builder.create()

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = rememberAsyncImagePainter(viewState.courseDetails.imageUrl),
            contentDescription = "Course Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .clip(RoundedCornerShape(8.dp))
                .shadow(4.dp)
        )
        Text(
            text = viewState.courseDetails.name,
            color = Color(0xFF8E24AA), // Magenta for title
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Left,
                fontSize = 30.sp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)

        )
        Text(
            text = viewState.courseDetails.description,
            color = Color.White,
            style = TextStyle(
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.Left,
                fontSize = 20.sp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        )
        Text(
            text = "Members: ${viewState.courseDetails.participantsNumber}/${viewState.courseDetails.participantsLimit}",
            color = if (viewState.courseDetails.participantsNumber < viewState.courseDetails.participantsLimit) {
                Color(0xFF66BB6A)
            } else Color(0xFFD32F2F),
            style = TextStyle(
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Left,
                fontSize = 24.sp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp)
        )
        Text(
            text = "Creator: ${viewState.courseDetails.author}",
            color = Color.White,
            style = TextStyle(
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.Left,
                fontSize = 18.sp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp)
        )

        Button(
            onClick = {
                if (!studentViewState.enrolledCourses.any { it.courseId == viewState.courseDetails.code }) {
                    dialog.show()
                }
            },
            enabled = !viewState.loading || !viewState.softLoading,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
                .clip(androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
        ) {
            if (viewState.loading || viewState.softLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White
                )
            } else {
                Text(
                    text = if (!studentViewState.enrolledCourses.any { it.courseId == viewState.courseDetails.code }) "Sign Up!" else "Already signed up!",
                    color = if (!studentViewState.enrolledCourses.any { it.courseId == viewState.courseDetails.code }) Color.White else Color.Green,
                    style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold)
                )
            }
        }
        Text(
            text = "What you can learn",
            color = Color.White,
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Left,
                fontSize = 30.sp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)

        )
        Column(modifier = Modifier.fillMaxWidth()) {
            viewState.courseDetails.descriptionList.forEach { description ->
                Row(modifier = Modifier.padding(vertical = 4.dp)) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "desc2",
                        tint = Color.Green,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = description,
                        color = Color.White,
                        style = TextStyle(
                            fontWeight = FontWeight.Light,
                            textAlign = TextAlign.Left,
                            fontSize = 22.sp
                        )
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Description",
            color = Color.White,
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Left,
                fontSize = 30.sp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )
        Text(
            text = viewState.courseDetails.descriptionDetails,
            color = Color.White,
            style = TextStyle(
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.Left,
                fontSize = 20.sp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        )
    }
}
