package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AttendanceRecord
import com.example.data.model.BehaviorLog
import com.example.data.model.SchoolRequest
import com.example.data.model.SemesterResult
import com.example.data.model.Student
import com.example.data.model.TermType
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // --- Students ---
    @Query("SELECT * FROM students ORDER BY stt ASC")
    fun getAllStudents(): Flow<List<Student>>

    @Query("SELECT * FROM students WHERE id = :id")
    fun getStudentById(id: Int): Flow<Student?>

    @Query("SELECT COUNT(*) FROM students")
    suspend fun getStudentCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudents(students: List<Student>)

    @Update
    suspend fun updateStudent(student: Student)

    // --- Attendance ---
    @Query("SELECT * FROM attendance_records WHERE date = :date")
    fun getAttendanceForDate(date: String): Flow<List<AttendanceRecord>>

    @Query("SELECT * FROM attendance_records WHERE date >= :startDate AND date <= :endDate")
    fun getAttendanceForDateRange(startDate: String, endDate: String): Flow<List<AttendanceRecord>>

    @Query("SELECT * FROM attendance_records")
    fun getAllAttendance(): Flow<List<AttendanceRecord>>

    @Query("SELECT * FROM attendance_records WHERE studentId = :studentId ORDER BY date DESC")
    fun getAttendanceForStudent(studentId: Int): Flow<List<AttendanceRecord>>

    @Query("SELECT COUNT(*) FROM attendance_records WHERE date = :date")
    suspend fun getAttendanceCountForDate(date: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAttendance(record: AttendanceRecord)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendances(records: List<AttendanceRecord>)

    // --- Behavior Logs ---
    @Query("SELECT * FROM behavior_logs ORDER BY timestamp DESC")
    fun getAllBehaviorLogs(): Flow<List<BehaviorLog>>

    @Query("SELECT * FROM behavior_logs WHERE studentId = :studentId ORDER BY timestamp DESC")
    fun getBehaviorLogsForStudent(studentId: Int): Flow<List<BehaviorLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBehaviorLog(log: BehaviorLog): Long

    @Query("DELETE FROM behavior_logs WHERE id = :id")
    suspend fun deleteBehaviorLog(id: Long)

    // --- School Requests ---
    @Query("SELECT * FROM school_requests ORDER BY createdAt DESC")
    fun getAllSchoolRequests(): Flow<List<SchoolRequest>>

    @Query("SELECT * FROM school_requests WHERE studentId = :studentId ORDER BY createdAt DESC")
    fun getSchoolRequestsForStudent(studentId: Int): Flow<List<SchoolRequest>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchoolRequest(request: SchoolRequest): Long

    @Update
    suspend fun updateSchoolRequest(request: SchoolRequest)

    @Query("DELETE FROM school_requests WHERE id = :id")
    suspend fun deleteSchoolRequest(id: Long)

    // --- Semester Results ---
    @Query("SELECT * FROM semester_results WHERE term = :term")
    fun getResultsForTerm(term: TermType): Flow<List<SemesterResult>>

    @Query("SELECT * FROM semester_results WHERE studentId = :studentId")
    fun getResultsForStudent(studentId: Int): Flow<List<SemesterResult>>

    @Query("SELECT * FROM semester_results WHERE studentId = :studentId AND term = :term LIMIT 1")
    fun getResultForStudentAndTerm(studentId: Int, term: TermType): Flow<SemesterResult?>

    @Query("SELECT COUNT(*) FROM semester_results")
    suspend fun getSemesterResultCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSemesterResult(result: SemesterResult)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSemesterResults(results: List<SemesterResult>)
}
