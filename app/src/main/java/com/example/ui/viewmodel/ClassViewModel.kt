package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.db.SeedData
import com.example.data.model.AttendanceRecord
import com.example.data.model.AttendanceStatus
import com.example.data.model.BehaviorLog
import com.example.data.model.BehaviorType
import com.example.data.model.SchoolRequest
import com.example.data.model.SemesterResult
import com.example.data.model.Student
import com.example.data.model.TermType
import com.example.data.repository.ClassRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class ClassViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ClassRepository

    init {
        val database = AppDatabase.getDatabase(application, viewModelScope)
        repository = ClassRepository(database.appDao())
    }

    val students: StateFlow<List<Student>> = repository.allStudents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val searchQuery = MutableStateFlow("")
    val genderFilter = MutableStateFlow("Tất cả") // "Tất cả", "Nam", "Nữ"

    val filteredStudents: StateFlow<List<Student>> = combine(students, searchQuery, genderFilter) { list, query, gender ->
        list.filter { student ->
            val matchQuery = query.isBlank() ||
                    student.fullName.contains(query, ignoreCase = true) ||
                    student.stt.toString() == query.trim() ||
                    student.parentPhone.contains(query)
            val matchGender = gender == "Tất cả" || student.gender == gender
            matchQuery && matchGender
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Attendance
    val selectedDate = MutableStateFlow(SeedData.getTodayString())

    val attendanceList: StateFlow<List<AttendanceRecord>> = selectedDate
        .flatMapLatest { date -> repository.getAttendanceForDate(date) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val attendanceMap: StateFlow<Map<Int, AttendanceRecord>> = attendanceList
        .map { list -> list.associateBy { it.studentId } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    // Behavior logs
    val behaviorLogs: StateFlow<List<BehaviorLog>> = repository.allBehaviorLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // School requests
    val schoolRequests: StateFlow<List<SchoolRequest>> = repository.allSchoolRequests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Semester results
    val selectedTerm = MutableStateFlow(TermType.HKI)

    val semesterResults: StateFlow<List<SemesterResult>> = selectedTerm
        .flatMapLatest { term -> repository.getResultsForTerm(term) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val semesterResultsMap: StateFlow<Map<Int, SemesterResult>> = semesterResults
        .map { list -> list.associateBy { it.studentId } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    // Actions
    fun setSearchQuery(q: String) {
        searchQuery.value = q
    }

    fun setGenderFilter(g: String) {
        genderFilter.value = g
    }

    fun setSelectedDate(date: String) {
        selectedDate.value = date
    }

    fun markAttendance(studentId: Int, status: AttendanceStatus, note: String = "") {
        viewModelScope.launch {
            repository.setAttendance(studentId, selectedDate.value, status, note)
        }
    }

    fun markAllPresent() {
        viewModelScope.launch {
            val allIds = students.value.map { it.id }
            repository.markAllPresent(selectedDate.value, allIds)
        }
    }

    fun addBehaviorLog(studentId: Int, type: BehaviorType, category: String, detail: String) {
        viewModelScope.launch {
            repository.addBehaviorLog(studentId, selectedDate.value, type, category, detail)
        }
    }

    fun deleteBehaviorLog(id: Long) {
        viewModelScope.launch {
            repository.deleteBehaviorLog(id)
        }
    }

    fun addSchoolRequest(studentId: Int, title: String, content: String, deadline: String) {
        viewModelScope.launch {
            repository.addSchoolRequest(studentId, title, content, deadline)
        }
    }

    fun updateSchoolRequest(request: SchoolRequest) {
        viewModelScope.launch {
            repository.updateSchoolRequest(request)
        }
    }

    fun deleteSchoolRequest(id: Long) {
        viewModelScope.launch {
            repository.deleteSchoolRequest(id)
        }
    }

    fun setSelectedTerm(term: TermType) {
        selectedTerm.value = term
    }

    fun updateSemesterResult(result: SemesterResult) {
        viewModelScope.launch {
            repository.upsertSemesterResult(result)
        }
    }

    fun updateStudentInfo(student: Student) {
        viewModelScope.launch {
            repository.updateStudent(student)
        }
    }

    val allAttendanceRecords: StateFlow<List<AttendanceRecord>> = repository.allAttendance
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun syncOfficialStudents() {
        viewModelScope.launch {
            repository.syncOfficialStudents(SeedData.students)
        }
    }
}
