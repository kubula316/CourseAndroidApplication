package eu.tutorials.courseapplication.service

import eu.tutorials.courseapplication.Course
import eu.tutorials.courseapplication.CourseDto
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

private val courseRetrofit = Retrofit.Builder().baseUrl("http://10.0.2.2:9000/course-service/").addConverterFactory(
    GsonConverterFactory.create()).build()

val courseService = courseRetrofit.create(CourseService::class.java)

interface CourseService{
    @GET("courses/projections")
    suspend fun getAllCoursesDto(@Header("Authorization") token: String): List<CourseDto>

    @GET("courses/{courseId}")
    suspend fun getCourse(
        @Path("courseId") courseId: String,
        @Header("Authorization") token: String
    ): Course

    @GET("courses/savedCourses")
    suspend fun getSavedCourses(
        @Query("savedList") savedList : List<String>,
        @Header("Authorization") token: String
    ) : List<Course>

    @GET("courses/search")
    suspend fun getCoursesByTagsOrName(@Query("searchTerm") searchTerm : String ,@Header("Authorization") token: String): List<CourseDto>

    @GET("courses/search/category")
    suspend fun getCoursesByCategory(@Query("category") searchTerm : String ,@Header("Authorization") token: String): List<CourseDto>

}