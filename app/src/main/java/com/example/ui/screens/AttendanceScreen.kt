package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Today
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.SeedData
import com.example.data.model.AttendanceRecord
import com.example.data.model.AttendanceStatus
import com.example.data.model.Student
import com.example.ui.components.AttendanceBadge
import com.example.ui.components.StudentAvatar
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.InfoBlue
import com.example.ui.theme.SchoolPrimary
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningAmber
import com.example.util.ZaloShareHelper
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun AttendanceScreen(
    students: List<Student>,
    selectedDate: String,
    onDateChange: (String) -> Unit,
    attendanceMap: Map<Int, AttendanceRecord>,
    onMarkAttendance: (Int, AttendanceStatus, String) -> Unit,
    onMarkAllPresent: () -> Unit,
    onSendMessage: (String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showOnlyIssues by remember { mutableStateOf(false) }
    var editingStudentNoteId by remember { mutableStateOf<Int?>(null) }
    var tempNoteText by remember { mutableStateOf("") }

    val presentCount = attendanceMap.values.count { it.status == AttendanceStatus.DI_HOC }
    val lateCount = attendanceMap.values.count { it.status == AttendanceStatus.DI_TRE }
    val excusedCount = attendanceMap.values.count { it.status == AttendanceStatus.NGHI_CO_PHEP }
    val unexcusedCount = attendanceMap.values.count { it.status == AttendanceStatus.NGHI_KHONG_PHEP }

    val displayedStudents = if (showOnlyIssues) {
        students.filter { student ->
            val status = attendanceMap[student.id]?.status ?: AttendanceStatus.DI_HOC
            status != AttendanceStatus.DI_HOC
        }
    } else {
        students
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp)
    ) {
        // Date Selector Bar
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = {
                    val newDate = shiftDate(selectedDate, -1)
                    onDateChange(newDate)
                }) {
                    Icon(Icons.Default.ChevronLeft, contentDescription = "Ngày trước")
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Today, contentDescription = null, tint = SchoolPrimary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Ngày: $selectedDate",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = SchoolPrimary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = {
                        val newDate = shiftDate(selectedDate, 1)
                        onDateChange(newDate)
                    }) {
                        Icon(Icons.Default.ChevronRight, contentDescription = "Ngày sau")
                    }
                    TextButton(onClick = { onDateChange(SeedData.getTodayString()) }) {
                        Text("Hôm nay", fontSize = 12.sp)
                    }
                }
            }
        }

        // Attendance stats cards
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            MetricCard(
                title = "Đi học",
                count = presentCount,
                color = SuccessGreen,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "Đi trễ",
                count = lateCount,
                color = WarningAmber,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "Có phép",
                count = excusedCount,
                color = InfoBlue,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "Không phép",
                count = unexcusedCount,
                color = ErrorRed,
                modifier = Modifier.weight(1f)
            )
        }

        // Action & Filter Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onMarkAllPresent,
                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("mark_all_present_button")
            ) {
                Icon(Icons.Default.DoneAll, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Cả lớp có mặt", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            FilterChip(
                selected = showOnlyIssues,
                onClick = { showOnlyIssues = !showOnlyIssues },
                label = { Text("Cần báo PH (${lateCount + excusedCount + unexcusedCount})", fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = ErrorRed,
                    selectedLabelColor = Color.White
                )
            )
        }

        // Student Attendance List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(displayedStudents, key = { it.id }) { student ->
                val att = attendanceMap[student.id]
                val currentStatus = att?.status ?: AttendanceStatus.DI_HOC
                val currentNote = att?.note ?: ""

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = when (currentStatus) {
                            AttendanceStatus.DI_HOC -> MaterialTheme.colorScheme.surface
                            AttendanceStatus.DI_TRE -> Color(0xFFFFFBEB)
                            AttendanceStatus.NGHI_CO_PHEP -> Color(0xFFEFF6FF)
                            AttendanceStatus.NGHI_KHONG_PHEP -> Color(0xFFFEF2F2)
                        }
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                StudentAvatar(stt = student.stt, gender = student.gender, size = 40)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = student.fullName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "PH: ${student.parentPhone}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            AttendanceBadge(status = currentStatus)
                        }

                        // Status Selection Buttons
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            AttendanceStatus.values().forEach { status ->
                                val isSelected = currentStatus == status
                                Surface(
                                    color = if (isSelected) {
                                        when (status) {
                                            AttendanceStatus.DI_HOC -> SuccessGreen
                                            AttendanceStatus.DI_TRE -> WarningAmber
                                            AttendanceStatus.NGHI_CO_PHEP -> InfoBlue
                                            AttendanceStatus.NGHI_KHONG_PHEP -> ErrorRed
                                        }
                                    } else {
                                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("status_${student.stt}_${status.name}")
                                ) {
                                    TextButton(
                                        onClick = {
                                            onMarkAttendance(student.id, status, currentNote)
                                        },
                                        modifier = Modifier.height(34.dp)
                                    ) {
                                        Text(
                                            text = status.label,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }

                        // If not present or has note, provide note & Zalo actions
                        if (currentStatus != AttendanceStatus.DI_HOC || currentNote.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            if (editingStudentNoteId == student.id) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    OutlinedTextField(
                                        value = tempNoteText,
                                        onValueChange = { tempNoteText = it },
                                        placeholder = { Text("Lý do vắng / trễ...") },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Button(onClick = {
                                        onMarkAttendance(student.id, currentStatus, tempNoteText)
                                        editingStudentNoteId = null
                                    }) {
                                        Text("Lưu")
                                    }
                                }
                            } else {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = if (currentNote.isNotBlank()) "Ghi chú: $currentNote" else "Chưa có ghi chú",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(end = 4.dp)
                                    )
                                    TextButton(onClick = {
                                        tempNoteText = currentNote
                                        editingStudentNoteId = student.id
                                    }) {
                                        Text("Sửa lý do", fontSize = 11.sp)
                                    }
                                    Button(
                                        onClick = {
                                            val record = att ?: AttendanceRecord(
                                                studentId = student.id,
                                                date = selectedDate,
                                                status = currentStatus,
                                                note = currentNote
                                            )
                                            val msg = ZaloShareHelper.buildAttendanceMessage(student, record)
                                            onSendMessage(student.parentName, student.parentPhone, msg)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0068FF)),
                                        modifier = Modifier.testTag("send_zalo_att_${student.stt}")
                                    ) {
                                        Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Báo Zalo", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
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
}

@Composable
private fun MetricCard(title: String, count: Int, color: Color, modifier: Modifier = Modifier) {
    Surface(
        color = color.copy(alpha = 0.12f),
        shape = RoundedCornerShape(10.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "$count", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = color)
            Text(text = title, fontSize = 10.sp, color = color, fontWeight = FontWeight.Medium)
        }
    }
}

private fun shiftDate(dateStr: String, days: Int): String {
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = sdf.parse(dateStr) ?: Date()
        val cal = Calendar.getInstance().apply {
            time = date
            add(Calendar.DAY_OF_YEAR, days)
        }
        sdf.format(cal.time)
    } catch (e: Exception) {
        dateStr
    }
}
