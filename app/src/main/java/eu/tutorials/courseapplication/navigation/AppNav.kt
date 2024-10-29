package eu.tutorials.courseapplication.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import eu.tutorials.courseapplication.MainViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import eu.tutorials.courseapplication.screens.CoursesScreen
import eu.tutorials.courseapplication.screens.LoginScreen

@Composable
fun AppNav(){
    val navController = rememberNavController()
    val courseViewModel: MainViewModel = viewModel()
    val viewState by courseViewModel.coursesState


    NavHost(navController = navController, startDestination = LoginScreen) {
        composable<LoginScreen> {
            LoginScreen(modifier = Modifier.fillMaxSize(),viewState = viewState,onLoginClick = { email, password ->
                courseViewModel.login(email, password)
                navController.navigate(CoursesScreen)
            })
        }
        composable<CoursesScreen>{
            CoursesScreen(viewState = viewState, onCourseClick = {
                println("GOTODETAILS")
            })
        }

    }
}