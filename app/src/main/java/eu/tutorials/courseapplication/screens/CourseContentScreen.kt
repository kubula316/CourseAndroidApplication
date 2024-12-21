package eu.tutorials.courseapplication.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    onLecutreClick: (String, String) -> Unit,
    viewModel: MainViewModel,
    onIconClick: (String, Boolean) -> Unit,
    studentViewState :Student
) {
    Box(modifier = modifier.background(
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
    )) {
        when {
            viewState.loading -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(48.dp) // Adjust the size as needed
                        .align(Alignment.Center),
                    strokeWidth = 4.dp,
                    strokeCap = StrokeCap.Butt,
                    color = Color.Magenta
                )
            }

            viewState.error != null -> {
                Text(text = "${viewState.error}")
                println(viewState.error)
            }
            else -> {
                ShowCourseContentScreen(viewState, onLecutreClick, viewModel, onIconClick, studentViewState)
            }
        }
    }
}

@Composable
fun ShowCourseContentScreen(viewState: MainViewModel.CoursesState, onLectureClick: (String, String) -> Unit, viewModel: MainViewModel, onIconClick: (String, Boolean) -> Unit, studentViewState: Student) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        VideoPlayer(viewModel)
        SectionShowScreen(sections = viewState.courseDetails.sections,
            enrolledCourse = studentViewState.enrolledCourses.find { it.courseId == viewState.courseDetails.code}!!,
            onLectureClick = { url, lectureId->
                onLectureClick(url, lectureId)
        },
            onIconClick = onIconClick)
    }
}

@Composable
fun SectionShowScreen(sections: List<Section>,
                      onLectureClick: (String, String) -> Unit,
                      onIconClick: (String, Boolean) -> Unit,
                      enrolledCourse: EnrolledCourse) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        items(sections) { section ->
            SectionBlock(section, onLectureClick, enrolledCourse, onIconClick)
        }
    }
}

@Composable
fun SectionBlock(section: Section, onLectureClick: (String, String) -> Unit, enrolledCourse: EnrolledCourse, onIconClick: (String, Boolean) -> Unit) {
    val isExpanded = remember { mutableStateOf(true) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { isExpanded.value = !isExpanded.value }
    ) {
            Text(
                text = section.title,
                color = Color.White,
                style = TextStyle(
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Left,
                    fontSize = 22.sp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, start = 16.dp, bottom = 4.dp)

            )

            if (isExpanded.value) {
                section.lessons.forEach { lesson ->
                    LessonBlock(lesson = lesson, onLectureClick = onLectureClick, enrolledCourse, onIconClick)
                }

            }


    }
}
@Composable
fun LessonBlock(lesson: Lecture, onLectureClick: (String, String) -> Unit, enrolledCourse: EnrolledCourse, onIconClick: (String, Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onLectureClick(lesson.videoUrl, lesson.id) },
        verticalAlignment = Alignment.CenterVertically

    ) {
        Text(text = lesson.position.toString(),
            modifier = Modifier.padding(18.dp),
            style = TextStyle(
                fontSize = 26.sp
            ),
        )
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Row{
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Check Icon",
                    tint = if (enrolledCourse.completedLecturesId.contains(lesson.id)) Color.Green else Color.Gray,
                    modifier = Modifier.clickable { onIconClick(lesson.id,
                        enrolledCourse.completedLecturesId.contains(lesson.id)
                    ) }
                )
                Text(
                    text = lesson.title,
                    color = Color.White,
                    style = TextStyle(
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Left,
                        fontSize = 22.sp
                    ),
                    maxLines = 1
                )
            }
            Text(modifier = Modifier.padding(top = 4.dp),
                text = lesson.description,
                color = Color.White,
                style = TextStyle(
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Left,
                    fontSize = 16.sp
                ),
                maxLines = 1
            )
        }
    }

}




