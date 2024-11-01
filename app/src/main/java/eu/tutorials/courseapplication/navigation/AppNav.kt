package eu.tutorials.courseapplication.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import eu.tutorials.courseapplication.MainViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import eu.tutorials.courseapplication.screens.CoursesScreen
import eu.tutorials.courseapplication.screens.LoginScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNav(){
    val navController = rememberNavController()
    val courseViewModel: MainViewModel = viewModel()
    val viewState by courseViewModel.coursesState
    val items = listOf(
        BottomNavigationItem(
        title = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Default.Home,
        hasNotification = false,
        badgeCount = null
        ),
        BottomNavigationItem(
            title = "Search",
            selectedIcon = Icons.Filled.Search,
            unselectedIcon = Icons.Default.Search,
            hasNotification = false,
            badgeCount = null
        ),
        BottomNavigationItem(
            title = "Saved",
            selectedIcon = Icons.Filled.Create,
            unselectedIcon = Icons.Default.Create,
            hasNotification = false,
            badgeCount = null
        ),
        BottomNavigationItem(
            title = "Account",
            selectedIcon = Icons.Filled.AccountCircle,
            unselectedIcon = Icons.Default.AccountCircle,
            hasNotification = false,
            badgeCount = null
        )

    )

    Scaffold(
        bottomBar = {if (viewState.isAuthenticated)
            NavigationBar {
                items.forEachIndexed { index, item -> 
                    NavigationBarItem(
                        selected = viewState.selectedItemIndex == index,
                        onClick = { courseViewModel.changeItemIndex(index)},
                        label = { Text(text = item.title)},
                        icon = { BadgedBox(badge = {
                            if (item.badgeCount != null){
                                Text(text = item.badgeCount.toString())}
                            else if (item.hasNotification){
                                Badge()}
                        }) {
                            Icon(
                                imageVector = if (index == viewState.selectedItemIndex){item.selectedIcon} else item.unselectedIcon,
                                contentDescription = null)
                        }})
                }
            }
        },
        topBar = {if (viewState.isAuthenticated)
            TopAppBar(
                title = { Text(text = items[viewState.selectedItemIndex].title)},
                navigationIcon = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) {paddingValues ->
        NavHost(navController = navController, startDestination = LoginScreen) {
            composable<LoginScreen> {
                LoginScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    viewState = viewState,
                    onLoginClick = { email, password ->
                    courseViewModel.login(email, password)
                    navController.navigate(CoursesScreen)
                })
            }
            composable<CoursesScreen>{
                CoursesScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    viewState = viewState,
                    onCourseClick = {
                    println("GOTODETAILS")
                })
            }

        }
    }


}