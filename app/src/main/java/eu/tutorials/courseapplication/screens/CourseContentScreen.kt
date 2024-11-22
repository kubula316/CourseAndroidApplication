package eu.tutorials.courseapplication.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.exoplayer.ExoPlayer
import coil.compose.rememberAsyncImagePainter
import eu.tutorials.courseapplication.Lecture
import eu.tutorials.courseapplication.MainViewModel
import eu.tutorials.courseapplication.Section
import eu.tutorials.courseapplication.navigation.CourseDetailsScreen
import eu.tutorials.courseapplication.util.VideoPlayer


@Composable
fun CourseContentScreen(
    viewState: MainViewModel.CoursesState,
    modifier: Modifier = Modifier,
    onLecutreClick: (String) -> Unit
) {
    Box(modifier = modifier.background(
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
    )) {
        when {
            viewState.loading -> {
                CircularProgressIndicator(modifier.align((Alignment.Center)))
            }

            viewState.error != null -> {
                Text(text = "${viewState.error}")
                println(viewState.error)
            }

            else -> {
                ShowCourseContentScreen(viewState, onLecutreClick)
            }
        }
    }
}

@Composable
fun ShowCourseContentScreen(viewState: MainViewModel.CoursesState, onLectureClick: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        VideoPlayer(videoUrl = viewState.playVideoUrl)
        SectionShowScreen(sections = viewState.courseDetails.sections,
            onLectureClick = { url->
                onLectureClick(url)
        })
    }
}

@Composable
fun SectionShowScreen(sections: List<Section>, onLectureClick: (String) -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        items(sections) { section ->
            SectionBlock(section, onLectureClick)
        }
    }
}

@Composable
fun SectionBlock(section: Section, onLectureClick: (String) -> Unit) {
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
                    fontSize = 20.sp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, start = 16.dp)

            )

            if (isExpanded.value) {
                section.lessons.forEach() { lesson ->
                    LessonBlock(lesson = lesson, onLectureClick = onLectureClick)
                }

            }


    }
}
@Composable
fun LessonBlock(lesson: Lecture, onLectureClick: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {onLectureClick(lesson.videoUrl)},
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
                    tint = Color.Green
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




