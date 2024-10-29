package eu.tutorials.courseapplication

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.tutorials.courseapplication.service.studentService
import kotlinx.coroutines.launch
import eu.tutorials.courseapplication.service.AuthRequest
import eu.tutorials.courseapplication.service.courseService

class MainViewModel : ViewModel() {
    private val _coursesState = mutableStateOf(CoursesState())
    val coursesState:State<CoursesState> = _coursesState

    private val _authToken = mutableStateOf<String?>(null)
    val authToken: State<String?> = _authToken

    fun login(email:String, password:String){
        validateStudent(email, password)
    }

    private fun validateStudent(email: String, password: String){
        viewModelScope.launch {
            try {
                _coursesState.value = _coursesState.value.copy(loading = true)
                val authRequest = AuthRequest(email, password)

                val response = studentService.authenticateStudent(authRequest)

                _authToken.value = response.token

                println(_authToken.value)
                loadCourses()
                _coursesState.value = _coursesState.value.copy(loading = false,)

            } catch (e : Exception){
                _coursesState.value = _coursesState.value.copy(
                    loading = false,
                    error = "LoginError, ${e.message}"
                )
            }
        }
    }
    fun loadCourses(){
        fetchCourses()
    }

    private fun fetchCourses(){
        viewModelScope.launch{
            try{
                val token = "Bearer ${_authToken.value}"
                _coursesState.value = _coursesState.value.copy(loading = true,)
                val response = courseService.getAllCourses(token)
                _coursesState.value = _coursesState.value.copy(loading = false, list = response)

            }catch (e : Exception){
                _coursesState.value = _coursesState.value.copy(
                    loading = false,
                    error = "LoginError, ${e.message}"
                )
            }
        }
    }

    data class CoursesState(val loading: Boolean = false, val list: List<Course> = emptyList(), val error:String? = null, ){}
}