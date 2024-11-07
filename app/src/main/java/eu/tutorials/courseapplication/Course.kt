package eu.tutorials.courseapplication

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
data class CourseDto(val code : String,
                  val name : String,
                  val description : String,
                  val author : String,
                  val participantsLimit: Int,
                  val participantsNumber: Int,
                  val imageUrl: String
    )

@Serializable
data class Course(val code : String,
                  val status : Status,
                  val name : String,
                  val description : String,
                  val author : String,
                  val startDate : String,
                  val endDate : String,
                  val participantsLimit: Int,
                  val participantsNumber: Int,
                  val participants: List<CourseMember>,
                  val category: Category,
                  val tags : List<String>,
                  val imageUrl: String
)

data class Student(
    val id : Long,
    val firstName: String,
    val lastName: String,
    val email: String,
    val status: Status,
    val enrolledCourses: List<String>,
    val profileImageUrl : String,
    )

data class CourseResponse(val categories : List<Course>)

@Parcelize
enum class Status : Parcelable {
    ACTIVE,
    INTACTIVE
}

@Parcelize
@Serializable
data class CourseMember(
    val email : String,
    val dateOfSign : String
) : Parcelable

@Parcelize
enum class Category : Parcelable {
    PROGRAMING,
    LANGUAGE,
    GAMEDEV,
    BUSINESS,
    MUSIC,
    FINANCE_AND_ACCOUNTING,
    PERSONAL_DEVELOPMENT,
    MARKETING,
    HEALTH_AND_FITNESS
}
