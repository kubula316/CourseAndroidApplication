package eu.tutorials.courseapplication.navigation

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import eu.tutorials.courseapplication.MainViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import eu.tutorials.courseapplication.Course
import eu.tutorials.courseapplication.screens.CourseContentScreen
import eu.tutorials.courseapplication.screens.CourseDetailsScreen
import eu.tutorials.courseapplication.screens.CoursesScreen
import eu.tutorials.courseapplication.screens.LoginScreen
import eu.tutorials.courseapplication.screens.ProfileScreen
import eu.tutorials.courseapplication.screens.RegistrationScreen
import eu.tutorials.courseapplication.screens.SavedCoursesScreen
import eu.tutorials.courseapplication.screens.SearchScreen
import eu.tutorials.courseapplication.screens.SearchTagScreen
import eu.tutorials.courseapplication.ui.theme.MagentaHaze
import eu.tutorials.courseapplication.util.MainViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNav(){
    val context : Context = LocalContext.current
    val navController = rememberNavController()
    val courseViewModel: MainViewModel = viewModel(
        factory = MainViewModelFactory(context.applicationContext)
    )
    val viewState by courseViewModel.coursesState
    val studentViewState by courseViewModel.studentDetails
    val items = listOf(
        BottomNavigationItem(
            title = "Home",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Default.Home,
            hasNotification = false,
            badgeCount = null,
            onClickAction = {
                navController.navigate(CoursesScreen)
                courseViewModel.turnOfLookingAtDetails()
                courseViewModel.stopVideoPlayer()
            }
        ),
        BottomNavigationItem(
            title = "Search",
            selectedIcon = Icons.Filled.Search,
            unselectedIcon = Icons.Default.Search,
            hasNotification = false,
            badgeCount = null,
            onClickAction = {
                navController.navigate(SearchScreen)
                courseViewModel.turnOfLookingAtDetails()
                courseViewModel.stopVideoPlayer()
            }
        ),
        BottomNavigationItem(
            title = "My Courses",
            selectedIcon = Icons.Filled.Create,
            unselectedIcon = Icons.Default.Create,
            hasNotification = false,
            badgeCount = null,
            onClickAction = {
                navController.navigate(SavedCoursesScreen)
                courseViewModel.turnOfLookingAtDetails()
                courseViewModel.stopVideoPlayer()
            }
        ),
        BottomNavigationItem(
            title = "Account",
            selectedIcon = Icons.Filled.AccountCircle,
            unselectedIcon = Icons.Default.AccountCircle,
            hasNotification = false,
            badgeCount = null,
            onClickAction = {
                navController.navigate(ProfileScreen)
                courseViewModel.turnOfLookingAtDetails()
                courseViewModel.stopVideoPlayer()
            }
        )

    )
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    val systemUiController = rememberSystemUiController()

    SideEffect {
        systemUiController.setSystemBarsColor(
            color =  Color.Black,
            darkIcons = true
        )
    }
    courseViewModel.initializeExoPlayer(context)
    Scaffold(
        bottomBar = {if (viewState.isAuthenticated)
            NavigationBar(
                containerColor = MagentaHaze,
                contentColor = Color.White
            ) {

                items.forEachIndexed { index, item -> 
                    NavigationBarItem(
                        selected = viewState.selectedItemIndex == index,
                        onClick = { courseViewModel.changeItemIndex(index)
                                    item.onClickAction.invoke()},
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
                colors = TopAppBarColors(
                    containerColor = MagentaHaze,
                    navigationIconContentColor = Color.White,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White,
                    scrolledContainerColor = Color.White
                ),
                title = {
                    if (viewState.selectedItemIndex == 1) { // Check if the selected item is the Search screen
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = {
                                searchQuery = it
                                courseViewModel.updateSearchQuery(it.text)
                            },
                            placeholder = { Text("Search courses") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                            singleLine = true,
                            leadingIcon = {
                                Icon(imageVector = Icons.Filled.Search, contentDescription = "Search", modifier = Modifier.clickable {
                                    courseViewModel.searchCoursesByTagsOrName(viewState.searchQuery)
                                    navController.navigate(SearchTagScreen)
                                })
                            }
                        )
                    } else if (!viewState.lookingAtDetails){
                    Text(text = items[viewState.selectedItemIndex].title)
                }

                        },
                navigationIcon = {
                    if (viewState.lookingAtDetails){
                        IconButton(onClick = {
                            navController.navigate(CoursesScreen)
                            courseViewModel.changeLookingAtDetails(false)
                            courseViewModel.changeItemIndex(0)
                            courseViewModel.stopVideoPlayer()
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Go back"
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { courseViewModel.loadCoursesDto()}) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                },
            )
        }
    ) {paddingValues ->
        NavHost(navController = navController, startDestination = if (!viewState.isAuthenticated) LoginScreen else CoursesScreen) {
            composable<LoginScreen> {
                LoginScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    viewState = viewState,
                    onLoginClick = { email, password ->
                    courseViewModel.login(email, password)
                    navController.navigate(CoursesScreen)
                },
                    onRegisterClick = {
                        navController.navigate(RegistrationScreen)
                    })
            }
            composable<CoursesScreen>{
                CoursesScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    viewState = viewState,
                    onCourseClick = {course ->
                        courseViewModel.loadCourse(courseId = course.code)
                    navController.navigate(CourseDetailsScreen)
                courseViewModel.changeLookingAtDetails(true)
                })
            }
            composable<CourseDetailsScreen> {
                CourseDetailsScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    viewState = viewState,
                    onSignUpClick = {code ->
                        courseViewModel.enrollToCourse(code)},
                    studentViewState = studentViewState,
                    context = context)
            }
            composable<SearchScreen> {
                SearchScreen(
                   modifier = Modifier
                       .fillMaxSize()
                       .padding(paddingValues),
                    onCategoryClick =  {category ->
                        courseViewModel.searchCoursesByCategory(category)
                        navController.navigate(SearchTagScreen)
                        courseViewModel.changeLookingAtDetails(true)
                    },
                    viewState = viewState
                )
            }
            composable<SavedCoursesScreen> {
                SavedCoursesScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    onCourseClick = {course: Course ->
                        courseViewModel.loadCourseFromView(course)
                        courseViewModel.changeLookingAtDetails(true)
                        navController.navigate(CourseContentScreen)
                    },
                    viewState = viewState,
                    studentViewState = studentViewState,
                    onDeleteClick = {code : String ->
                        courseViewModel.removeCourse(code)
                    },
                    context = context
                )
            }
            composable<ProfileScreen> {
                ProfileScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    viewState = viewState,
                    onLogoutClick = {
                        navController.navigate(LoginScreen){
                            popUpTo(LoginScreen)
                        }
                        courseViewModel.logout()
                    },
                    studentViewState = studentViewState,
                    onIconUpdate = {uriString ->
                        courseViewModel.uploadImageFromUri(context, uriString)
                    }
                )
            }
            composable<SearchTagScreen>{
                SearchTagScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    viewState = viewState,
                    onCourseClick = {course ->
                        courseViewModel.loadCourse(courseId = course.code)
                        navController.navigate(CourseDetailsScreen)
                        courseViewModel.changeLookingAtDetails(true)
                    })
            }
            composable<CourseContentScreen> {
                CourseContentScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    viewState = viewState,
                    onLectureClick = { url, lectureId ->
                        courseViewModel.setCurrentVideoUrl(url, lectureId)
                    },
                    viewModel = courseViewModel,
                    onIconClick = {lectureId, isCompleted ->
                        courseViewModel.changeLectureCompletion(lectureId, isCompleted)
                    },
                    studentViewState = studentViewState
                )
            }
            composable<RegistrationScreen> {
                RegistrationScreen(
                    onRegisterClick = {
                        courseViewModel.registerStudent(it)
                        navController.navigate(CoursesScreen)
                    },
                    onBackClick = {
                        navController.popBackStack()
                    },
                    viewState = viewState
                )
            }

        }
    }


}