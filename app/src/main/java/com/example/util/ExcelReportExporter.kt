package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.data.model.AttendanceRecord
import com.example.data.model.AttendanceStatus
import com.example.data.model.BehaviorLog
import com.example.data.model.BehaviorType
import com.example.data.model.Student
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets

data class StudentReportRow(
    val student: Student,
    val dayStatuses: List<String>, // e.g. ["✓", "✓", "✓", "✓", "-"]
    val presentCount: Int,
    val excusedCount: Int,
    val unexcusedCount: Int,
    val lateCount: Int,
    val speechPraiseCount: Int, // Phát biểu
    val chorePraiseCount: Int, // Trực nhật / bài tập tốt
    val personalWorkRemindCount: Int, // Việc riêng
    val talkingRemindCount: Int, // Nói chuyện
    val choreRemindCount: Int, // K.trực nhật
    val homeworkRemindCount: Int, // K.viết bài / K.làm bài
    val conductRanking: String, // "Tốt", "Khá", "Đạt", "Cần cố gắng"
    val note: String
)

object ExcelReportExporter {

    fun computeReportRows(
        students: List<Student>,
        dayDates: List<String>,
        attendanceRecords: List<AttendanceRecord>,
        behaviorLogs: List<BehaviorLog>
    ): List<StudentReportRow> {
        val attendanceByStudentAndDate = attendanceRecords.groupBy { it.studentId to it.date }
        val behaviorByStudent = behaviorLogs.groupBy { it.studentId }

        return students.map { student ->
            val dayStatuses = dayDates.map { date ->
                val record = attendanceByStudentAndDate[student.id to date]?.firstOrNull()
                when (record?.status) {
                    AttendanceStatus.DI_HOC -> "✓"
                    AttendanceStatus.NGHI_CO_PHEP -> "P"
                    AttendanceStatus.NGHI_KHONG_PHEP -> "KP"
                    AttendanceStatus.DI_TRE -> "T"
                    null -> "-"
                }
            }

            // Attendance sums over the selected dates
            val studentRecords = dayDates.mapNotNull { date ->
                attendanceByStudentAndDate[student.id to date]?.firstOrNull()
            }
            val presentCount = studentRecords.count { it.status == AttendanceStatus.DI_HOC }
            val excusedCount = studentRecords.count { it.status == AttendanceStatus.NGHI_CO_PHEP }
            val unexcusedCount = studentRecords.count { it.status == AttendanceStatus.NGHI_KHONG_PHEP }
            val lateCount = studentRecords.count { it.status == AttendanceStatus.DI_TRE }

            // Behaviors
            val studentLogs = behaviorByStudent[student.id].orEmpty().filter { log ->
                dayDates.isEmpty() || dayDates.contains(log.date)
            }

            val speechPraise = studentLogs.count { it.type == BehaviorType.PRAISE && (it.category.contains("phát biểu", true) || it.category.contains("bài", true)) }
            val chorePraise = studentLogs.count { it.type == BehaviorType.PRAISE && !it.category.contains("phát biểu", true) && !it.category.contains("bài", true) }

            val personalWorkRemind = studentLogs.count { it.type == BehaviorType.REMIND && it.category.contains("riêng", true) }
            val talkingRemind = studentLogs.count { it.type == BehaviorType.REMIND && it.category.contains("nói chuyện", true) }
            val choreRemind = studentLogs.count { it.type == BehaviorType.REMIND && it.category.contains("trực nhật", true) }
            val homeworkRemind = studentLogs.count { it.type == BehaviorType.REMIND && (it.category.contains("viết bài", true) || it.category.contains("bài tập", true) || it.category.contains("nón bảo hiểm", true) || it.category.contains("nội quy", true)) }

            val totalRemind = studentLogs.count { it.type == BehaviorType.REMIND }

            // Ranking logic:
            // Tốt: 0 nhắc nhở, không vắng không phép
            // Khá: <= 1 nhắc nhở, không vắng không phép
            // Đạt: 2 nhắc nhở hoặc 1 vắng không phép
            // Cần cố gắng: >= 3 nhắc nhở hoặc >= 2 vắng không phép
            val conductRanking = when {
                totalRemind == 0 && unexcusedCount == 0 -> "Tốt"
                totalRemind <= 1 && unexcusedCount == 0 -> "Khá"
                totalRemind <= 2 -> "Đạt"
                else -> "Cần cố gắng"
            }

            // Note
            val note = when {
                speechPraise > 0 -> "Hăng hái phát biểu, tích cực xây dựng bài"
                conductRanking == "Tốt" -> "Chuyên cần tốt, chấp hành nghiêm nội quy"
                totalRemind > 0 -> studentLogs.firstOrNull { it.type == BehaviorType.REMIND }?.category ?: "Cần cố gắng rèn luyện thêm"
                else -> "Ngoan, lễ phép"
            }

            StudentReportRow(
                student = student,
                dayStatuses = dayStatuses,
                presentCount = presentCount,
                excusedCount = excusedCount,
                unexcusedCount = unexcusedCount,
                lateCount = lateCount,
                speechPraiseCount = speechPraise,
                chorePraiseCount = chorePraise,
                personalWorkRemindCount = personalWorkRemind,
                talkingRemindCount = talkingRemind,
                choreRemindCount = choreRemind,
                homeworkRemindCount = homeworkRemind,
                conductRanking = conductRanking,
                note = note
            )
        }
    }

