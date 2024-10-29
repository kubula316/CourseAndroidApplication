package eu.tutorials.courseapplication

import android.os.Build
import android.os.Parcelable
import androidx.annotation.RequiresApi
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Serializable
data class Course(val code : String,
                  val status : Status,
                  val name : String,
                  val description : String,
                  val startDate : String,
                  val endDate : String,
                  val participantsLimit: Int,
                  val participantsNumber: Int,
                  val participants: List<CourseMember>,
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
