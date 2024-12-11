package eu.tutorials.courseapplication.screens

import android.app.AlertDialog
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import eu.tutorials.courseapplication.Course
import eu.tutorials.courseapplication.MainViewModel
import eu.tutorials.courseapplication.Student

@Composable
fun SavedCoursesScreen(
    modifier: Modifier = Modifier,
    onCourseClick: (Course) -> Unit,
    viewState: MainViewModel.CoursesState,
    studentViewState : Student,
    onDeleteClick : (String) -> Unit,
    context:Context
){
    Box(modifier = modifier){
        when{
            viewState.loading -> { CircularProgressIndicator(modifier.align((Alignment.Center)))
            }
            viewState.error != null -> { Text(text = "${viewState.error}")
                println(viewState.error)
            }
            else -> {
                ShowSavedCoursesScreen(courses = viewState.savedCourses, onCourseClick, viewState, studentViewState, onDeleteClick, context)
            }
        }
    }
}

@Composable
fun ShowSavedCoursesScreen(courses: List<Course>, onCategoryClick:(Course) -> Unit, viewState: MainViewModel.CoursesState, studentViewState: Student, onDeleteClick: (String) -> Unit, context: Context) {
    Column {
        LazyVerticalGrid(
            GridCells.Fixed(1), modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            items(courses){
                    course -> SavedCourseItem(course = course, onCategoryClick, viewState, studentViewState, onDeleteClick, context)
            }
        }
    }

}



@Composable
fun SavedCourseItem(course: Course, onCategoryClick: (Course) -> Unit, viewState: MainViewModel.CoursesState, studentViewState: Student, onDeleteClick: (String) -> Unit, context: Context) {
    val builder: AlertDialog.Builder = AlertDialog.Builder(context)
    builder
        .setMessage("Are you sure you want delete course ${course.name} ?")
        .setTitle("Confirm delete")
        .setPositiveButton("Yes") { dialog, which ->
            onDeleteClick(course.code)
            dialog.dismiss()
        }
        .setNegativeButton("No") { dialog, which ->
            dialog.dismiss()
        }
    val dialog: AlertDialog = builder.create()
    Box(modifier = Modifier.padding(bottom = 12.dp)){
        Box(modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.07f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(4.dp)
            .clickable { onCategoryClick(course) }
        ){
            Column(
                modifier = Modifier
                    .padding(4.dp)
                ,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = rememberAsyncImagePainter(course.imageUrl),
                    contentDescription = "Category Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .clip(RoundedCornerShape(32.dp))
                        .border(
                            width = 1.dp,
                            color = Color.Magenta,
                            shape = RoundedCornerShape(32.dp)
                        )
                )
                Text(
                    text = course.name,
                    color = Color.White,
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Left,
                        fontSize = 24.sp
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp)
                )
                if (course.author != null){
                    Text(
                        text = course.author,
                        color = Color.LightGray,
                        style = TextStyle(
                            fontWeight = FontWeight.Light,
                            textAlign = TextAlign.Left,
                            fontSize = 20.sp
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp)
                    )
                }

                val enrolledCourse = studentViewState.enrolledCourses.find { it.courseId == course.code }

                val progress = if (enrolledCourse!!.completedLecturesId != null && enrolledCourse.completedLecturesId!!.isNotEmpty()) {
                    val completedLessons = enrolledCourse.completedLecturesId.size
                    val totalLessons = course.sections.sumOf { it.lessons.count() }

                    if (totalLessons > 0) {
                        completedLessons.toFloat() / totalLessons
                    } else {
                        0f
                    }
                } else {
                    0f
                }

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
                        text = "${progress*100} % Completed",
                        color = Color.Magenta.copy(0.6f),
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            textAlign = TextAlign.Left
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    Icon(imageVector = Icons.Filled.Delete, contentDescription = "Remove Course", tint = Color.Red, modifier = Modifier.clickable { dialog.show() })
                }


            }
    }







    }
}