    fun exportWeeklyReportXls(
        context: Context,
        periodTitle: String, // e.g. "Tuần từ 14/09/2026 đến 18/09/2026"
        dayHeaders: List<Pair<String, String>>, // e.g. [("Thứ 2", "14-Sep"), ("Thứ 3", "15-Sep"), ...]
        students: List<Student>,
        attendanceRecords: List<AttendanceRecord>,
        behaviorLogs: List<BehaviorLog>
    ): File {
        val dates = dayHeaders.map { pair ->
            // Pair second might be "14-Sep", map to yyyy-MM-dd or match records
            // We find all records where date matches the day
            pair.second
        }

        // To reliably match, let's also accept full yyyy-MM-dd list if available
        val rows = computeReportRows(students, dates, attendanceRecords, behaviorLogs)
        return generateHtmlSpreadsheetFile(context, periodTitle, dayHeaders, rows, "Bao_Cao_Tong_Ket_Tuan_Lop_5A.xls")
    }

    fun exportMonthlyReportXls(
        context: Context,
        monthYearTitle: String, // e.g. "Tháng 09/2026"
        students: List<Student>,
        attendanceRecords: List<AttendanceRecord>,
        behaviorLogs: List<BehaviorLog>
    ): File {
        val dates = attendanceRecords.map { it.date }.distinct().sorted()
        val dayHeaders = if (dates.isNotEmpty()) {
            dates.take(5).map { date ->
                val dayNum = date.split("-").lastOrNull() ?: ""
                "Ngày" to "$dayNum-Th9"
            }
        } else {
            listOf("Thứ 2" to "14-Sep", "Thứ 3" to "15-Sep", "Thứ 4" to "16-Sep", "Thứ 5" to "17-Sep", "Thứ 6" to "18-Sep")
        }

        val rows = computeReportRows(students, dates, attendanceRecords, behaviorLogs)
        return generateHtmlSpreadsheetFile(context, "Tháng: $monthYearTitle", dayHeaders, rows, "Bao_Cao_Tong_Ket_Thang_Lop_5A.xls")
    }

