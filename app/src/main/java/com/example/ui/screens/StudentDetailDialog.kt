package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AttendanceRecord
import com.example.data.model.AttendanceStatus
import com.example.data.model.BehaviorLog
import com.example.data.model.BehaviorType
import com.example.data.model.SchoolRequest
import com.example.data.model.SemesterResult
import com.example.data.model.Student
import com.example.data.model.TermType
import com.example.ui.components.AttachPhotoDialog
import com.example.ui.components.AttendanceBadge
import com.example.ui.components.StudentAvatar
import com.example.ui.theme.SchoolPrimary
import com.example.ui.theme.SuccessGreen
import com.example.util.ZaloShareHelper

@Composable
fun StudentDetailDialog(
    student: Student,
    todayAttendance: AttendanceRecord?,
    behaviorLogs: List<BehaviorLog>,
    schoolRequests: List<SchoolRequest>,
    semesterResults: List<SemesterResult>,
    onUpdateStudent: (Student) -> Unit,
    onSendMessage: (String, String, String) -> Unit, // recipientName, phone, message
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    var isEditingContact by remember { mutableStateOf(false) }
    var editedPhone by remember { mutableStateOf(student.parentPhone) }
    var editedParentName by remember { mutableStateOf(student.parentName) }

    var showAttachPhotoDialog by remember { mutableStateOf(false) }
    var isEditingFullProfile by remember { mutableStateOf(false) }
    var editedFatherName by remember { mutableStateOf(student.fatherName) }
    var editedFatherJob by remember { mutableStateOf(student.fatherJob) }
    var editedMotherName by remember { mutableStateOf(student.motherName) }
    var editedMotherJob by remember { mutableStateOf(student.motherJob) }
    var editedAddress by remember { mutableStateOf(student.address) }
    var editedBirthPlace by remember { mutableStateOf(student.birthPlace) }
    var editedCitizenId by remember { mutableStateOf(student.citizenId) }

    val studentLogs = behaviorLogs.filter { it.studentId == student.id }
    val studentRequests = schoolRequests.filter { it.studentId == student.id }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                StudentAvatar(
                    stt = student.stt,
                    gender = student.gender,
                    size = 52,
                    photoUri = student.photoUri,
                    onClick = { showAttachPhotoDialog = true },
                    showEditBadge = true
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = student.fullName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "STT: ${student.stt} • Giới tính: ${student.gender} • NS: ${student.dob}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        onClick = { showAttachPhotoDialog = true },
                        color = SchoolPrimary.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Dán hình",
                                tint = SchoolPrimary,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (student.photoUri.isNotBlank()) "Đổi hình ảnh" else "Dán hình học sinh",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = SchoolPrimary
                            )
                        }
                    }
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Contact info bar
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        if (isEditingContact) {
                            OutlinedTextField(
                                value = editedParentName,
                                onValueChange = { editedParentName = it },
                                label = { Text("Tên phụ huynh") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = editedPhone,
                                onValueChange = { editedPhone = it },
                                label = { Text("Số điện thoại Zalo") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                                TextButton(onClick = { isEditingContact = false }) {
                                    Text("Hủy")
                                }
                                Button(onClick = {
                                    onUpdateStudent(
                                        student.copy(
                                            parentName = editedParentName,
                                            parentPhone = editedPhone
                                        )
                                    )
                                    isEditingContact = false
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
                                Column {
                                    Text(
                                        text = "Phụ huynh: ${student.parentName}",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = "Zalo/SĐT: ${student.parentPhone}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Row {
                                    IconButton(
                                        onClick = { isEditingContact = true },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Sửa SĐT",
                                            modifier = Modifier.size(18.dp)
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
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Tabs for detail views
                val tabs = listOf("Chuyên Cần", "Nề Nếp", "Yêu Cầu", "Điểm Số", "Gia Đình & LL")
                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    edgePadding = 0.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(title, fontSize = 12.sp, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Tab Content
                when (selectedTab) {
                    0 -> {
                        // Attendance Info
                        Text(
                            text = "Tình trạng chuyên cần hôm nay:",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        todayAttendance?.let { att ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                AttendanceBadge(status = att.status)
                                Button(
                                    onClick = {
                                        val msg = ZaloShareHelper.buildAttendanceMessage(student, att)
                                        onSendMessage(student.parentName, student.parentPhone, msg)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0068FF)),
                                    modifier = Modifier.testTag("send_att_zalo")
                                ) {
                                    Icon(imageVector = Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Gửi Zalo Chuyên Cần", fontSize = 11.sp)
                                }
                            }
                            if (att.note.isNotBlank()) {
                                Text(
                                    text = "Ghi chú: ${att.note}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        } ?: Text(
                            text = "Chưa có dữ liệu điểm danh hôm nay.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    1 -> {
                        // Behavior history
                        if (studentLogs.isEmpty()) {
                            Text(
                                text = "Chưa có phản ánh nề nếp nào của em.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            studentLogs.forEach { log ->
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (log.type == BehaviorType.PRAISE) Color(0xFFF0FDF4) else Color(0xFFFEF2F2)
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = log.category,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = if (log.type == BehaviorType.PRAISE) Color(0xFF15803D) else Color(0xFFB91C1C)
                                            )
                                            Text(
                                                text = log.date,
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        if (log.detail.isNotBlank()) {
                                            Text(
                                                text = log.detail,
                                                fontSize = 12.sp,
                                                modifier = Modifier.padding(top = 2.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Button(
                                            onClick = {
                                                val msg = ZaloShareHelper.buildBehaviorMessage(student, log)
                                                onSendMessage(student.parentName, student.parentPhone, msg)
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0068FF)),
                                            modifier = Modifier.align(Alignment.End)
                                        ) {
                                            Icon(imageVector = Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Gửi Zalo Uốn Nắn", fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    2 -> {
                        // School requests for this student
                        if (studentRequests.isEmpty()) {
                            Text(
                                text = "Chưa có yêu cầu riêng nào của nhà trường cho em.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            studentRequests.forEach { req ->
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text(
                                            text = req.title,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                        Text(
                                            text = req.content,
                                            fontSize = 12.sp,
                                            modifier = Modifier.padding(top = 2.dp)
                                        )
                                        if (req.deadline.isNotBlank()) {
                                            Text(
                                                text = "Hạn: ${req.deadline}",
                                                fontSize = 11.sp,
                                                color = Color(0xFFB45309),
                                                fontWeight = FontWeight.Medium,
                                                modifier = Modifier.padding(top = 2.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Button(
                                            onClick = {
                                                val msg = ZaloShareHelper.buildSchoolRequestMessage(student, req)
                                                onSendMessage(student.parentName, student.parentPhone, msg)
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0068FF)),
                                            modifier = Modifier.align(Alignment.End)
                                        ) {
                                            Icon(imageVector = Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Gửi Zalo Riêng", fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    3 -> {
                        // Semester results
                        val terms = listOf(TermType.GHKI, TermType.HKI, TermType.GHKII, TermType.HKII)
                        terms.forEach { term ->
                            val res = semesterResults.find { it.studentId == student.id && it.term == term }
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = term.title,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = SchoolPrimary
                                        )
                                        res?.let {
                                            Surface(
                                                color = Color(0xFFDCFCE7),
                                                shape = RoundedCornerShape(6.dp)
                                            ) {
                                                Text(
                                                    text = "ĐTB: ${it.averageScore}",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 12.sp,
                                                    color = Color(0xFF15803D),
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }
                                    res?.let { item ->
                                        Text(
                                            text = "Toán: ${item.mathScore} | TV: ${item.vietnameseScore} | Anh: ${item.englishScore} | KH: ${item.scienceScore} | LS-ĐL: ${item.historyGeoScore} | Tin: ${item.informaticsScore}",
                                            fontSize = 11.sp,
                                            modifier = Modifier.padding(vertical = 3.dp)
                                        )
                                        Text(
                                            text = "🏆 ${item.title} • Phẩm chất: ${item.conduct}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                        if (item.teacherNote.isNotBlank()) {
                                            Text(
                                                text = "Nhận xét: \"${item.teacherNote}\"",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Button(
                                            onClick = {
                                                val msg = ZaloShareHelper.buildSemesterResultMessage(student, item)
                                                onSendMessage(student.parentName, student.parentPhone, msg)
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0068FF)),
                                            modifier = Modifier.align(Alignment.End)
                                        ) {
                                            Icon(imageVector = Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Gửi Bảng Điểm Zalo", fontSize = 11.sp)
                                        }
                                    } ?: Text(
                                        text = "Chưa có bảng điểm kỳ này.",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                    4 -> {
                        // Gia Đình & Thông Tin Liên Lạc
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Hồ sơ Cha Mẹ & Định danh:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = SchoolPrimary
                                )
                                TextButton(
                                    onClick = { isEditingFullProfile = !isEditingFullProfile }
                                ) {
                                    Icon(
                                        imageVector = if (isEditingFullProfile) Icons.Default.CheckCircle else Icons.Default.Edit,
                                        contentDescription = null,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(if (isEditingFullProfile) "Hủy sửa" else "Sửa hồ sơ", fontSize = 12.sp)
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))

                            if (isEditingFullProfile) {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        OutlinedTextField(
                                            value = editedFatherName,
                                            onValueChange = { editedFatherName = it },
                                            label = { Text("Họ tên Cha", fontSize = 12.sp) },
                                            singleLine = true,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                        OutlinedTextField(
                                            value = editedFatherJob,
                                            onValueChange = { editedFatherJob = it },
                                            label = { Text("Nghề nghiệp Cha (ví dụ: LR, CN)", fontSize = 12.sp) },
                                            singleLine = true,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                        OutlinedTextField(
                                            value = editedMotherName,
                                            onValueChange = { editedMotherName = it },
                                            label = { Text("Họ tên Mẹ", fontSize = 12.sp) },
                                            singleLine = true,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                        OutlinedTextField(
                                            value = editedMotherJob,
                                            onValueChange = { editedMotherJob = it },
                                            label = { Text("Nghề nghiệp Mẹ", fontSize = 12.sp) },
                                            singleLine = true,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                        OutlinedTextField(
                                            value = editedCitizenId,
                                            onValueChange = { editedCitizenId = it },
                                            label = { Text("Mã định danh cá nhân (12 số CCCD)", fontSize = 12.sp) },
                                            singleLine = true,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                        OutlinedTextField(
                                            value = editedPhone,
                                            onValueChange = { editedPhone = it },
                                            label = { Text("Số điện thoại Cha Mẹ / Zalo", fontSize = 12.sp) },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                            singleLine = true,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                        OutlinedTextField(
                                            value = editedAddress,
                                            onValueChange = { editedAddress = it },
                                            label = { Text("Chỗ ở hiện tại", fontSize = 12.sp) },
                                            singleLine = true,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                        OutlinedTextField(
                                            value = editedBirthPlace,
                                            onValueChange = { editedBirthPlace = it },
                                            label = { Text("Nơi sinh", fontSize = 12.sp) },
                                            singleLine = true,
                                            modifier = Modifier.fillMaxWidth()
                                        )

                                        Button(
                                            onClick = {
                                                onUpdateStudent(
                                                    student.copy(
                                                        fatherName = editedFatherName.trim(),
                                                        fatherJob = editedFatherJob.trim(),
                                                        motherName = editedMotherName.trim(),
                                                        motherJob = editedMotherJob.trim(),
                                                        citizenId = editedCitizenId.trim(),
                                                        parentPhone = editedPhone.trim(),
                                                        address = editedAddress.trim(),
                                                        birthPlace = editedBirthPlace.trim(),
                                                        parentName = if (editedFatherName.isNotBlank()) editedFatherName.trim() else editedMotherName.trim()
                                                    )
                                                )
                                                isEditingFullProfile = false
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = SchoolPrimary),
                                            modifier = Modifier.align(Alignment.End)
                                        ) {
                                            Icon(imageVector = Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Lưu Thông Tin")
                                        }
                                    }
                                }
                            } else {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text("👨 Họ tên Cha: ${student.fatherName.ifBlank { "Chưa có thông tin" }}", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                        Text("   Nghề nghiệp: ${student.fatherJob.ifBlank { "Không" }}", fontSize = 11.sp, color = Color.DarkGray)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("👩 Họ tên Mẹ: ${student.motherName.ifBlank { "Chưa có thông tin" }}", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                        Text("   Nghề nghiệp: ${student.motherJob.ifBlank { "Không" }}", fontSize = 11.sp, color = Color.DarkGray)
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text("🆔 Số định danh cá nhân: ${student.citizenId.ifBlank { "Chưa có" }}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = SchoolPrimary)
                                        Spacer(modifier = Modifier.height(3.dp))
                                        Text("📞 Số điện thoại PH/Zalo: ${student.parentPhone}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Spacer(modifier = Modifier.height(3.dp))
                                        Text("🏠 Chỗ ở hiện tại: ${student.address.ifBlank { "Tân Bình" }}", fontSize = 12.sp)
                                        Spacer(modifier = Modifier.height(3.dp))
                                        Text("📍 Nơi sinh: ${student.birthPlace.ifBlank { "Cai Lậy" }}", fontSize = 12.sp)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        ZaloShareHelper.openZaloChat(context, student.parentPhone)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0068FF)),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(imageVector = Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Mở Zalo PH", fontSize = 11.sp)
                                }

                                Button(
                                    onClick = {
                                        ZaloShareHelper.dialPhone(context, student.parentPhone)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(imageVector = Icons.Default.Call, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Gọi điện", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Đóng")
            }
        }
    )

    if (showAttachPhotoDialog) {
        AttachPhotoDialog(
            student = student,
            onSavePhoto = { newUri ->
                onUpdateStudent(student.copy(photoUri = newUri))
                showAttachPhotoDialog = false
            },
            onDismiss = { showAttachPhotoDialog = false }
        )
    }
}
