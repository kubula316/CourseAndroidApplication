package eu.tutorials.courseapplication.service

import eu.tutorials.courseapplication.Course
import eu.tutorials.courseapplication.Student
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

private val studentRetrofit = Retrofit.Builder().baseUrl("http://10.0.2.2:9000/student-service/").addConverterFactory(
    GsonConverterFactory.create()).build()

val studentService = studentRetrofit.create(StudentService::class.java)



interface StudentService{
    @POST("auth/authenticate")
    suspend fun authenticateStudent(@Body authRequest: AuthRequest): AutResponse

    @GET("students/{id}")
    suspend fun getStudentData(@Path("id") id : Long,
                               @Header("Authorization") token: String): Student
}

data class AuthRequest(
    val email: String,
    val password: String
)

data class AutResponse(
    val token: String
)