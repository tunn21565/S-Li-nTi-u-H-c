package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "students")
data class Student(
    @PrimaryKey val id: Int,
    val stt: Int,
    val fullName: String,
    val gender: String, // "Nam" or "Nữ"
    val dob: String, // e.g. "18/12/2016"
    val parentPhone: String,
    val parentName: String,
    val notes: String = "",
    val fatherName: String = "",
    val fatherJob: String = "",
    val motherName: String = "",
    val motherJob: String = "",
    val address: String = "",
    val birthPlace: String = "",
    val citizenId: String = "",
    val photoUri: String = "", // Đường dẫn ảnh thẻ / dán hình
    val luuBan: String = "",
    val chuyenDen: String = "",
    val policyCategory: String = ""
)

enum class AttendanceStatus(val label: String, val description: String) {
    DI_HOC("Đi học", "Có mặt đúng giờ"),
    DI_TRE("Đi trễ", "Đến lớp muộn"),
    NGHI_CO_PHEP("Có phép", "Nghỉ học có phép"),
    NGHI_KHONG_PHEP("Không phép", "Nghỉ không phép")
}

@Entity(
    tableName = "attendance_records",
    indices = [Index(value = ["studentId", "date"], unique = true)]
)
data class AttendanceRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: Int,
    val date: String, // "YYYY-MM-DD"
    val status: AttendanceStatus,
    val note: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)

enum class BehaviorType(val label: String) {
    PRAISE("Khen ngợi / Tích cực"),
    REMIND("Nhắc nhở / Vi phạm")
}

@Entity(tableName = "behavior_logs")
data class BehaviorLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: Int,
    val date: String, // "YYYY-MM-DD"
    val type: BehaviorType,
    val category: String,
    val detail: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "school_requests")
data class SchoolRequest(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: Int,
    val title: String,
    val content: String,
    val deadline: String = "",
    val isSent: Boolean = false,
    val sentDate: String = "",
    val status: String = "Chờ gửi Zalo",
    val createdAt: Long = System.currentTimeMillis()
)

enum class TermType(val code: String, val title: String) {
    GHKI("GHKI", "Giữa Học Kỳ I"),
    HKI("HKI", "Cuối Học Kỳ I"),
    GHKII("GHKII", "Giữa Học Kỳ II"),
    HKII("HKII", "Cuối Học Kỳ II")
}

@Entity(
    tableName = "semester_results",
    indices = [Index(value = ["studentId", "term"], unique = true)]
)
data class SemesterResult(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: Int,
    val term: TermType,
    val mathScore: Double = 9.0,
    val vietnameseScore: Double = 8.5,
    val englishScore: Double = 8.5,
    val scienceScore: Double = 9.0,
    val historyGeoScore: Double = 8.5,
    val informaticsScore: Double = 9.0,
    val conduct: String = "Tốt", // Tốt / Đạt / Cần cố gắng
    val title: String = "Học sinh Tiêu biểu", // Học sinh Xuất sắc / Học sinh Tiêu biểu / Hoàn thành tốt
    val teacherNote: String = "Chăm ngoan, tích cực phát biểu xây dựng bài.",
    val updatedAt: Long = System.currentTimeMillis()
) {
    val averageScore: Double
        get() = ((mathScore + vietnameseScore + englishScore + scienceScore + historyGeoScore + informaticsScore) / 6.0 * 10).toInt() / 10.0
}
