package eu.tutorials.courseapplication.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eu.tutorials.courseapplication.EnrolledCourse
import eu.tutorials.courseapplication.Lecture
import eu.tutorials.courseapplication.MainViewModel
import eu.tutorials.courseapplication.Section
import eu.tutorials.courseapplication.Student
import eu.tutorials.courseapplication.util.VideoPlayer

@Composable
fun CourseContentScreen(
    viewState: MainViewModel.CoursesState,
    modifier: Modifier = Modifier,
    onLectureClick: (String, String) -> Unit,
    viewModel: MainViewModel,
    onIconClick: (String, Boolean) -> Unit,
    studentViewState: Student
) {
    Box(
        modifier = modifier.background(
            Brush.horizontalGradient(
                colors = listOf(Color(0xFF8132b1), Color(0xFF6A1B9A))
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
                ShowCourseContentScreen(viewState, onLectureClick, viewModel, onIconClick, studentViewState)
            }
        }
    }
}

@Composable
fun ShowCourseContentScreen(
    viewState: MainViewModel.CoursesState,
    onLectureClick: (String, String) -> Unit,
    viewModel: MainViewModel,
    onIconClick: (String, Boolean) -> Unit,
    studentViewState: Student
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        VideoPlayer(viewModel)
        SectionShowScreen(
            sections = viewState.courseDetails.sections,
            enrolledCourse = studentViewState.enrolledCourses.find { it.courseId == viewState.courseDetails.code }!!,
            onLectureClick = onLectureClick,
            onIconClick = onIconClick
        )
    }
}

@Composable
fun SectionShowScreen(
    sections: List<Section>,
    onLectureClick: (String, String) -> Unit,
    onIconClick: (String, Boolean) -> Unit,
    enrolledCourse: EnrolledCourse
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth().padding(vertical = 8.dp)
    ) {
        items(sections) { section ->
            SectionBlock(section, onLectureClick, enrolledCourse, onIconClick)
        }
    }
}

@Composable
fun SectionBlock(
    section: Section,
    onLectureClick: (String, String) -> Unit,
    enrolledCourse: EnrolledCourse,
    onIconClick: (String, Boolean) -> Unit
) {
    val isExpanded = remember { mutableStateOf(true) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded.value = !isExpanded.value }
            .padding(8.dp)
    ) {
        Text(
            text = section.title,
            color = Color.White,
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Start,
                fontSize = 24.sp
            ),
            modifier = Modifier
                .fillMaxWidth().padding(bottom = 8.dp)
        )

        if (isExpanded.value) {
            section.lessons.forEach { lesson ->
                Divider(
                    color = Color.White.copy(alpha = 0.3f),
                    thickness = 1.dp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                LessonBlock(lesson, onLectureClick, enrolledCourse, onIconClick)

            }
        }
    }
}

@Composable
fun LessonBlock(
    lesson: Lecture,
    onLectureClick: (String, String) -> Unit,
    enrolledCourse: EnrolledCourse,
    onIconClick: (String, Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onLectureClick(lesson.videoUrl, lesson.id) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = lesson.position.toString(),
            modifier = Modifier
                .padding(16.dp)
                .size(32.dp),
            style = TextStyle(
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            ),
            textAlign = TextAlign.Center
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Completion Status",
                    tint = if (enrolledCourse.completedLecturesId.contains(lesson.id)) Color.Green else Color.Gray,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            onIconClick(
                                lesson.id,
                                enrolledCourse.completedLecturesId.contains(lesson.id)
                            )
                        }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = lesson.title,
                    color = Color.White,
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    maxLines = 1
                )
            }
            Text(
                text = lesson.description,
                color = Color(0xFFBDBDBD), // jasny szary
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal
                ),
                maxLines = 1,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}