    private fun generateHtmlSpreadsheetFile(
        context: Context,
        periodTitle: String,
        dayHeaders: List<Pair<String, String>>,
        rows: List<StudentReportRow>,
        fileName: String
    ): File {
        val reportsDir = File(context.cacheDir, "reports").apply { mkdirs() }
        val file = File(reportsDir, fileName)

        val totalStudents = rows.size
        val totCount = rows.count { it.conductRanking == "Tốt" }
        val khaCount = rows.count { it.conductRanking == "Khá" }
        val datCount = rows.count { it.conductRanking == "Đạt" }
        val cogangCount = rows.count { it.conductRanking == "Cần cố gắng" }

        val totPct = if (totalStudents > 0) String.format("%.1f", totCount * 100.0 / totalStudents) else "0"
        val khaPct = if (totalStudents > 0) String.format("%.1f", khaCount * 100.0 / totalStudents) else "0"
        val datPct = if (totalStudents > 0) String.format("%.1f", datCount * 100.0 / totalStudents) else "0"
        val cogangPct = if (totalStudents > 0) String.format("%.1f", cogangCount * 100.0 / totalStudents) else "0"

        val sb = StringBuilder()
        sb.append("<!DOCTYPE html>\n")
        sb.append("<html xmlns:o=\"urn:schemas-microsoft-com:office:office\" xmlns:x=\"urn:schemas-microsoft-com:office:excel\" xmlns=\"http://www.w3.org/TR/REC-html40\">\n")
        sb.append("<head>\n")
        sb.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\n")
        sb.append("<!--[if gte mso 9]><xml><x:ExcelWorkbook><x:ExcelWorksheets><x:ExcelWorksheet><x:Name>Báo Cáo Lớp 5A</x:Name><x:WorksheetOptions><x:DisplayGridlines/></x:WorksheetOptions></x:ExcelWorksheet></x:ExcelWorksheets></x:ExcelWorkbook></xml><![endif]-->\n")
        sb.append("<style>\n")
        sb.append("  body { font-family: 'Times New Roman', Arial, sans-serif; font-size: 11pt; color: #1e293b; }\n")
        sb.append("  table { border-collapse: collapse; width: 100%; margin-bottom: 20px; }\n")
        sb.append("  th, td { border: 1px solid #94a3b8; padding: 6px 8px; font-size: 10pt; }\n")
        sb.append("  th { background-color: #1e3a8a; color: #ffffff; text-align: center; font-weight: bold; }\n")
        sb.append("  th.sub-header { background-color: #3b82f6; color: #ffffff; font-weight: bold; }\n")
        sb.append("  .text-center { text-align: center; }\n")
        sb.append("  .text-left { text-align: left; }\n")
        sb.append("  .text-right { text-align: right; }\n")
        sb.append("  .title-school { font-size: 12pt; font-weight: bold; color: #1e3a8a; }\n")
        sb.append("  .title-main { font-size: 16pt; font-weight: bold; color: #1e3a8a; text-align: center; padding: 10px 0 4px 0; }\n")
        sb.append("  .subtitle { font-size: 12pt; font-style: italic; color: #475569; text-align: center; margin-bottom: 15px; }\n")
        sb.append("  .zalo-link { font-size: 11pt; color: #0068ff; text-align: center; font-weight: bold; margin-bottom: 10px; }\n")
        sb.append("  .badge-tot { background-color: #dcfce7; color: #15803d; font-weight: bold; text-align: center; }\n")
        sb.append("  .badge-kha { background-color: #e0f2fe; color: #0369a1; font-weight: bold; text-align: center; }\n")
        sb.append("  .badge-dat { background-color: #fef3c7; color: #b45309; font-weight: bold; text-align: center; }\n")
        sb.append("  .badge-cogang { background-color: #fee2e2; color: #b91c1c; font-weight: bold; text-align: center; }\n")
        sb.append("  .summary-table th { background-color: #0f766e; color: #ffffff; }\n")
        sb.append("  .summary-table td { font-size: 11pt; padding: 8px 12px; }\n")
        sb.append("  .signatures td { border: none; padding-top: 30px; text-align: center; font-size: 11pt; }\n")
        sb.append("</style>\n")
        sb.append("</head>\n")
        sb.append("<body>\n")

        // Official Header
        sb.append("<table style='border:none; margin-bottom: 10px;'>\n")
        sb.append("  <tr style='border:none;'>\n")
        sb.append("    <td style='border:none; width: 50%; text-align: center; vertical-align: top;'>\n")
        sb.append("      <div class='title-school'>UBND XÃ TÂN THẠNH</div>\n")
        sb.append("      <div style='font-weight: bold; font-size: 11pt;'>TRƯỜNG TIỂU HỌC TÂN THẠNH</div>\n")
        sb.append("      <div style='font-size: 10pt; color: #64748b;'>LỚP 5A - PHÂN HIỆU TÂN BÌNH</div>\n")
        sb.append("    </td>\n")
        sb.append("    <td style='border:none; width: 50%; text-align: center; vertical-align: top;'>\n")
        sb.append("      <div style='font-weight: bold; font-size: 11pt;'>CỘNG HÒA XÃ HỘI CHỦ NGHĨA VIỆT NAM</div>\n")
        sb.append("      <div style='font-weight: bold; font-size: 10pt;'>Độc lập - Tự do - Hạnh phúc</div>\n")
        sb.append("      <div style='font-size: 9pt; color: #64748b;'>---------------</div>\n")
        sb.append("    </td>\n")
        sb.append("  </tr>\n")
        sb.append("</table>\n")

        // Title
        sb.append("<div class='title-main'>BÁO CÁO ĐIỂM DANH & SINH HOẠT - LỚP 5A</div>\n")
        sb.append("<div class='subtitle'>Báo cáo chuyên cần & sinh hoạt: ").append(periodTitle).append(" • Năm học 2026 - 2027</div>\n")
        sb.append("<div class='zalo-link'>Đường link Nhóm Zalo Lớp 5A: <a href='").append(ZaloShareHelper.CLASS_ZALO_GROUP_URL).append("'>https://zalo.me/g/ehbagp244</a></div>\n")

        // Main Table
        sb.append("<table border='1'>\n")
        sb.append("  <thead>\n")
        sb.append("    <tr>\n")
        sb.append("      <th rowspan='2' style='width: 35px;'>STT</th>\n")
        sb.append("      <th rowspan='2' style='min-width: 160px;'>Họ và Tên</th>\n")
        sb.append("      <th rowspan='2' style='width: 55px;'>Giới tính</th>\n")
        sb.append("      <th rowspan='2' style='width: 85px;'>Ngày sinh</th>\n")
        sb.append("      <th colspan='").append(dayHeaders.size).append("'>Điểm danh từng ngày</th>\n")
        sb.append("      <th colspan='4'>Tổng kết chuyên cần</th>\n")
        sb.append("      <th colspan='2'>Khen ngợi (+)</th>\n")
        sb.append("      <th colspan='4'>Nhắc nhở (-)</th>\n")
        sb.append("      <th rowspan='2' style='width: 90px;'>Xếp loại Nề nếp</th>\n")
        sb.append("      <th rowspan='2' style='min-width: 180px;'>Ghi chú gửi PH</th>\n")
        sb.append("    </tr>\n")

        // Subheaders
        sb.append("    <tr>\n")
        dayHeaders.forEach { (weekday, dateLabel) ->
            sb.append("      <th class='sub-header'>").append(weekday).append("<br><span style='font-size:8pt;'>").append(dateLabel).append("</span></th>\n")
        }
        sb.append("      <th class='sub-header'>Có mặt</th>\n")
        sb.append("      <th class='sub-header'>Có phép</th>\n")
        sb.append("      <th class='sub-header'>K.phép</th>\n")
        sb.append("      <th class='sub-header'>Đi trễ</th>\n")
        sb.append("      <th class='sub-header'>Phát biểu</th>\n")
        sb.append("      <th class='sub-header'>Trực nhật tốt</th>\n")
        sb.append("      <th class='sub-header'>Việc riêng</th>\n")
        sb.append("      <th class='sub-header'>Nói chuyện</th>\n")
        sb.append("      <th class='sub-header'>K.trực nhật</th>\n")
        sb.append("      <th class='sub-header'>K.viết bài</th>\n")
        sb.append("    </tr>\n")
        sb.append("  </thead>\n")

        sb.append("  <tbody>\n")
        rows.forEach { r ->
            val rankClass = when (r.conductRanking) {
                "Tốt" -> "badge-tot"
                "Khá" -> "badge-kha"
                "Đạt" -> "badge-dat"
                else -> "badge-cogang"
            }

            sb.append("    <tr>\n")
            sb.append("      <td class='text-center'>").append(r.student.stt).append("</td>\n")
            sb.append("      <td class='text-left'><b>").append(r.student.fullName).append("</b></td>\n")
            sb.append("      <td class='text-center'>").append(r.student.gender).append("</td>\n")
            sb.append("      <td class='text-center'>").append(r.student.dob).append("</td>\n")

            // Days
            r.dayStatuses.forEach { status ->
                val color = when (status) {
                    "✓" -> "color:#15803d; font-weight:bold;"
                    "P" -> "color:#0284c7; font-weight:bold;"
                    "KP" -> "color:#dc2626; font-weight:bold;"
                    "T" -> "color:#d97706; font-weight:bold;"
                    else -> "color:#94a3b8;"
                }
                sb.append("      <td class='text-center' style='").append(color).append("'>").append(status).append("</td>\n")
            }

            // Attendance summary
            sb.append("      <td class='text-center'>").append(if (r.presentCount > 0) r.presentCount else "-").append("</td>\n")
            sb.append("      <td class='text-center'>").append(if (r.excusedCount > 0) r.excusedCount else "-").append("</td>\n")
            sb.append("      <td class='text-center'>").append(if (r.unexcusedCount > 0) r.unexcusedCount else "-").append("</td>\n")
            sb.append("      <td class='text-center'>").append(if (r.lateCount > 0) r.lateCount else "-").append("</td>\n")

            // Praise
            sb.append("      <td class='text-center'>").append(if (r.speechPraiseCount > 0) r.speechPraiseCount else "-").append("</td>\n")
            sb.append("      <td class='text-center'>").append(if (r.chorePraiseCount > 0) r.chorePraiseCount else "-").append("</td>\n")

            // Reminders
            sb.append("      <td class='text-center'>").append(if (r.personalWorkRemindCount > 0) r.personalWorkRemindCount else "-").append("</td>\n")
            sb.append("      <td class='text-center'>").append(if (r.talkingRemindCount > 0) r.talkingRemindCount else "-").append("</td>\n")
            sb.append("      <td class='text-center'>").append(if (r.choreRemindCount > 0) r.choreRemindCount else "-").append("</td>\n")
            sb.append("      <td class='text-center'>").append(if (r.homeworkRemindCount > 0) r.homeworkRemindCount else "-").append("</td>\n")

            // Conduct Ranking
            sb.append("      <td class='").append(rankClass).append("'>").append(r.conductRanking).append("</td>\n")
            sb.append("      <td class='text-left'>").append(r.note).append("</td>\n")
            sb.append("    </tr>\n")
        }
        sb.append("  </tbody>\n")
        sb.append("</table>\n")

        // Summary Table (BẢNG TỔNG HỢP XẾP LOẠI NỀ NẾP Lớp 5A)
        sb.append("<div style='margin-top: 20px; font-weight: bold; font-size: 13pt; color: #0f766e;'>BẢNG TỔNG HỢP XẾP LOẠI NỀ NẾP (Lớp 5A - Sĩ số: ").append(totalStudents).append(" học sinh)</div>\n")
        sb.append("<table class='summary-table' border='1' style='width: 70%; max-width: 600px;'>\n")
        sb.append("  <tr>\n")
        sb.append("    <th>Mức xếp loại nề nếp</th>\n")
        sb.append("    <th>Số lượng học sinh</th>\n")
        sb.append("    <th>Tỷ lệ (%)</th>\n")
        sb.append("  </tr>\n")
        sb.append("  <tr>\n")
        sb.append("    <td><b>🌟 Tốt</b> (Mẫu mực, không vi phạm)</td>\n")
        sb.append("    <td class='text-center'><b>").append(totCount).append(" em</b></td>\n")
        sb.append("    <td class='text-center'><b>").append(totPct).append("%</b></td>\n")
        sb.append("  </tr>\n")
        sb.append("  <tr>\n")
        sb.append("    <td><b>👍 Khá</b> (Nề nếp tốt, vi phạm ít)</td>\n")
        sb.append("    <td class='text-center'>").append(khaCount).append(" em</td>\n")
        sb.append("    <td class='text-center'>").append(khaPct).append("%</td>\n")
        sb.append("  </tr>\n")
        sb.append("  <tr>\n")
        sb.append("    <td><b>📋 Đạt</b> (Cơ bản chấp hành)</td>\n")
        sb.append("    <td class='text-center'>").append(datCount).append(" em</td>\n")
        sb.append("    <td class='text-center'>").append(datPct).append("%</td>\n")
        sb.append("  </tr>\n")
        sb.append("  <tr>\n")
        sb.append("    <td><b>⚠️ Cần cố gắng</b> (Cần rèn luyện thêm)</td>\n")
        sb.append("    <td class='text-center'>").append(cogangCount).append(" em</td>\n")
        sb.append("    <td class='text-center'>").append(cogangPct).append("%</td>\n")
        sb.append("  </tr>\n")
        sb.append("</table>\n")

        // Legend & Teacher Comment
        sb.append("<div style='margin-top: 15px; font-size: 10pt; color: #334155;'>\n")
        sb.append("  <b>* Ghi chú ký hiệu:</b> ✓: Có mặt | P: Nghỉ có phép | KP: Nghỉ không phép | T: Đi trễ<br>\n")
        sb.append("  <b>* Ý kiến của Giáo viên chủ nhiệm:</b> Kính gửi quý Phụ huynh học sinh Lớp 5A kết quả điểm danh và các mặt sinh hoạt trên lớp của các em. Kính mong quý phụ huynh tiếp tục quan tâm, phối hợp chặt chẽ cùng nhà trường để uốn nắn, bồi dưỡng các em ngày càng tiến bộ hơn nữa.<br>\n")
        sb.append("</div>\n")

        // Signatures
        sb.append("<table class='signatures' style='margin-top: 30px;'>\n")
        sb.append("  <tr>\n")
        sb.append("    <td style='width: 33%; vertical-align: top;'>\n")
        sb.append("      <b>Ban đại diện Cha mẹ học sinh</b><br>\n")
        sb.append("      <i>(Ký và ghi rõ họ tên)</i><br><br><br><br><br>\n")
        sb.append("    </td>\n")
        sb.append("    <td style='width: 33%; vertical-align: top;'>\n")
        sb.append("      <b>Duyệt của Hiệu trưởng</b><br>\n")
        sb.append("      <i>(Ký và ghi rõ họ tên)</i><br><br><br><br>\n")
        sb.append("      <b>Trương Thị Kim Dương</b>\n")
        sb.append("    </td>\n")
        sb.append("    <td style='width: 34%; vertical-align: top;'>\n")
        sb.append("      <i>Tân Thạnh, ngày 18 tháng 09 năm 2026</i><br>\n")
        sb.append("      <b>Giáo viên chủ nhiệm Lớp 5A</b><br>\n")
        sb.append("      <i>(Ký và ghi rõ họ tên)</i><br><br><br><br>\n")
        sb.append("      <b>Nguyễn Hoàng Tuấn</b>\n")
        sb.append("    </td>\n")
        sb.append("  </tr>\n")
        sb.append("</table>\n")

        sb.append("</body>\n")
        sb.append("</html>\n")

        // Write file with UTF-8 BOM
        FileOutputStream(file).use { fos ->
            // UTF-8 BOM bytes
            fos.write(0xEF)
            fos.write(0xBB)
            fos.write(0xBF)
            OutputStreamWriter(fos, StandardCharsets.UTF_8).use { writer ->
                writer.write(sb.toString())
            }
        }

        return file
    }

