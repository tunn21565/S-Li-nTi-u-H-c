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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
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
import com.example.data.model.BehaviorLog
import com.example.data.model.BehaviorType
import com.example.data.model.Student
import com.example.ui.components.BehaviorBadge
import com.example.ui.components.StudentAvatar
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.SchoolPrimary
import com.example.ui.theme.SuccessGreen
import com.example.util.ZaloShareHelper

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun BehaviorScreen(
    students: List<Student>,
    behaviorLogs: List<BehaviorLog>,
    onAddLog: (Int, BehaviorType, String, String) -> Unit,
    onDeleteLog: (Long) -> Unit,
    onSendMessage: (String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var filterType by remember { mutableStateOf<BehaviorType?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    val praiseCount = behaviorLogs.count { it.type == BehaviorType.PRAISE }
    val remindCount = behaviorLogs.count { it.type == BehaviorType.REMIND }

    val displayedLogs = if (filterType != null) {
        behaviorLogs.filter { it.type == filterType }
    } else {
        behaviorLogs
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp)
        ) {
            // Stats header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Khen ngợi", fontSize = 11.sp, color = SuccessGreen)
                            Text("$praiseCount lượt", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = SuccessGreen)
                        }
                    }
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = ErrorRed, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Nhắc nhở / Uốn nắn", fontSize = 11.sp, color = ErrorRed)
                            Text("$remindCount lượt", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = ErrorRed)
                        }
                    }
                }
            }

            // Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = filterType == null,
                    onClick = { filterType = null },
                    label = { Text("Tất cả (${behaviorLogs.size})", fontSize = 12.sp) }
                )
                FilterChip(
                    selected = filterType == BehaviorType.PRAISE,
                    onClick = { filterType = BehaviorType.PRAISE },
                    label = { Text("Khen ngợi ($praiseCount)", fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = SuccessGreen,
                        selectedLabelColor = Color.White
                    )
                )
                FilterChip(
                    selected = filterType == BehaviorType.REMIND,
                    onClick = { filterType = BehaviorType.REMIND },
                    label = { Text("Nhắc nhở ($remindCount)", fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ErrorRed,
                        selectedLabelColor = Color.White
                    )
                )
            }

            // Logs list
            if (displayedLogs.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Chưa có phản ánh nề nếp nào trong danh sách.",
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
                    items(displayedLogs, key = { it.id }) { log ->
                        val student = students.find { it.id == log.studentId }
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (log.type == BehaviorType.PRAISE) Color(0xFFF9FFF9) else Color(0xFFFFF7F7)
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
                                                    text = "STT: ${it.stt} • Ngày: ${log.date}",
                                                    fontSize = 11.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }
                                    BehaviorBadge(type = log.type, category = log.category)
                                }

                                if (log.detail.isNotBlank()) {
                                    Text(
                                        text = log.detail,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(top = 6.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(
                                        onClick = { onDeleteLog(log.id) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Xóa",
                                            tint = Color.Gray,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    Button(
                                        onClick = {
                                            student?.let { s ->
                                                val msg = ZaloShareHelper.buildBehaviorMessage(s, log)
                                                onSendMessage(s.parentName, s.parentPhone, msg)
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0068FF)),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (log.type == BehaviorType.PRAISE) "Gửi Zalo Khen Ngợi" else "Gửi Zalo Uốn Nắn",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
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
        }

        // FAB to add behavior
        FloatingActionButton(
            onClick = { showAddDialog = true },
            containerColor = SchoolPrimary,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .testTag("add_behavior_fab")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Ghi nhận nề nếp", fontWeight = FontWeight.Bold)
            }
        }
    }

    // Add Behavior Dialog
    if (showAddDialog) {
        AddBehaviorDialog(
            students = students,
            onDismiss = { showAddDialog = false },
            onSave = { studentId, type, category, detail, sendZaloImmediately ->
                onAddLog(studentId, type, category, detail)
                showAddDialog = false
                if (sendZaloImmediately) {
                    val s = students.find { it.id == studentId }
                    if (s != null) {
                        val tempLog = BehaviorLog(
                            studentId = studentId,
                            date = "Hôm nay",
                            type = type,
                            category = category,
                            detail = detail
                        )
                        val msg = ZaloShareHelper.buildBehaviorMessage(s, tempLog)
                        onSendMessage(s.parentName, s.parentPhone, msg)
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun AddBehaviorDialog(
    students: List<Student>,
    onDismiss: () -> Unit,
    onSave: (studentId: Int, type: BehaviorType, category: String, detail: String, sendZaloImmediately: Boolean) -> Unit
) {
    var selectedStudent by remember { mutableStateOf(students.firstOrNull()) }
    var expandedDropdown by remember { mutableStateOf(false) }
    var selectedType by remember { mutableStateOf(BehaviorType.REMIND) }
    var selectedCategory by remember { mutableStateOf("Không làm bài tập") }
    var detailText by remember { mutableStateOf("") }
    var sendZaloImmediately by remember { mutableStateOf(true) }

    // User requested categories:
    val positivePresets = listOf(
        "Hăng hái phát biểu",
        "Giúp đỡ bạn bè",
        "Làm bài tập tốt",
        "Siêng năng trực nhật",
        "Có nhiều tiến bộ"
    )

    val reminderPresets = listOf(
        "Không làm bài tập",
        "Không viết bài",
        "Đánh bạn trong lớp",
        "Vi phạm nội quy",
        "Không đội nón bảo hiểm",
        "Nói chuyện riêng",
        "Đi học trễ"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Ghi Nhận Nề Nếp & Học Tập",
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

                Spacer(modifier = Modifier.height(12.dp))

                // Select Type
                Text("Phân loại:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        color = if (selectedType == BehaviorType.REMIND) ErrorRed else MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        TextButton(onClick = {
                            selectedType = BehaviorType.REMIND
                            selectedCategory = reminderPresets.first()
                        }) {
                            Text(
                                "⚠️ Nhắc nhở / Uốn nắn",
                                color = if (selectedType == BehaviorType.REMIND) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Surface(
                        color = if (selectedType == BehaviorType.PRAISE) SuccessGreen else MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        TextButton(onClick = {
                            selectedType = BehaviorType.PRAISE
                            selectedCategory = positivePresets.first()
                        }) {
                            Text(
                                "🌟 Khen ngợi / Tích cực",
                                color = if (selectedType == BehaviorType.PRAISE) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Preset Chips for easy 1-tap selection
                Text("Chọn nhanh hành vi nề nếp:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))
                val presets = if (selectedType == BehaviorType.PRAISE) positivePresets else reminderPresets
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    presets.forEach { item ->
                        val isSelected = selectedCategory == item
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategory = item },
                            label = { Text(item, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = if (selectedType == BehaviorType.PRAISE) SuccessGreen else ErrorRed,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Detail notes
                OutlinedTextField(
                    value = detailText,
                    onValueChange = { detailText = it },
                    label = { Text("Chi tiết cụ thể (ví dụ: bài tập nào, xảy ra ở đâu)") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Direct send toggle
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
                            text = "Mở Zalo gửi phụ huynh ngay khi lưu",
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
                        onSave(s.id, selectedType, selectedCategory, detailText, sendZaloImmediately)
                    }
                },
                enabled = selectedStudent != null && selectedCategory.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = SchoolPrimary)
            ) {
                Text("Lưu phản ánh", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy")
            }
        }
    )
}
