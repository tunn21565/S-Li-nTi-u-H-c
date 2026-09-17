package com.example.data.repository

import com.example.data.db.AppDao
import com.example.data.model.AttendanceRecord
import com.example.data.model.AttendanceStatus
import com.example.data.model.BehaviorLog
import com.example.data.model.BehaviorType
import com.example.data.model.SchoolRequest
import com.example.data.model.SemesterResult
import com.example.data.model.Student
import com.example.data.model.TermType
import kotlinx.coroutines.flow.Flow

class ClassRepository(private val dao: AppDao) {

    val allStudents: Flow<List<Student>> = dao.getAllStudents()

    fun getStudent(id: Int): Flow<Student?> = dao.getStudentById(id)

    suspend fun updateStudent(student: Student) {
        dao.updateStudent(student)
    }

    // Attendance
    fun getAttendanceForDate(date: String): Flow<List<AttendanceRecord>> =
        dao.getAttendanceForDate(date)

    fun getAttendanceForDateRange(startDate: String, endDate: String): Flow<List<AttendanceRecord>> =
        dao.getAttendanceForDateRange(startDate, endDate)

    val allAttendance: Flow<List<AttendanceRecord>> = dao.getAllAttendance()

    suspend fun syncOfficialStudents(students: List<Student>) {
        dao.insertStudents(students)
    }

    fun getAttendanceForStudent(studentId: Int): Flow<List<AttendanceRecord>> =
        dao.getAttendanceForStudent(studentId)

    suspend fun setAttendance(studentId: Int, date: String, status: AttendanceStatus, note: String = "") {
        dao.upsertAttendance(
            AttendanceRecord(
                studentId = studentId,
                date = date,
                status = status,
                note = note
            )
        )
    }

    suspend fun markAllPresent(date: String, studentIds: List<Int>) {
        val records = studentIds.map { id ->
            AttendanceRecord(
                studentId = id,
                date = date,
                status = AttendanceStatus.DI_HOC,
                note = ""
            )
        }
        dao.insertAttendances(records)
    }

    // Behavior logs
    val allBehaviorLogs: Flow<List<BehaviorLog>> = dao.getAllBehaviorLogs()

    fun getBehaviorLogsForStudent(studentId: Int): Flow<List<BehaviorLog>> =
        dao.getBehaviorLogsForStudent(studentId)

    suspend fun addBehaviorLog(
        studentId: Int,
        date: String,
        type: BehaviorType,
        category: String,
        detail: String
    ): Long {
        return dao.insertBehaviorLog(
            BehaviorLog(
                studentId = studentId,
                date = date,
                type = type,
                category = category,
                detail = detail
            )
        )
    }

    suspend fun deleteBehaviorLog(id: Long) {
        dao.deleteBehaviorLog(id)
    }

    // School requests
    val allSchoolRequests: Flow<List<SchoolRequest>> = dao.getAllSchoolRequests()

    fun getSchoolRequestsForStudent(studentId: Int): Flow<List<SchoolRequest>> =
        dao.getSchoolRequestsForStudent(studentId)

    suspend fun addSchoolRequest(
        studentId: Int,
        title: String,
        content: String,
        deadline: String
    ): Long {
        return dao.insertSchoolRequest(
            SchoolRequest(
                studentId = studentId,
                title = title,
                content = content,
                deadline = deadline,
                status = "Chờ gửi Zalo"
            )
        )
    }

    suspend fun updateSchoolRequest(request: SchoolRequest) {
        dao.updateSchoolRequest(request)
    }

    suspend fun deleteSchoolRequest(id: Long) {
        dao.deleteSchoolRequest(id)
    }

    // Semester Results
    fun getResultsForTerm(term: TermType): Flow<List<SemesterResult>> =
        dao.getResultsForTerm(term)

    fun getResultsForStudent(studentId: Int): Flow<List<SemesterResult>> =
        dao.getResultsForStudent(studentId)

    suspend fun upsertSemesterResult(result: SemesterResult) {
        dao.upsertSemesterResult(result)
    }
}