    fun shareExcelFile(context: Context, file: File, title: String = "Gửi file Báo Cáo Excel Lớp 5A") {
        try {
            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/vnd.ms-excel"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, file.nameWithoutExtension)
                putExtra(
                    Intent.EXTRA_TEXT,
                    "Kính gửi Quý Phụ huynh Lớp 5A file Excel Báo cáo tổng kết. Link nhóm Zalo lớp: ${ZaloShareHelper.CLASS_ZALO_GROUP_URL}"
                )
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooser = Intent.createChooser(shareIntent, title).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
        } catch (e: Exception) {
            Toast.makeText(context, "Không thể chia sẻ file Excel: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    fun openExcelFile(context: Context, file: File) {
        try {
            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val viewIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/vnd.ms-excel")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            context.startActivity(viewIntent)
        } catch (e: Exception) {
            Toast.makeText(context, "Không có ứng dụng nào để mở file Excel: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    fun buildTextSummary(
        periodTitle: String,
        students: List<Student>,
        rows: List<StudentReportRow>
    ): String {
        val total = rows.size
        val totCount = rows.count { it.conductRanking == "Tốt" }
        val khaCount = rows.count { it.conductRanking == "Khá" }
        val datCount = rows.count { it.conductRanking == "Đạt" }
        val cogangCount = rows.count { it.conductRanking == "Cần cố gắng" }

        val totPct = if (total > 0) String.format("%.1f", totCount * 100.0 / total) else "0"
        val khaPct = if (total > 0) String.format("%.1f", khaCount * 100.0 / total) else "0"

        return """
            📊 [BÁO CÁO TỔNG KẾT SINH HOẠT & CHUYÊN CẦN - LỚP 5A]
            Trường Tiểu học Tân Thạnh (Phân hiệu Tân Bình)
            Thời gian: $periodTitle
            GVCN: Nguyễn Hoàng Tuấn
            Nhóm Zalo lớp: ${ZaloShareHelper.CLASS_ZALO_GROUP_URL}
            ------------------------------------------
            📈 TỔNG HỢP XẾP LOẠI NỀ NẾP (Sĩ số: $total HS):
            • 🌟 Xếp loại Tốt: $totCount em ($totPct%)
            • 👍 Xếp loại Khá: $khaCount em ($khaPct%)
            • 📋 Xếp loại Đạt: $datCount em
            • ⚠️ Cần cố gắng: $cogangCount em
            ------------------------------------------
            Kính gửi Quý Phụ huynh xem chi tiết từng học sinh trong file Excel đính kèm. Kính chúc Quý Phụ huynh và các em học sinh sức khỏe, học tập tốt!
        """.trimIndent()
    }
}
