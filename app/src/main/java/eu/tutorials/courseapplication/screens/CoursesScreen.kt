package eu.tutorials.courseapplication.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
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
import coil.size.Size
import eu.tutorials.courseapplication.Course
import eu.tutorials.courseapplication.CourseDto
import eu.tutorials.courseapplication.MainViewModel

@Composable
fun CoursesScreen(
    modifier: Modifier = Modifier,
    onCourseClick: (CourseDto) -> Unit,
    viewState: MainViewModel.CoursesState
){
    Box(modifier = modifier){
        when{
            viewState.loading -> { CircularProgressIndicator(modifier.align((Alignment.Center)))
            }
            viewState.error != null -> { Text(text = "${viewState.error}")
                println(viewState.error)
            }
            else -> {
                println(viewState.studentDetails)
                CoursesShowScreen(courses = viewState.list, onCourseClick)
            }
        }
    }
}

@Composable
fun CoursesShowScreen(courses: List<CourseDto>, onCategoryClick:(CourseDto) -> Unit) {
    LazyVerticalGrid(GridCells.Fixed(2), modifier = Modifier
        .fillMaxSize()) {
        items(courses){
                course -> CourseItem(course = course, onCategoryClick)
        }
    }
}

@Composable
fun CourseItem(course: CourseDto, onCategoryClick: (CourseDto) -> Unit) {
    Column(
        modifier = Modifier
            .padding(4.dp)
            .clickable { onCategoryClick(course) },
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
                .padding(top = 4.dp)
        )
        if (course.author != null){
            Text(
                text = course.author,
                color = Color.LightGray,
                style = TextStyle(
                    fontWeight = FontWeight.Light,
                    textAlign = TextAlign.Left,
                    fontSize = 16.sp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp)
            )
        }
            Text(
                text = "Members: ${course.participantsNumber}/${course.participantsLimit}",
                color = if (course.participantsNumber < course.participantsLimit){Color.Green} else Color.Red,
                style = TextStyle(
                    fontWeight = FontWeight.Light,
                    textAlign = TextAlign.Left,
                    fontSize = 16.sp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp)
                )


    }
}
