package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SemesterResult
import com.example.data.model.Student
import com.example.data.model.TermType
import com.example.ui.components.StudentAvatar
import com.example.ui.theme.SchoolPrimary
import com.example.ui.theme.SuccessGreen
import com.example.util.ZaloShareHelper

@Composable
fun GradesScreen(
    students: List<Student>,
    selectedTerm: TermType,
    onSelectTerm: (TermType) -> Unit,
    semesterResultsMap: Map<Int, SemesterResult>,
    onUpdateResult: (SemesterResult) -> Unit,
    onSendMessage: (String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var editingResultStudent by remember { mutableStateOf<Pair<Student, SemesterResult>?>(null) }
    var expandedStudentId by remember { mutableStateOf<Int?>(null) }

    val resultsList = semesterResultsMap.values.toList()
    val classAvg = if (resultsList.isNotEmpty()) {
        (resultsList.sumOf { it.averageScore } / resultsList.size * 10).toInt() / 10.0
    } else 8.5

    val excellentCount = resultsList.count { it.title.contains("Xuất sắc") }
    val typicalCount = resultsList.count { it.title.contains("Tiêu biểu") }
    val completedCount = resultsList.count { it.title.contains("Hoàn thành tốt") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp)
    ) {
        // 4 Terms Tab Bar (GHKI, HKI, GHKII, HKII)
        val terms = listOf(TermType.GHKI, TermType.HKI, TermType.GHKII, TermType.HKII)
        TabRow(
            selectedTabIndex = terms.indexOf(selectedTerm),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
        ) {
            terms.forEach { term ->
                val isSelected = selectedTerm == term
                Tab(
                    selected = isSelected,
                    onClick = { onSelectTerm(term) },
                    text = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = term.code,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = if (isSelected) SchoolPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = when (term) {
                                    TermType.GHKI -> "Giữa HK1"
                                    TermType.HKI -> "Cuối HK1"
                                    TermType.GHKII -> "Giữa HK2"
                                    TermType.HKII -> "Cuối HK2"
                                },
                                fontSize = 10.sp,
                                color = if (isSelected) SchoolPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                )
            }
        }

        // Stats Overview
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("ĐTB Cả Lớp", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("$classAvg", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = SchoolPrimary)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Xuất sắc", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("$excellentCount em", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF15803D))
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Tiêu biểu", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("$typicalCount em", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF0284C7))
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Hoàn thành tốt", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("$completedCount em", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFFB45309))
                }
            }
        }

        // Students Grade List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(students, key = { it.id }) { student ->
                val result = semesterResultsMap[student.id] ?: SemesterResult(
                    studentId = student.id,
                    term = selectedTerm
                )
                val isExpanded = expandedStudentId == student.id

                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expandedStudentId = if (isExpanded) null else student.id }
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
                                        text = "STT: ${student.stt} • ${result.title}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    color = Color(0xFFDCFCE7),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "ĐTB: ${result.averageScore}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF15803D),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                                IconButton(
                                    onClick = { editingResultStudent = student to result },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = "Sửa điểm", modifier = Modifier.size(18.dp))
                                }
                            }
                        }

                        // Compact subject score bar
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            SubjectScoreTag(name = "Toán", score = result.mathScore)
                            SubjectScoreTag(name = "T.Việt", score = result.vietnameseScore)
                            SubjectScoreTag(name = "T.Anh", score = result.englishScore)
                            SubjectScoreTag(name = "K.Học", score = result.scienceScore)
                            SubjectScoreTag(name = "Sử-Địa", score = result.historyGeoScore)
                            SubjectScoreTag(name = "Tin học", score = result.informaticsScore)
                        }

                        // Teacher's remark
                        if (result.teacherNote.isNotBlank()) {
                            Text(
                                text = "💬 \"${result.teacherNote}\"",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }

                        // Actions Row: Send Zalo Report
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Phẩm chất: ${result.conduct}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = SchoolPrimary
                            )

                            Button(
                                onClick = {
                                    val msg = ZaloShareHelper.buildSemesterResultMessage(student, result)
                                    onSendMessage(student.parentName, student.parentPhone, msg)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0068FF)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("send_grade_zalo_${student.stt}")
                            ) {
                                Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Gửi Báo Cáo Zalo", fontSize = 11.sp, fontWeight = FontWeight.Bold)
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

    // Edit Score Dialog
    editingResultStudent?.let { (student, res) ->
        EditGradeDialog(
            student = student,
            initialResult = res,
            onDismiss = { editingResultStudent = null },
            onSave = { updated ->
                onUpdateResult(updated)
                editingResultStudent = null
            }
        )
    }
}

@Composable
private fun SubjectScoreTag(name: String, score: Double) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        shape = RoundedCornerShape(6.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 5.dp, vertical = 3.dp)
        ) {
            Text(name, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("$score", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SchoolPrimary)
        }
    }
}

@Composable
private fun EditGradeDialog(
    student: Student,
    initialResult: SemesterResult,
    onDismiss: () -> Unit,
    onSave: (SemesterResult) -> Unit
) {
    var math by remember { mutableStateOf(initialResult.mathScore.toString()) }
    var tv by remember { mutableStateOf(initialResult.vietnameseScore.toString()) }
    var eng by remember { mutableStateOf(initialResult.englishScore.toString()) }
    var sci by remember { mutableStateOf(initialResult.scienceScore.toString()) }
    var his by remember { mutableStateOf(initialResult.historyGeoScore.toString()) }
    var info by remember { mutableStateOf(initialResult.informaticsScore.toString()) }
    var conduct by remember { mutableStateOf(initialResult.conduct) }
    var title by remember { mutableStateOf(initialResult.title) }
    var note by remember { mutableStateOf(initialResult.teacherNote) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Cập Nhật Điểm: ${student.fullName}",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Kỳ đánh giá: ${initialResult.term.title}",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = SchoolPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = math,
                        onValueChange = { math = it },
                        label = { Text("Toán") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = tv,
                        onValueChange = { tv = it },
                        label = { Text("Tiếng Việt") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = eng,
                        onValueChange = { eng = it },
                        label = { Text("Tiếng Anh") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = sci,
                        onValueChange = { sci = it },
                        label = { Text("Khoa học") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = his,
                        onValueChange = { his = it },
                        label = { Text("Sử - Địa") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = info,
                        onValueChange = { info = it },
                        label = { Text("Tin học") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = conduct,
                    onValueChange = { conduct = it },
                    label = { Text("Năng lực - Phẩm chất (Tốt / Đạt)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Danh hiệu khen thưởng") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Nhận xét của giáo viên chủ nhiệm") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val m = math.toDoubleOrNull() ?: initialResult.mathScore
                    val t = tv.toDoubleOrNull() ?: initialResult.vietnameseScore
                    val e = eng.toDoubleOrNull() ?: initialResult.englishScore
                    val s = sci.toDoubleOrNull() ?: initialResult.scienceScore
                    val h = his.toDoubleOrNull() ?: initialResult.historyGeoScore
                    val i = info.toDoubleOrNull() ?: initialResult.informaticsScore

                    onSave(
                        initialResult.copy(
                            mathScore = m,
                            vietnameseScore = t,
                            englishScore = e,
                            scienceScore = s,
                            historyGeoScore = h,
                            informaticsScore = i,
                            conduct = conduct,
                            title = title,
                            teacherNote = note
                        )
                    )
                }
            ) {
                Text("Lưu điểm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy")
            }
        }
    )
}
