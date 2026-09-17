package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AttendanceRecord
import com.example.data.model.AttendanceStatus
import com.example.data.model.BehaviorLog
import com.example.data.model.SchoolRequest
import com.example.data.model.SemesterResult
import com.example.data.model.Student
import com.example.ui.components.AttachPhotoDialog
import com.example.ui.components.AttendanceBadge
import com.example.ui.components.StudentAvatar
import com.example.ui.theme.SchoolPrimary
import com.example.util.ZaloShareHelper

@Composable
fun StudentsScreen(
    students: List<Student>,
    filteredStudents: List<Student>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    genderFilter: String,
    onGenderFilterChange: (String) -> Unit,
    attendanceMap: Map<Int, AttendanceRecord>,
    behaviorLogs: List<BehaviorLog>,
    schoolRequests: List<SchoolRequest>,
    semesterResults: List<SemesterResult>,
    onUpdateStudent: (Student) -> Unit,
    onSendMessage: (String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedStudentForDetail by remember { mutableStateOf<Student?>(null) }
    var studentForPhoto by remember { mutableStateOf<Student?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp)
    ) {
        // Quick Summary Card
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Nhóm Zalo Lớp 5A", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = SchoolPrimary)
                        Text(ZaloShareHelper.CLASS_ZALO_GROUP_URL, fontSize = 11.sp, color = Color(0xFF0068FF))
                    }
                    Button(
                        onClick = { ZaloShareHelper.openClassZaloGroup(context) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0068FF)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Mở Zalo Lớp", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Sĩ số", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${students.size} HS", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = SchoolPrimary)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Nam", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${students.count { it.gender == "Nam" }} em", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF0284C7))
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Nữ", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${students.count { it.gender == "Nữ" }} em", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFFDB2777))
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Có mặt", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        val presentCount = attendanceMap.values.count { it.status == AttendanceStatus.DI_HOC }
                        Text("$presentCount/${students.size}", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF16A34A))
                    }
                }
            }
        }

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = { Text("Tìm theo tên, STT (1-31), SĐT...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Xóa tìm kiếm")
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("search_student_input")
        )

        // Gender filter chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Tất cả", "Nam", "Nữ").forEach { label ->
                FilterChip(
                    selected = genderFilter == label,
                    onClick = { onGenderFilterChange(label) },
                    label = { Text(label, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = SchoolPrimary,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        // Student list
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredStudents, key = { it.id }) { student ->
                val att = attendanceMap[student.id]
                val logsCount = behaviorLogs.count { it.studentId == student.id }
                val requestCount = schoolRequests.count { it.studentId == student.id }

                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedStudentForDetail = student }
                        .testTag("student_card_${student.stt}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StudentAvatar(
                            stt = student.stt,
                            gender = student.gender,
                            size = 50,
                            photoUri = student.photoUri,
                            onClick = { studentForPhoto = student },
                            showEditBadge = true
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = student.fullName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                att?.let { AttendanceBadge(status = it.status) }

                                Spacer(modifier = Modifier.weight(1f))

                                // Button Dán hình
                                Surface(
                                    onClick = { studentForPhoto = student },
                                    color = SchoolPrimary.copy(alpha = 0.12f),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.padding(end = 4.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CameraAlt,
                                            contentDescription = "Dán hình",
                                            tint = SchoolPrimary,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = if (student.photoUri.isNotBlank()) "Đổi hình" else "Dán hình",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SchoolPrimary
                                        )
                                    }
                                }
                            }

                            val parentDetails = buildString {
                                if (student.fatherName.isNotBlank()) append("Cha: ${student.fatherName} (${student.fatherJob})")
                                if (student.motherName.isNotBlank()) {
                                    if (isNotEmpty()) append(" • ")
                                    append("Mẹ: ${student.motherName} (${student.motherJob})")
                                }
                            }.ifBlank { "PH: ${student.parentName}" }

                            Text(
                                text = "🆔 ĐD: ${student.citizenId.ifBlank { "Chưa có" }} • SĐT: ${student.parentPhone}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = SchoolPrimary
                            )
                            Text(
                                text = parentDetails,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1
                            )
                            Text(
                                text = "📍 ${student.address} (NS: ${student.dob})",
                                fontSize = 10.sp,
                                color = Color.Gray,
                                maxLines = 1
                            )
                            Row(
                                modifier = Modifier.padding(top = 3.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                if (logsCount > 0) {
                                    Surface(
                                        color = Color(0xFFFEF3C7),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = "$logsCount phản ánh",
                                            fontSize = 10.sp,
                                            color = Color(0xFF92400E),
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                if (requestCount > 0) {
                                    Surface(
                                        color = Color(0xFFE0E7FF),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = "$requestCount yêu cầu",
                                            fontSize = 10.sp,
                                            color = Color(0xFF3730A3),
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // Action buttons
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = {
                                    // Open quick Zalo contact
                                    val attRecord = att ?: AttendanceRecord(studentId = student.id, date = "Hôm nay", status = AttendanceStatus.DI_HOC)
                                    val msg = ZaloShareHelper.buildAttendanceMessage(student, attRecord)
                                    onSendMessage(student.parentName, student.parentPhone, msg)
                                },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Chat,
                                    contentDescription = "Zalo",
                                    tint = Color(0xFF0068FF),
                                    modifier = Modifier.size(19.dp)
                                )
                            }
                            IconButton(
                                onClick = { ZaloShareHelper.dialPhone(context, student.parentPhone) },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Call,
                                    contentDescription = "Gọi điện",
                                    tint = SchoolPrimary,
                                    modifier = Modifier.size(19.dp)
                                )
                            }
                        }
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    // Photo attachment dialog
    studentForPhoto?.let { student ->
        AttachPhotoDialog(
            student = student,
            onSavePhoto = { newUri ->
                val updated = student.copy(photoUri = newUri)
                onUpdateStudent(updated)
                if (selectedStudentForDetail?.id == student.id) {
                    selectedStudentForDetail = updated
                }
                studentForPhoto = null
            },
            onDismiss = { studentForPhoto = null }
        )
    }

    // Detail dialog
    selectedStudentForDetail?.let { student ->
        StudentDetailDialog(
            student = student,
            todayAttendance = attendanceMap[student.id],
            behaviorLogs = behaviorLogs,
            schoolRequests = schoolRequests,
            semesterResults = semesterResults,
            onUpdateStudent = { updated ->
                onUpdateStudent(updated)
                selectedStudentForDetail = updated
            },
            onSendMessage = { name, phone, message ->
                selectedStudentForDetail = null
                onSendMessage(name, phone, message)
            },
            onDismiss = { selectedStudentForDetail = null }
        )
    }
}
