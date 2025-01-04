package eu.tutorials.courseapplication

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import eu.tutorials.courseapplication.service.studentService
import kotlinx.coroutines.launch
import eu.tutorials.courseapplication.service.AuthRequest
import eu.tutorials.courseapplication.service.courseService
import com.auth0.android.jwt.JWT
import eu.tutorials.courseapplication.service.AutResponse
import eu.tutorials.courseapplication.service.RefreshRequest
import eu.tutorials.courseapplication.service.RegisterRequest
import eu.tutorials.courseapplication.util.TokenManager
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream


class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val appContext: Context = application.applicationContext

    private val _coursesState = mutableStateOf(CoursesState())
    val coursesState:State<CoursesState> = _coursesState

    private val _studentDetails = mutableStateOf(Student(id = 0, firstName = "placeholder", lastName = "placeholder", email = "placeholder", status = Status.ACTIVE, enrolledCourses = emptyList(), profileImageUrl = "https://coursesapp.blob.core.windows.net/student-profile-image-container/BlankProfile.png"))
    val studentDetails:State<Student> = _studentDetails

    private val tokenManager = TokenManager(appContext)

    private val _authToken = mutableStateOf<String?>(null)

    private val _refreshToken = mutableStateOf<String?>(null)

    init {
        _authToken.value = tokenManager.getToken()
        _refreshToken.value = tokenManager.getRefreshToken()
        checkIsLoggedIn()
        if (coursesState.value.isAuthenticated){
            refreshAllData(_refreshToken.value.toString())
        }

    }

    private fun checkIsLoggedIn(){
        if (tokenManager.isTokenAvailable()){
            _coursesState.value = _coursesState.value.copy(isAuthenticated = true)
        }
    }


    private var _exoPlayer: ExoPlayer? = null
    val exoPlayer: ExoPlayer?
        get() = _exoPlayer



    fun initializeExoPlayer(context: Context) {
        if (_exoPlayer == null) {
            _exoPlayer = ExoPlayer.Builder(context).build().apply {
                addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(state: Int) {
                        if (state == Player.STATE_ENDED) {
                            markVideoAsWatched()
                        }
                    }

                })
            }
        }
    }

    private fun markVideoAsWatched() {
        if (!_studentDetails.value.enrolledCourses.find { it.courseId == coursesState.value.courseDetails.code }!!.completedLecturesId.contains(_coursesState.value.playVideoLectureId)){
            viewModelScope.launch {
                try{
                    var token = "Bearer ${_authToken.value}"
                    token = checkAndRefreshToken(token)
                    val updatedStudent:Student = studentService.markLectureAsCompleted(studentDetails.value.id, coursesState.value.courseDetails.code, coursesState.value.playVideoLectureId, token)
                    _studentDetails.value = updatedStudent
                }catch (e : Exception){
                    _coursesState.value = _coursesState.value.copy(
                        error = "LoginError, ${e.message}"
                    )
                }

            }
        }
    }

    fun changeLectureCompletion(lectureId: String, isCompleted:Boolean){
        if (!_coursesState.value.softLoading){
            _coursesState.value = _coursesState.value.copy(softLoading = true)
            if (!isCompleted){
                viewModelScope.launch {
                    try {
                        var token = "Bearer ${_authToken.value}"
                        token = checkAndRefreshToken(token)
                        val updatedStudent:Student = studentService.markLectureAsCompleted(courseId = coursesState.value.courseDetails.code, studentId = studentDetails.value.id, lectureId = lectureId, token = token)
                        println("DUPA")
                        _studentDetails.value = updatedStudent
                        _coursesState.value = _coursesState.value.copy(softLoading = false)
                    }catch (e : Exception){
                        _coursesState.value = _coursesState.value.copy(
                            error = "LoginError, ${e.message}",
                            softLoading = false
                        )
                    }
                }
            }else{
                viewModelScope.launch {
                    try {
                        var token = "Bearer ${_authToken.value}"
                        if (isTokenExpired()){
                            println("PRZED"+_authToken.value.toString())
                            val response = refreshToken(_refreshToken.value.toString())
                            _refreshToken.value = response.refreshToken
                            _authToken.value = response.token
                            token = "Bearer ${response.token}"
                            println("PO"+_authToken.value.toString())
                        }
                        println("DUPA2")
                        val updatedStudent:Student = studentService.markLectureAsUncompleted(courseId = coursesState.value.courseDetails.code, studentId = studentDetails.value.id, lectureId = lectureId, token = token)

                        _coursesState.value = _coursesState.value.copy(softLoading = false)
                        _studentDetails.value = updatedStudent
                    }catch (e : Exception){
                        _coursesState.value = _coursesState.value.copy(
                            error = "LoginError, ${e.message}",
                            softLoading = false
                        )
                    }

                }
            }
        }
    }

    private suspend fun MainViewModel.checkAndRefreshToken(token: String): String {
        var token1 = token
        if (isTokenExpired()) {
            println("PRZED" + _authToken.value.toString())
            val response = refreshToken(_refreshToken.value.toString())
            _refreshToken.value = response.refreshToken
            _authToken.value = response.token
            token1 = "Bearer ${response.token}"
            println("PO" + _authToken.value.toString())
        }
        return token1
    }

    fun enrollToCourse(code:String){
        if (!_coursesState.value.softLoading){
            viewModelScope.launch {
                try {
                    var token = "Bearer ${_authToken.value}"
                    token = checkAndRefreshToken(token)
                    _coursesState.value = _coursesState.value.copy(softLoading = true)
                    val course:Course = courseService.addStudentToCourse(code,studentDetails.value.id,token)
                    val newEnrolledCoursesList = studentDetails.value.enrolledCourses
                    val mutableList = newEnrolledCoursesList.toMutableList()
                    mutableList.add(EnrolledCourse(courseId = course.code, emptyList()))
                    val newSavedCoursesList = _coursesState.value.savedCourses
                    val mutableList2 = newSavedCoursesList.toMutableList()
                    mutableList2.add(course)
                    _studentDetails.value = _studentDetails.value.copy(enrolledCourses = mutableList)
                    _coursesState.value = _coursesState.value.copy(softLoading = false, savedCourses = mutableList2)
                }catch (e:Exception){
                    _coursesState.value = _coursesState.value.copy(error = e.message, softLoading = false)
                }
            }
        }
    }

    fun removeCourse(code:String){
        viewModelScope.launch {
            try {
                var token = "Bearer ${_authToken.value}"
                token = checkAndRefreshToken(token)
                _coursesState.value = _coursesState.value.copy(loading = true)
                courseService.removeCourse(code, studentDetails.value.email, token)
                val newEnrolledCourses: List<EnrolledCourse> = studentDetails.value.enrolledCourses
                val mutableCourses = newEnrolledCourses.toMutableList()
                mutableCourses.removeIf{it.courseId == code}
                val newSavedCoursesList = coursesState.value.savedCourses
                val mutableList2 = newSavedCoursesList.toMutableList()
                mutableList2.removeIf{it.code == code}
                if (mutableList2.isNotEmpty()){
                    _coursesState.value = _coursesState.value.copy(savedCourses = mutableList2)
                }else{
                    _coursesState.value = _coursesState.value.copy(savedCourses = emptyList())
                }
                if (mutableCourses.isNotEmpty()){
                    _studentDetails.value = studentDetails.value.copy(enrolledCourses = mutableCourses)
                }else{
                    _studentDetails.value = studentDetails.value.copy(enrolledCourses = emptyList())
                }
                _coursesState.value = _coursesState.value.copy(loading = false)
            }catch (e:Exception){
                _coursesState.value = _coursesState.value.copy(loading = false, error = e.message)
            }
        }
    }

    private fun setVideoUrl(videoUrl: String) {
        _exoPlayer?.let { player ->
            val mediaItem = MediaItem.fromUri(videoUrl)
            player.setMediaItem(mediaItem)
            player.prepare()
            player.playWhenReady = true
        }
    }

    override fun onCleared() {
        super.onCleared()
        _exoPlayer?.release()
        _exoPlayer = null
    }

    fun login(email:String, password:String){
        validateStudent(email, password)
    }

    private fun isTokenExpired(): Boolean {
        val token = _authToken.value ?: return true
        return try {
            val jwt = JWT(token)
            println("TOKEN WYGASNIE: " + jwt.expiresAt?.time)
            println("AKTUALNY CZAS: " + System.currentTimeMillis())
            val expiresAt = jwt.expiresAt ?: return true
            val currentTime = System.currentTimeMillis()
            println(expiresAt.time <= currentTime)
            expiresAt.time <= currentTime
        } catch (e: Exception) {
            true
        }
    }

    private suspend fun loadStudentDataById(id: Long) {
        var token = "Bearer ${_authToken.value}"
        token = checkAndRefreshToken(token)
        val student : Student = studentService.getStudentData(id, token)
        _studentDetails.value = student
        val enrolledCourses:List<String> = studentDetails.value.enrolledCourses.map { it.courseId }
        if (enrolledCourses.isNotEmpty()){
            val savedCourses :List<Course> = courseService.getSavedCourses(enrolledCourses, token)
            _coursesState.value = _coursesState.value.copy(savedCourses= savedCourses)
        }else{
            _coursesState.value = _coursesState.value.copy(savedCourses= emptyList())
        }
    }

    private fun loadStudentData(){
        viewModelScope.launch {
            try {
                var token = "Bearer ${_authToken.value}"
                token = checkAndRefreshToken(token)
                _coursesState.value = _coursesState.value.copy(loading = true)
                val jwt = JWT(_authToken.value!!)
                val id: Long = jwt.getClaim("id").asLong()!!
                val student : Student = studentService.getStudentData(id, token)

                _studentDetails.value = student

                val enrolledCourses:List<String> = studentDetails.value.enrolledCourses.map { it.courseId }
                if (enrolledCourses.isNotEmpty()){
                    val savedCourses :List<Course> = courseService.getSavedCourses(enrolledCourses, token)
                    _coursesState.value = _coursesState.value.copy(savedCourses= savedCourses)
                }

                _coursesState.value = _coursesState.value.copy(loading = false, isAuthenticated = true)
            } catch (e: Exception){
                _coursesState.value = _coursesState.value.copy(
                    loading = false,
                    error = "LoginError, ${e.message}"
                )
            }
        }
    }

    fun registerStudent(registerResponse : RegisterRequest){
        viewModelScope.launch {
            try {
                println(registerResponse)
                tokenManager.clearToken()
                _coursesState.value = _coursesState.value.copy(loading = true)
                val response = studentService.registerStudent(registerResponse)
                _authToken.value = response.token
                _refreshToken.value = response.refreshToken
                val tokenResponse = response.token
                val refreshResponse = response.refreshToken
                tokenManager.saveToken(tokenResponse, refreshResponse)
                val jwt = JWT(_authToken.value!!)
                val id: Long = jwt.getClaim("id").asLong()!!
                loadStudentDataById(id)
                loadCoursesDto()
                _coursesState.value = _coursesState.value.copy(loading = false, isAuthenticated = true)
            } catch (e: Exception){
                _coursesState.value = _coursesState.value.copy(loading = false, error = e.message)
            }
        }
    }

    private fun validateStudent(email: String, password: String){
        viewModelScope.launch {
            try {
                _coursesState.value = _coursesState.value.copy(loading = true)
                val authRequest = AuthRequest(email, password)
                val response = studentService.authenticateStudent(authRequest)
                _authToken.value = response.token
                _refreshToken.value = response.refreshToken
                val tokenResponse = response.token
                val refreshResponse = response.refreshToken
                tokenManager.saveToken(tokenResponse, refreshResponse)
                val jwt = JWT(_authToken.value!!)
                val id: Long = jwt.getClaim("id").asLong()!!
                loadStudentDataById(id)
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

    private fun refreshAllData(refreshToken: String){
        viewModelScope.launch {
            try {
                _coursesState.value = _coursesState.value.copy(loading = true)

                val response = refreshToken(refreshToken)
                val token = "Bearer ${response.token}"
                val courseDTO = courseService.getAllCoursesDto(token)
                _coursesState.value = _coursesState.value.copy(list = courseDTO)
                val jwt = JWT(response.token)
                val id: Long = jwt.getClaim("id").asLong()!!
                val student : Student = studentService.getStudentData(id, token)
                _studentDetails.value = student
                val enrolledCourses:List<String> = studentDetails.value.enrolledCourses.map { it.courseId }
                if (enrolledCourses.isNotEmpty()){
                    val savedCourses :List<Course> = courseService.getSavedCourses(enrolledCourses, token)
                    _coursesState.value = _coursesState.value.copy(savedCourses= savedCourses)
                }
                _coursesState.value = _coursesState.value.copy(loading = false)

            }catch (e : Exception){
                _coursesState.value = _coursesState.value.copy(
                    loading = false,
                    error = "LoginError, ${e.message}"
                )
            }
        }
    }

    private suspend fun refreshToken(refreshToken: String): AutResponse {
        val refreshRequest = RefreshRequest(refreshToken)
        val response = studentService.refreshToken(refreshRequest)
        _authToken.value = response.token
        _refreshToken.value = response.refreshToken
        tokenManager.saveToken(response.token, response.refreshToken)
        println("response token:: "+response.token)
        println("refresh token:: "+response.refreshToken)
        println("token from manager: "+ tokenManager.getToken())
        println("refresh token from manager: "+ tokenManager.getRefreshToken())
        return response
    }

    fun loadCoursesDto(){
        fetchCoursesDto()
    }

    private fun fetchCoursesDto(){
        viewModelScope.launch{
            try{
                var token = "Bearer ${_authToken.value}"
                token = checkAndRefreshToken(token)
                _coursesState.value = _coursesState.value.copy(loading = true)
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
                var token = "Bearer ${_authToken.value}"
                token = checkAndRefreshToken(token)
                _coursesState.value = _coursesState.value.copy(loading = true)

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

    fun searchCoursesByTagsOrName(searchQuery: String) {
        viewModelScope.launch{
            try{
                var token = "Bearer ${_authToken.value}"
                token = checkAndRefreshToken(token)
                _coursesState.value = _coursesState.value.copy(loading = true)
                val response = courseService.getCoursesByTagsOrName(searchQuery, token)
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
                var token = "Bearer ${_authToken.value}"
                token = checkAndRefreshToken(token)
                _coursesState.value = _coursesState.value.copy(loading = true)
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

    fun stopVideoPlayer(){
        _exoPlayer?.stop()
    }

    fun logout() {
        tokenManager.clearToken()
        _authToken.value = null
        _refreshToken.value = null
        _coursesState.value = _coursesState.value.copy(isAuthenticated = false, lookingAtDetails = false, selectedItemIndex = 0)
        _studentDetails.value = studentDetails.value.copy(id = 0, firstName = "placeholder", lastName = "placeholder", email = "placeholder", status = Status.ACTIVE, enrolledCourses = emptyList(), profileImageUrl = "https://coursesapp.blob.core.windows.net/student-profile-image-container/BlankProfile.png")
    }

    fun updateSearchQuery(text: String) {
        _coursesState.value = _coursesState.value.copy(searchQuery = text)
    }

    fun loadCourseFromView(course: Course) {
        _coursesState.value = _coursesState.value.copy(loading = false, courseDetails = course)
    }

    fun setCurrentVideoUrl(url: String, lectureId:String) {
        _coursesState.value = _coursesState.value.copy(playVideoUrl = url, playVideoLectureId = lectureId)
        setVideoUrl(url)
    }

    fun turnOfLookingAtDetails(){
        _coursesState.value = _coursesState.value.copy(lookingAtDetails = false)
    }

    private fun uriToFile(context: Context, uriString: String): File? {
        val uri = Uri.parse(uriString)
        return try {
            val tempFile = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(tempFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            tempFile
        } catch (e: Exception) {
            _coursesState.value = _coursesState.value.copy(error = e.message)
            null
        }
    }



    fun uploadImageFromUri(context: Context, uriString: String) {
        viewModelScope.launch {
            val file = uriToFile(context, uriString)
            if (file != null) {
                val requestFile: RequestBody =
                    file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                val body : MultipartBody.Part = MultipartBody.Part.createFormData("file", file.name, requestFile)
                uploadImage(body)
            } else {
                _coursesState.value = _coursesState.value.copy(
                    error = "Failed to convert URI to File"
                )
            }
        }
    }

    private fun uploadImage(file : MultipartBody.Part){
        viewModelScope.launch {
            try {
                _coursesState.value = _coursesState.value.copy(loading = true)
                var token = "Bearer ${_authToken.value}"
                token = checkAndRefreshToken(token)
                val newStudent = studentService.uploadProfileImage(studentDetails.value.id, "student-profile-image-container", file, token)
                _studentDetails.value = newStudent
                _coursesState.value = _coursesState.value.copy(loading = false)
            }catch (e:Exception){
                _coursesState.value = _coursesState.value.copy(error = e.message, loading = false)
            }
        }

    }




    data class CoursesState(val loading: Boolean = false,
                            val softLoading: Boolean = false,
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
                                author = "placeholder",
                                participantsNumber = 0,
                                participantsLimit = 0,
                                participants = emptyList(),
                                category = Category.MUSIC,
                                tags = emptyList(),
                                imageUrl = "placeholder",
                                sections = emptyList(),
                                descriptionDetails = "placeholder",
                                descriptionList = emptyList()
                            ),
                            val savedCourses: List<Course> = emptyList(),
                            val searchedCourses: List<CourseDto> = emptyList(),
                            val searchQuery : String = "",
                            val playVideoUrl: String = "",
                            val playVideoLectureId: String =""
        )
}