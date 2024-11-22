package eu.tutorials.courseapplication

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import eu.tutorials.courseapplication.service.studentService
import kotlinx.coroutines.launch
import eu.tutorials.courseapplication.service.AuthRequest
import eu.tutorials.courseapplication.service.courseService
import com.auth0.android.jwt.JWT


class MainViewModel : ViewModel() {
    private val _coursesState = mutableStateOf(CoursesState())
    val coursesState:State<CoursesState> = _coursesState

    private val _authToken = mutableStateOf<String?>(null)
    val authToken: State<String?> = _authToken

    fun login(email:String, password:String){
        validateStudent(email, password)
    }


    private suspend fun loadStudentData(id: Long) {
        val token = "Bearer ${_authToken.value}"
        val student : Student = studentService.getStudentData(id, token)
        _coursesState.value = _coursesState.value.copy(studentDetails = student)
        val savedCourses :List<Course> = courseService.getSavedCourses(_coursesState.value.studentDetails.enrolledCourses, token)
        _coursesState.value = _coursesState.value.copy(savedCourses= savedCourses)
    }

    private fun validateStudent(email: String, password: String){
        viewModelScope.launch {
            try {
                _coursesState.value = _coursesState.value.copy(loading = true)
                val authRequest = AuthRequest(email, password)

                val response = studentService.authenticateStudent(authRequest)

                _authToken.value = response.token

                val jwt: JWT = JWT(_authToken.value!!)
                val id: Long = jwt.getClaim("id").asLong()!!


                loadStudentData(id)
                loadCoursesDto()
                _coursesState.value = _coursesState.value.copy(loading = false, isAuthenticated = true)

            } catch (e : Exception){
                _coursesState.value = _coursesState.value.copy(
                    loading = false,
                    error = "LoginError, ${e.message}"
                )
            }
        }
    }

    fun loadCoursesDto(){
        fetchCoursesDto()
    }

    private fun fetchCoursesDto(){
        viewModelScope.launch{
            try{
                val token = "Bearer ${_authToken.value}"
                _coursesState.value = _coursesState.value.copy(loading = true,)
                val response = courseService.getAllCoursesDto(token)
                _coursesState.value = _coursesState.value.copy(loading = false, list = response)

            }catch (e : Exception){
                _coursesState.value = _coursesState.value.copy(
                    loading = false,
                    error = "LoginError, ${e.message}"
                )
            }
        }
    }

    fun loadCourse(courseId : String){
        fetchCourse(courseId)
    }

    private fun fetchCourse(courseId: String) {
        viewModelScope.launch{
            try{
                val token = "Bearer ${_authToken.value}"
                _coursesState.value = _coursesState.value.copy(loading = true,)

                val response = courseService.getCourse(courseId, token)
                _coursesState.value = _coursesState.value.copy(loading = false, courseDetails = response)

            }catch (e : Exception){
                _coursesState.value = _coursesState.value.copy(
                    loading = false,
                    error = "LoginError, ${e.message}"
                )
            }
        }
    }

    fun searchCoursesByTagsOrName(searchQuerry: String) {
        viewModelScope.launch{
            try{
                val token = "Bearer ${_authToken.value}"
                _coursesState.value = _coursesState.value.copy(loading = true,)
                val response = courseService.getCoursesByTagsOrName(searchQuerry, token)
                _coursesState.value = _coursesState.value.copy(loading = false, searchedCourses = response)

            }catch (e : Exception){
                _coursesState.value = _coursesState.value.copy(
                    loading = false,
                    error = "LoginError, ${e.message}"
                )
            }
        }
    }

    fun searchCoursesByCategory(category: String) {
        viewModelScope.launch{
            try{
                val token = "Bearer ${_authToken.value}"
                _coursesState.value = _coursesState.value.copy(loading = true,)
                val response = courseService.getCoursesByCategory(category, token)
                _coursesState.value = _coursesState.value.copy(loading = false, searchedCourses = response)

            }catch (e : Exception){
                _coursesState.value = _coursesState.value.copy(
                    loading = false,
                    error = "LoginError, ${e.message}"
                )
            }
        }
    }

    fun changeItemIndex(index: Int) {
        _coursesState.value = _coursesState.value.copy(selectedItemIndex = index)
    }

    fun changeLookingAtDetails(boolean: Boolean){
        _coursesState.value = _coursesState.value.copy(lookingAtDetails = boolean)
    }

    fun logout() {
        _coursesState.value = _coursesState.value.copy(isAuthenticated = false, lookingAtDetails = false, selectedItemIndex = 0,
            studentDetails = Student(
            id = 0,
            firstName = "placeholder",
            lastName = "placeholder",
            email = "placeholder",
            status = Status.ACTIVE,
            enrolledCourses = emptyList(),
            profileImageUrl = "https://coursesapp.blob.core.windows.net/student-profile-image-container/BlankProfile.png"
        ))
        _authToken.value = ""
    }

    fun updateSearchQuery(text: String) {
        _coursesState.value = _coursesState.value.copy(searchQuerry = text)
    }

    fun loadCourseFromView(course: Course) {
        _coursesState.value = _coursesState.value.copy(loading = false, courseDetails = course)
    }

    fun setCurrentVideoUrl(url: String) {
        _coursesState.value = _coursesState.value.copy(playVideoUrl = url)
    }


    data class CoursesState(val loading: Boolean = false,
                            val list: List<CourseDto> = emptyList(),
                            val error:String? = null,
                            val selectedItemIndex: Int = 0,
                            val isAuthenticated: Boolean = false,
                            val lookingAtDetails: Boolean = false,
                            val courseDetails : Course = Course(
                                code = "placeholder",
                                status = Status.ACTIVE,
                                name = "placeholder",
                                description = "placeholder",
                                author = "placehodler",
                                startDate = "placeholder",
                                endDate = "placeholder",
                                participantsNumber = 0,
                                participantsLimit = 0,
                                participants = emptyList(),
                                category = Category.MUSIC,
                                tags = emptyList(),
                                imageUrl = "placeholder",
                                sections = emptyList()
                            ),
                            val studentDetails : Student = Student(
                                id = 0,
                                firstName = "placeholder",
                                lastName = "placeholder",
                                email = "placeholder",
                                status = Status.ACTIVE,
                                enrolledCourses = emptyList(),
                                profileImageUrl = "https://coursesapp.blob.core.windows.net/student-profile-image-container/BlankProfile.png"
                            ),
                            val savedCourses: List<Course> = emptyList(),
                            val searchedCourses: List<CourseDto> = emptyList(),
                            val searchQuerry : String = "",
                            val playVideoUrl: String = ""
        ){}
}