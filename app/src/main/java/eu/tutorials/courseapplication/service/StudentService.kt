package eu.tutorials.courseapplication.service

import eu.tutorials.courseapplication.Course
import eu.tutorials.courseapplication.EnrolledCourse
import eu.tutorials.courseapplication.Student
import okhttp3.MultipartBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import java.io.File

private val studentRetrofit = Retrofit.Builder().baseUrl("http://10.0.2.2:9000/student-service/").addConverterFactory(
    GsonConverterFactory.create()).build()

val studentService = studentRetrofit.create(StudentService::class.java)



interface StudentService{
    @POST("auth/authenticate")
    suspend fun authenticateStudent(@Body authRequest: AuthRequest): AutResponse

    @POST("auth/register")
    suspend fun registerStudent(@Body registerRequest : RegisterRequest): AutResponse

    @POST("auth/refresh-token")
    suspend fun refreshToken(@Body refreshToken: RefreshRequest): AutResponse

    @GET("students/{id}")
    suspend fun getStudentData(@Path("id") id : Long,
                               @Header("Authorization") token: String): Student

    @POST("students/addLectureToCompleted")
    suspend fun markLectureAsCompleted(@Query("studentId") studentId : Long,
                                       @Query("courseId") courseId: String,
                                       @Query("lectureId") lectureId:String,
                                       @Header("Authorization") token: String) : Student

    @DELETE("students/removeLectureFromCompleted")
    suspend fun markLectureAsUncompleted(@Query("studentId") studentId : Long,
                                       @Query("courseId") courseId: String,
                                       @Query("lectureId") lectureId:String,
                                       @Header("Authorization") token: String) : Student

    @Multipart
    @POST("students/update")
    suspend fun uploadProfileImage(
        @Query("id") id: Long,
        @Query("containerName") containerName: String,
        @Part file: MultipartBody.Part,
        @Header("Authorization") token: String
    ): Student



}

data class RegisterRequest(
    val firstname: String,
    val lastname : String,
    val email: String,
    val password: String
)

data class AuthRequest(
    val email: String,
    val password: String
)

data class AutResponse(
    val token: String,
    val refreshToken: String
)

data class RefreshRequest(
    val refreshToken: String
)