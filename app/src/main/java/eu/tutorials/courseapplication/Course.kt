package eu.tutorials.courseapplication

import kotlinx.serialization.Serializable

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

data class CourseResponse(val categories : List<Course>)

enum class Status {
    ACTIVE,
    INTACTIVE
}

@Serializable
data class CourseMember(
    val email : String,
    val dateOfSign : String
)

enum class Category {
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
