package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AttendanceRecord
import com.example.data.model.BehaviorLog
import com.example.data.model.Student
import com.example.ui.components.StudentAvatar
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.SchoolGold
import com.example.ui.theme.SchoolPrimary
import com.example.ui.theme.SuccessGreen
import com.example.util.ExcelReportExporter
import com.example.util.StudentReportRow
import com.example.util.ZaloShareHelper
import java.io.File

@Composable
fun ReportsScreen(
    students: List<Student>,
    allAttendanceRecords: List<AttendanceRecord>,
    behaviorLogs: List<BehaviorLog>,
    onSendMessage: (parentName: String, parentPhone: String, message: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Tuần, 1: Tháng
    var selectedWeekIndex by remember { mutableIntStateOf(0) }
    var selectedMonthIndex by remember { mutableIntStateOf(0) }
    var generatedFile by remember { mutableStateOf<File?>(null) }
    var showExportSuccessDialog by remember { mutableStateOf(false) }

    val weekOptions = listOf(
        Triple("Tuần 1 (14/09 - 18/09/2026)", "Tuần từ 14/09/2026 đến 18/09/2026", listOf(
            "Thứ 2" to "14-Sep",
            "Thứ 3" to "15-Sep",
            "Thứ 4" to "16-Sep",
            "Thứ 5" to "17-Sep",
            "Thứ 6" to "18-Sep"
        )),
        Triple("Tuần 2 (21/09 - 25/09/2026)", "Tuần từ 21/09/2026 đến 25/09/2026", listOf(
            "Thứ 2" to "21-Sep",
            "Thứ 3" to "22-Sep",
            "Thứ 4" to "23-Sep",
            "Thứ 5" to "24-Sep",
            "Thứ 6" to "25-Sep"
        )),
        Triple("Tuần 3 (28/09 - 02/10/2026)", "Tuần từ 28/09/2026 đến 02/10/2026", listOf(
            "Thứ 2" to "28-Sep",
            "Thứ 3" to "29-Sep",
            "Thứ 4" to "30-Sep",
            "Thứ 5" to "01-Oct",
            "Thứ 6" to "02-Oct"
        ))
    )

    val monthOptions = listOf(
        "Tháng 09/2026",
        "Tháng 10/2026",
        "Tháng 11/2026",
        "Tháng 12/2026"
    )

    val currentWeek = weekOptions[selectedWeekIndex]
    val currentPeriodTitle = if (selectedTab == 0) currentWeek.second else "Tháng: ${monthOptions[selectedMonthIndex]}"

    // Compute report rows
    val dayDates = if (selectedTab == 0) {
        currentWeek.third.map { it.second }
    } else {
        listOf("14-Sep", "15-Sep", "16-Sep", "17-Sep", "18-Sep")
    }

    val reportRows = remember(students, dayDates, allAttendanceRecords, behaviorLogs) {
        ExcelReportExporter.computeReportRows(students, dayDates, allAttendanceRecords, behaviorLogs)
    }

    val totalStudents = reportRows.size
    val totCount = reportRows.count { it.conductRanking == "Tốt" }
    val khaCount = reportRows.count { it.conductRanking == "Khá" }
    val datCount = reportRows.count { it.conductRanking == "Đạt" }
    val cogangCount = reportRows.count { it.conductRanking == "Cần cố gắng" }

    val totPct = if (totalStudents > 0) String.format("%.1f", totCount * 100.0 / totalStudents) else "0"
    val khaPct = if (totalStudents > 0) String.format("%.1f", khaCount * 100.0 / totalStudents) else "0"
    val datPct = if (totalStudents > 0) String.format("%.1f", datCount * 100.0 / totalStudents) else "0"
    val cogangPct = if (totalStudents > 0) String.format("%.1f", cogangCount * 100.0 / totalStudents) else "0"

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(6.dp))

        // Top Banner with Zalo Link & Title
        ElevatedCard(
            colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFF0068FF)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Báo Cáo Tổng Kết Tuần & Tháng",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                        Text(
                            text = "Lớp 5A • Trường Tiểu học Tân Thạnh (Tân Bình)",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 12.sp
                        )
                    }

                    Button(
                        onClick = { ZaloShareHelper.openClassZaloGroup(context) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Groups, contentDescription = null, tint = Color(0xFF0068FF), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Nhóm Zalo 5A", color = Color(0xFF0068FF), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Đường link nhóm Zalo lớp: ${ZaloShareHelper.CLASS_ZALO_GROUP_URL}",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Tab Row: Báo cáo Tuần vs Báo cáo Tháng
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.fillMaxWidth()
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Báo cáo Tuần", fontWeight = FontWeight.SemiBold, fontSize = 13.sp) },
                icon = { Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(16.dp)) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Báo cáo Tháng", fontWeight = FontWeight.SemiBold, fontSize = 13.sp) },
                icon = { Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(16.dp)) }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Period Selector Chips
        if (selectedTab == 0) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                weekOptions.forEachIndexed { index, triple ->
                    FilterChip(
                        selected = selectedWeekIndex == index,
                        onClick = { selectedWeekIndex = index },
                        label = { Text(triple.first, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SchoolPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                monthOptions.forEachIndexed { index, month ->
                    FilterChip(
                        selected = selectedMonthIndex == index,
                        onClick = { selectedMonthIndex = index },
                        label = { Text(month, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SchoolPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Big Action Buttons: Export Excel (.xls) & Copy Summary
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    val file = if (selectedTab == 0) {
                        ExcelReportExporter.exportWeeklyReportXls(
                            context = context,
                            periodTitle = currentWeek.second,
                            dayHeaders = currentWeek.third,
                            students = students,
                            attendanceRecords = allAttendanceRecords,
                            behaviorLogs = behaviorLogs
                        )
                    } else {
                        ExcelReportExporter.exportMonthlyReportXls(
                            context = context,
                            monthYearTitle = monthOptions[selectedMonthIndex],
                            students = students,
                            attendanceRecords = allAttendanceRecords,
                            behaviorLogs = behaviorLogs
                        )
                    }
                    generatedFile = file
                    showExportSuccessDialog = true
                },
                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .weight(1.3f)
                    .testTag("export_excel_button")
            ) {
                Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Xuất File Excel (.xls)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }

            OutlinedButton(
                onClick = {
                    val summaryText = ExcelReportExporter.buildTextSummary(
                        periodTitle = currentPeriodTitle,
                        students = students,
                        rows = reportRows
                    )
                    ZaloShareHelper.copyToClipboard(context, summaryText, "Báo cáo Lớp 5A")
                },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Sao chép Zalo", fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // BẢNG TỔNG HỢP XẾP LOẠI NỀ NẾP (Card exactly matching Sample PDF)
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "BẢNG TỔNG HỢP XẾP LOẠI NỀ NẾP (LỚP 5A)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = SchoolPrimary
                )
                Text(
                    text = "Sĩ số: $totalStudents học sinh • $currentPeriodTitle",
                    fontSize = 11.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    RankingSummaryItem(
                        label = "🌟 Tốt",
                        count = "$totCount em",
                        pct = "$totPct%",
                        bgColor = Color(0xFFDCFCE7),
                        textColor = Color(0xFF15803D),
                        modifier = Modifier.weight(1f)
                    )
                    RankingSummaryItem(
                        label = "👍 Khá",
                        count = "$khaCount em",
                        pct = "$khaPct%",
                        bgColor = Color(0xFFE0F2FE),
                        textColor = Color(0xFF0369A1),
                        modifier = Modifier.weight(1f)
                    )
                    RankingSummaryItem(
                        label = "📋 Đạt",
                        count = "$datCount em",
                        pct = "$datPct%",
                        bgColor = Color(0xFFFEF3C7),
                        textColor = Color(0xFFB45309),
                        modifier = Modifier.weight(1f)
                    )
                    RankingSummaryItem(
                        label = "⚠️ Cố gắng",
                        count = "$cogangCount em",
                        pct = "$cogangPct%",
                        bgColor = Color(0xFFFEE2E2),
                        textColor = Color(0xFFB91C1C),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // BẢNG CHI TIẾT 31 HỌC SINH (Preview)
        Text(
            text = "DANH SÁCH CHI TIẾT 31 HỌC SINH LỚP 5A",
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = SchoolPrimary
        )
        Text(
            text = "Bấm vào học sinh để gửi báo cáo chuyên cần tuần/tháng riêng qua Zalo cho Phụ huynh",
            fontSize = 11.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Student rows
        reportRows.forEach { row ->
            StudentReportCard(
                row = row,
                periodTitle = currentPeriodTitle,
                onSendZalo = {
                    val s = row.student
                    val individualMsg = """
                        📋 [BÁO CÁO TỔNG KẾT HỌC TẬP & NỀ NẾP - LỚP 5A]
                        Kính gửi Quý Phụ huynh em: ${s.fullName} (STT: ${s.stt})
                        Thời gian: $currentPeriodTitle
                        ------------------------------------------
                        • Đi học có mặt: ${row.presentCount} buổi
                        • Nghỉ có phép: ${row.excusedCount} buổi
                        • Vắng không phép: ${row.unexcusedCount} buổi
                        • Đi trễ: ${row.lateCount} buổi
                        • Lượt phát biểu khen ngợi: ${row.speechPraiseCount}
                        • Nhắc nhở nề nếp: ${row.personalWorkRemindCount + row.talkingRemindCount + row.choreRemindCount + row.homeworkRemindCount}
                        • Xếp loại Nề nếp tuần: ${row.conductRanking.uppercase()}
                        • Nhận xét của GVCN: ${row.note}
                        ------------------------------------------
                        Link nhóm Zalo Lớp 5A: ${ZaloShareHelper.CLASS_ZALO_GROUP_URL}
                        Kính mong Quý Phụ huynh tiếp tục đồng hành cùng nhà trường!
                        GVCN: Nguyễn Hoàng Tuấn
                    """.trimIndent()
                    onSendMessage(s.parentName, s.parentPhone, individualMsg)
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Signatures Preview Card
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Ghi chú ký hiệu:", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                Text("✓: Có mặt | P: Nghỉ có phép | KP: Nghỉ không phép | T: Đi trễ", fontSize = 11.sp, color = Color.DarkGray)

                Spacer(modifier = Modifier.height(6.dp))
                Divider()
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                        Text("Ban đại diện CMHS", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        Text("(Ký và ghi rõ họ tên)", fontStyle = FontStyle.Italic, fontSize = 9.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(28.dp))
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                        Text("Duyệt của Hiệu trưởng", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        Text("(Ký và ghi rõ họ tên)", fontStyle = FontStyle.Italic, fontSize = 9.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(14.dp))
                        Text("Trương Thị Kim Dương", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = SchoolPrimary)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                        Text("GVCN Lớp 5A", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        Text("(Ký và ghi rõ họ tên)", fontStyle = FontStyle.Italic, fontSize = 9.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(14.dp))
                        Text("Nguyễn Hoàng Tuấn", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = SchoolPrimary)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }

    // Dialog after exporting Excel file
    if (showExportSuccessDialog && generatedFile != null) {
        val file = generatedFile!!
        AlertDialog(
            onDismissRequest = { showExportSuccessDialog = false },
            icon = {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(36.dp))
            },
            title = {
                Text("Xuất File Excel Thành Công!", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            },
            text = {
                Column {
                    Text("Đã tạo file báo cáo định dạng Excel (.xls) theo đúng chuẩn mẫu biểu:")
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        color = Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "📁 ${file.name}\n(Dung lượng: ${file.length() / 1024 + 1} KB)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Bạn muốn thực hiện thao tác nào tiếp theo?", fontSize = 12.sp, color = Color.DarkGray)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        ExcelReportExporter.shareExcelFile(context, file, "Gửi Báo Cáo Excel Lớp 5A")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0068FF))
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Gửi file qua Zalo", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                Row {
                    OutlinedButton(
                        onClick = {
                            ExcelReportExporter.openExcelFile(context, file)
                        }
                    ) {
                        Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Mở file", fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    TextButton(onClick = { showExportSuccessDialog = false }) {
                        Text("Đóng")
                    }
                }
            }
        )
    }
}

@Composable
private fun RankingSummaryItem(
    label: String,
    count: String,
    pct: String,
    bgColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = bgColor,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = label, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = textColor)
            Text(text = count, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, color = textColor)
            Text(text = pct, fontSize = 10.sp, color = textColor.copy(alpha = 0.8f))
        }
    }
}

@Composable
private fun StudentReportCard(
    row: StudentReportRow,
    periodTitle: String,
    onSendZalo: () -> Unit
) {
    val s = row.student
    val rankColor = when (row.conductRanking) {
        "Tốt" -> SuccessGreen
        "Khá" -> Color(0xFF0284C7)
        "Đạt" -> Color(0xFFD97706)
        else -> ErrorRed
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    StudentAvatar(stt = s.stt, gender = s.gender, size = 34)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(text = "${s.stt}. ${s.fullName}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(
                            text = "${s.gender} • Ngày sinh: ${s.dob} • SĐT: ${s.parentPhone}",
                            fontSize = 10.sp,
                            color = Color.Gray
                        )
                    }
                }

                Surface(
                    color = rankColor.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = row.conductRanking,
                        color = rankColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Day Statuses Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                row.dayStatuses.forEachIndexed { i, status ->
                    val color = when (status) {
                        "✓" -> SuccessGreen
                        "P" -> Color(0xFF0284C7)
                        "KP" -> ErrorRed
                        "T" -> Color(0xFFD97706)
                        else -> Color.Gray
                    }
                    Surface(
                        color = Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "T${i + 2}", fontSize = 9.sp, color = Color.Gray)
                            Text(text = status, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = color)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Stats & note
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Có mặt: ${row.presentCount} | Phát biểu: ${row.speechPraiseCount} | Nhắc nhở: ${row.personalWorkRemindCount + row.talkingRemindCount + row.choreRemindCount + row.homeworkRemindCount}",
                        fontSize = 10.sp,
                        color = Color.DarkGray
                    )
                    Text(
                        text = "💬 ${row.note}",
                        fontSize = 10.sp,
                        fontStyle = FontStyle.Italic,
                        color = Color.Gray,
                        maxLines = 1
                    )
                }

                Button(
                    onClick = onSendZalo,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0068FF)),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Gửi Zalo PH", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
