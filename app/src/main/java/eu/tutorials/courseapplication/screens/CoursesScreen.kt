package eu.tutorials.courseapplication.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import eu.tutorials.courseapplication.Course
import eu.tutorials.courseapplication.MainViewModel

@Composable
fun CoursesScreen(
    modifier: Modifier = Modifier,
    onCourseClick: (Course) -> Unit,
    viewState: MainViewModel.CoursesState
){
    Box(modifier = Modifier.fillMaxSize()){
        when{
            viewState.loading -> { CircularProgressIndicator(modifier.align((Alignment.Center)))
            }
            viewState.error != null -> { Text(text = "${viewState.error}")
                println(viewState.error)
            }
            else -> {
                CoursesShowScreen(courses = viewState.list, onCourseClick)
            }
        }
    }
}

@Composable
fun CoursesShowScreen(courses: List<Course>, onCategoryClick:(Course) -> Unit) {
    LazyVerticalGrid(GridCells.Fixed(1), modifier = Modifier.fillMaxSize()) {
        items(courses){
                course -> CourseItem(course = course, onCategoryClick)
        }
    }
}

@Composable
fun CourseItem(course: Course, onCategoryClick: (Course) -> Unit) {
    Column(
        modifier = Modifier.padding(8.dp).fillMaxSize().clickable { onCategoryClick(course)},
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(painter = rememberAsyncImagePainter(course.imageUrl), contentDescription = "Category Image", modifier = Modifier
            .fillMaxSize()
            .aspectRatio(1f))
        Text(text = course.description, color = Color.Black, style = TextStyle(fontWeight = FontWeight.Bold), modifier = Modifier.padding(top = 4.dp))
    }
}
