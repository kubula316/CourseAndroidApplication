package eu.tutorials.courseapplication.screens

import android.app.AlertDialog
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import eu.tutorials.courseapplication.Course
import eu.tutorials.courseapplication.MainViewModel
import eu.tutorials.courseapplication.Student

@Composable
fun SavedCoursesScreen(
    modifier: Modifier = Modifier,
    onCourseClick: (Course) -> Unit,
    viewState: MainViewModel.CoursesState,
    studentViewState: Student,
    onDeleteClick: (String) -> Unit,
    context: Context
) {
    Box(modifier = modifier.background(Color(0xFF7B1FA2))) {
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
                println(viewState.error)
            }

            else -> {
                ShowSavedCoursesScreen(
                    courses = viewState.savedCourses,
                    onCategoryClick = onCourseClick,
                    viewState,
                    studentViewState,
                    onDeleteClick,
                    context
                )
            }
        }
    }
}

@Composable
fun ShowSavedCoursesScreen(
    courses: List<Course>,
    onCategoryClick: (Course) -> Unit,
    viewState: MainViewModel.CoursesState,
    studentViewState: Student,
    onDeleteClick: (String) -> Unit,
    context: Context
) {
    Column(modifier = Modifier.background(Brush.verticalGradient(colors = listOf(Color.Magenta, Color(0xFF7B1FA2))))) {
        LazyVerticalGrid(
            GridCells.Fixed(1), modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            items(courses) { course ->
                SavedCourseItem(
                    course = course,
                    onCategoryClick = onCategoryClick,
                    viewState = viewState,
                    studentViewState = studentViewState,
                    onDeleteClick = onDeleteClick,
                    context = context
                )
            }
        }
    }
}

@Composable
fun SavedCourseItem(
    course: Course,
    onCategoryClick: (Course) -> Unit,
    viewState: MainViewModel.CoursesState,
    studentViewState: Student,
    onDeleteClick: (String) -> Unit,
    context: Context
) {
    val builder: AlertDialog.Builder = AlertDialog.Builder(context)
    builder
        .setMessage("Are you sure you want to delete course ${course.name}?")
        .setTitle("Confirm Delete")
        .setPositiveButton("Yes") { dialog, _ ->
            onDeleteClick(course.code)
            dialog.dismiss()
        }
        .setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
    val dialog: AlertDialog = builder.create()

    Box(
        modifier = Modifier
            .padding(bottom = 12.dp)
            .background(Color(0xFF7B1FA2), RoundedCornerShape(16.dp)) // Magenta card background
            .clickable { onCategoryClick(course) }
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Image(
                painter = rememberAsyncImagePainter(course.imageUrl),
                contentDescription = "Course Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(16.dp))
            )
            Text(
                text = course.name,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
            if (course.author != null) {
                Text(
                    text = course.author,
                    color = Color(0xFFDDDDDD),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Light,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            val enrolledCourse = studentViewState.enrolledCourses.find { it.courseId == course.code }
            val progress = if (enrolledCourse!!.completedLecturesId != null && enrolledCourse.completedLecturesId!!.isNotEmpty()) {
                val completedLessons = enrolledCourse.completedLecturesId.size
                val totalLessons = course.sections.sumOf { it.lessons.count() }
                if (totalLessons > 0) completedLessons.toFloat() / totalLessons else 0f
            } else 0f

            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                color = Color.Magenta,
                trackColor = Color.Gray
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${(progress * 100).toInt()}% Completed",
                    fontWeight = FontWeight.Bold,
                    color = lerp(Color.White, Color.Green, progress),
                    fontSize = 20.sp,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Remove Course",
                    tint = Color.Red,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { dialog.show() }
                )
            }
        }
    }
}
