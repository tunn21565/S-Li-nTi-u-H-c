package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.NotificationImportant
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.example.data.model.SchoolRequest
import com.example.data.model.Student
import com.example.ui.components.StudentAvatar
import com.example.ui.theme.SchoolPrimary
import com.example.ui.theme.SuccessGreen
import com.example.util.ZaloShareHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchoolRequestsScreen(
    students: List<Student>,
    schoolRequests: List<SchoolRequest>,
    onAddRequest: (Int, String, String, String) -> Unit,
    onUpdateRequest: (SchoolRequest) -> Unit,
    onDeleteRequest: (Long) -> Unit,
    onSendMessage: (String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedStudentFilter by remember { mutableStateOf<Int?>(null) }

    val displayedRequests = if (selectedStudentFilter != null) {
        schoolRequests.filter { it.studentId == selectedStudentFilter }
    } else {
        schoolRequests
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp)
        ) {
            // Header Info Card
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationImportant,
                        contentDescription = null,
                        tint = SchoolPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Yêu Cầu Riêng Cho Từng Em",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = SchoolPrimary
                        )
                        Text(
                            text = "Gửi thông báo cá nhân đến Zalo phụ huynh: nộp hồ sơ, BHYT, chuẩn bị dụng cụ, mời trao đổi...",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Student filter chips
            Text(
                text = "Lọc theo học sinh:",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChip(
                    selected = selectedStudentFilter == null,
                    onClick = { selectedStudentFilter = null },
                    label = { Text("Tất cả (${schoolRequests.size})", fontSize = 11.sp) }
                )
            }

            // Requests List
            if (displayedRequests.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.NotificationImportant,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Chưa có yêu cầu riêng nào của nhà trường.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(displayedRequests, key = { it.id }) { req ->
                        val student = students.find { it.id == req.studentId }
                        val isDone = req.status == "Đã hoàn thành"

                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isDone) Color(0xFFF9FAFB) else MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        student?.let {
                                            StudentAvatar(stt = it.stt, gender = it.gender, size = 36)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column {
                                                Text(
                                                    text = it.fullName,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 14.sp
                                                )
                                                Text(
                                                    text = "STT: ${it.stt} • SĐT: ${it.parentPhone}",
                                                    fontSize = 11.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }

                                    Surface(
                                        color = if (isDone) Color(0xFFDCFCE7) else Color(0xFFEFF6FF),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = req.status,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (isDone) SuccessGreen else SchoolPrimary,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "📌 ${req.title}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color(0xFF1E293B)
                                )
                                Text(
                                    text = req.content,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(top = 2.dp)
                                )

                                if (req.deadline.isNotBlank()) {
                                    Text(
                                        text = "⏰ Hạn chót: ${req.deadline}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFFD97706),
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row {
                                        IconButton(
                                            onClick = { onDeleteRequest(req.id) },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(Icons.Default.Delete, contentDescription = "Xóa", tint = Color.Gray, modifier = Modifier.size(18.dp))
                                        }
                                        TextButton(onClick = {
                                            val newStatus = if (isDone) "Chờ gửi Zalo" else "Đã hoàn thành"
                                            onUpdateRequest(req.copy(status = newStatus))
                                        }) {
                                            Text(if (isDone) "Đánh dấu chưa xong" else "Hoàn thành", fontSize = 11.sp)
                                        }
                                    }

                                    Button(
                                        onClick = {
                                            student?.let { s ->
                                                val msg = ZaloShareHelper.buildSchoolRequestMessage(s, req)
                                                onSendMessage(s.parentName, s.parentPhone, msg)
                                                onUpdateRequest(req.copy(isSent = true, status = "Đã gửi Zalo"))
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0068FF)),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Gửi Zalo Riêng", fontSize = 11.sp, fontWeight = FontWeight.Bold)
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

        // FAB to add school request
        FloatingActionButton(
            onClick = { showAddDialog = true },
            containerColor = SchoolPrimary,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .testTag("add_request_fab")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Thêm yêu cầu riêng", fontWeight = FontWeight.Bold)
            }
        }
    }

    if (showAddDialog) {
        AddSchoolRequestDialog(
            students = students,
            onDismiss = { showAddDialog = false },
            onSave = { studentId, title, content, deadline, sendZalo ->
                onAddRequest(studentId, title, content, deadline)
                showAddDialog = false
                if (sendZalo) {
                    val s = students.find { it.id == studentId }
                    if (s != null) {
                        val tempReq = SchoolRequest(
                            studentId = studentId,
                            title = title,
                            content = content,
                            deadline = deadline
                        )
                        val msg = ZaloShareHelper.buildSchoolRequestMessage(s, tempReq)
                        onSendMessage(s.parentName, s.parentPhone, msg)
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun AddSchoolRequestDialog(
    students: List<Student>,
    onDismiss: () -> Unit,
    onSave: (studentId: Int, title: String, content: String, deadline: String, sendZaloImmediately: Boolean) -> Unit
) {
    var selectedStudent by remember { mutableStateOf(students.firstOrNull()) }
    var expandedDropdown by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var deadline by remember { mutableStateOf("") }
    var sendZaloImmediately by remember { mutableStateOf(true) }

    val presetTemplates = listOf(
        Pair("Nộp bổ sung hồ sơ / BHYT", "Kính đề nghị phụ huynh nộp bổ sung bản photo thẻ BHYT và giấy khám sức khỏe để nhà trường cập nhật hồ sơ."),
        Pair("Chuẩn bị đồ dùng môn học", "Nhờ phụ huynh nhắc em chuẩn bị đầy đủ bộ đồ dùng học tập môn Mỹ thuật (màu vẽ, giấy A4) cho tiết học ngày mai."),
        Pair("Mời phụ huynh gặp GVCN", "Kính mời phụ huynh sắp xếp thời gian đến trường gặp GVCN sau giờ tan học để trao đổi về tình hình học tập của em."),
        Pair("Nhắc nhở trang phục & nề nếp", "Nhà trường nhắc nhở em cần mặc đúng đồng phục, đeo khăn quàng đỏ và đội mũ bảo hiểm khi đến trường."),
        Pair("Đăng ký phong trào / thi đấu", "GVCN thông báo về kỳ thi giao lưu Olympic Toán - Tiếng Anh, phụ huynh có nhu cầu đăng ký cho em vui lòng phản hồi.")
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Tạo Yêu Cầu Riêng Cho Học Sinh",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Select student dropdown
                Text("Chọn học sinh Lớp 5A:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))
                ExposedDropdownMenuBox(
                    expanded = expandedDropdown,
                    onExpandedChange = { expandedDropdown = it },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedStudent?.let { "STT ${it.stt}: ${it.fullName}" } ?: "Chọn học sinh",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDropdown) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedDropdown,
                        onDismissRequest = { expandedDropdown = false }
                    ) {
                        students.forEach { s ->
                            DropdownMenuItem(
                                text = { Text("${s.stt}. ${s.fullName} (${s.gender})") },
                                onClick = {
                                    selectedStudent = s
                                    expandedDropdown = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Presets
                Text("Mẫu yêu cầu nhanh từ nhà trường:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    presetTemplates.forEach { (t, c) ->
                        FilterChip(
                            selected = title == t,
                            onClick = {
                                title = t
                                content = c
                            },
                            label = { Text(t, fontSize = 11.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Tiêu đề yêu cầu") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Nội dung chi tiết") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = deadline,
                    onValueChange = { deadline = it },
                    label = { Text("Thời hạn (Ví dụ: Trước thứ Sáu, 16h30...)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    color = Color(0xFFEFF6FF),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Chat, contentDescription = null, tint = Color(0xFF0068FF), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Mở Zalo gửi riêng cho phụ huynh em ngay",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = SchoolPrimary
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    selectedStudent?.let { s ->
                        onSave(s.id, title, content, deadline, sendZaloImmediately)
                    }
                },
                enabled = selectedStudent != null && title.isNotBlank() && content.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = SchoolPrimary)
            ) {
                Text("Lưu yêu cầu", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy")
            }
        }
    )
}
