package eu.tutorials.courseapplication.service

import eu.tutorials.courseapplication.Course
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header

private val courseRetrofit = Retrofit.Builder().baseUrl("http://10.0.2.2:9000/course-service/").addConverterFactory(
    GsonConverterFactory.create()).build()

val courseService = courseRetrofit.create(CourseService::class.java)

interface CourseService{
    @GET("courses")
    suspend fun getAllCourses(@Header("Authorization") token: String): List<Course>
}