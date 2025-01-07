package eu.tutorials.courseapplication.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import eu.tutorials.courseapplication.CourseDto
import eu.tutorials.courseapplication.MainViewModel
import eu.tutorials.courseapplication.ui.theme.MagentaItemColor
import eu.tutorials.courseapplication.ui.theme.MagentaLightBackground
import eu.tutorials.courseapplication.ui.theme.MagentaPrimary

@Composable
fun CoursesScreen(
    modifier: Modifier = Modifier,
    onCourseClick: (CourseDto) -> Unit,
    viewState: MainViewModel.CoursesState
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MagentaLightBackground)
    ) {
        when {
            viewState.loading -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(48.dp)
                        .align(Alignment.Center),
                    strokeWidth = 4.dp,
                    strokeCap = StrokeCap.Round,
                    color = MagentaPrimary
                )
            }
            viewState.error != null -> {
                Text(
                    text = "Error: ${viewState.error}",
                    color = Color.Red,
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    ),
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            else -> {
                CoursesShowScreen(courses = viewState.list, onCourseClick)
            }
        }
    }
}

@Composable
fun CoursesShowScreen(courses: List<CourseDto>, onCourseClick: (CourseDto) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(2.dp) // Add spacing between items
    ) {
        items(courses) { course ->
            CourseItem(course = course, onCourseClick)
        }
    }
}

@Composable
fun CourseItem(course: CourseDto, onCourseClick: (CourseDto) -> Unit) {
    Column(
        modifier = Modifier
            .padding(6.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MagentaItemColor)
            .clickable { onCourseClick(course) }
            .padding(8.dp),
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
            color = MagentaPrimary,
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
            ),
            minLines = 2,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 8.dp)
        )
        course.author?.let { author ->
            Text(
                text = "By $author",
                color = Color.Gray,
                style = TextStyle(
                    fontWeight = FontWeight.Light,
                    fontSize = 14.sp
                ),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        Text(
            text = "Members: ${course.participantsNumber}/${course.participantsLimit}",
            color = if (course.participantsNumber < course.participantsLimit) MagentaPrimary else Color.Red,
            style = TextStyle(
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp
            ),
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}