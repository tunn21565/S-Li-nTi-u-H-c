package com.example.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.example.data.model.AttendanceRecord
import com.example.data.model.AttendanceStatus
import com.example.data.model.BehaviorLog
import com.example.data.model.BehaviorType
import com.example.data.model.SchoolRequest
import com.example.data.model.SemesterResult
import com.example.data.model.Student

object ZaloShareHelper {

    const val CLASS_ZALO_GROUP_URL = "https://zalo.me/g/ehbagp244"

    fun openClassZaloGroup(context: Context) {
        try {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(CLASS_ZALO_GROUP_URL)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(browserIntent)
        } catch (e: Exception) {
            Toast.makeText(context, "Không thể mở nhóm Zalo: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    // Clean phone number (e.g. 0912345001 -> 84912345001 or standard 0912345001)
    fun sanitizePhone(phone: String): String {
        return phone.filter { it.isDigit() }
    }

    // --- Message Builders ---

    fun buildAttendanceMessage(student: Student, record: AttendanceRecord): String {
        val statusText = when (record.status) {
            AttendanceStatus.DI_HOC -> "✅ Có mặt đi học đầy đủ và đúng giờ."
            AttendanceStatus.DI_TRE -> "⚠️ Đi học trễ (đến lớp muộn)."
            AttendanceStatus.NGHI_CO_PHEP -> "📋 Nghỉ học có phép."
            AttendanceStatus.NGHI_KHONG_PHEP -> "❌ Vắng học không phép (Chưa có lý do)."
        }

        val noteText = if (record.note.isNotBlank()) "\n- Ghi chú: ${record.note}" else ""

        val callToAction = when (record.status) {
            AttendanceStatus.DI_HOC -> "Nhà trường rất biểu dương tinh thần đi học chuyên cần của em."
            AttendanceStatus.DI_TRE -> "Kính nhờ Quý Phụ huynh nhắc nhở em thức dậy và xuất phát sớm hơn để đảm bảo giờ giấc học tập."
            AttendanceStatus.NGHI_CO_PHEP -> "Kính mong phụ huynh theo dõi sức khỏe và hỗ trợ em ôn bài trong thời gian nghỉ."
            AttendanceStatus.NGHI_KHONG_PHEP -> "Kính đề nghị Quý Phụ huynh liên hệ ngay với giáo viên chủ nhiệm để thông tin lý do vắng của em, nhằm phối hợp cùng nhà trường quản lý học sinh an toàn."
        }

        return """
            🏫 [TRƯỜNG TIỂU HỌC - SỔ LIÊN LẠC LỚP 5A]
            📋 THÔNG BÁO CHUYÊN CẦN NGÀY ${record.date}

            Kính gửi Quý Phụ huynh em: ${student.fullName} (STT: ${student.stt})
            
            Tình hình chuyên cần hôm nay của em:
            - Trạng thái: $statusText$noteText

            $callToAction

            Trân trọng,
            Giáo viên chủ nhiệm Lớp 5A
            Điện thoại liên hệ: GVCN Lớp 5A
        """.trimIndent()
    }

    fun buildBehaviorMessage(student: Student, log: BehaviorLog): String {
        val header = if (log.type == BehaviorType.PRAISE) {
            "🌟 [BIỂU DƯƠNG RÈN LUYỆN & HỌC TẬP - LỚP 5A]"
        } else {
            "⚠️ [NHẮC NHỞ NỀ NẾP & UỐN NẮN - LỚP 5A]"
        }

        val actionAdvice = if (log.type == BehaviorType.PRAISE) {
            "Gia đình và nhà trường cùng ghi nhận sự cố gắng của em. Quý Phụ huynh hãy dành lời khen để động viên em tiếp tục phát huy nhé!"
        } else {
            "Để giúp em hoàn thiện bản thân, kính nhờ Quý Phụ huynh cùng phối hợp với nhà trường trò chuyện, uốn nắn và nhắc nhở em khắc phục ngay trong những ngày tới."
        }

        val detailText = if (log.detail.isNotBlank()) "\n- Chi tiết: ${log.detail}" else ""

        return """
            $header
            Ngày: ${log.date}

            Kính gửi Quý Phụ huynh em: ${student.fullName} (STT: ${student.stt})

            Giáo viên chủ nhiệm xin thông tin đến gia đình về tình hình rèn luyện của em:
            - Nội dung: ${log.category}$detailText

            $actionAdvice

            Trân trọng cảm ơn sự đồng hành của Quý Phụ huynh!
            Giáo viên chủ nhiệm Lớp 5A
        """.trimIndent()
    }

    fun buildSchoolRequestMessage(student: Student, request: SchoolRequest): String {
        val deadlineText = if (request.deadline.isNotBlank()) "\n- Thời hạn thực hiện: ${request.deadline}" else ""

        return """
            📢 [YÊU CẦU TỪ NHÀ TRƯỜNG - LỚP 5A]
            Dành riêng cho học sinh: ${student.fullName} (STT: ${student.stt})

            Kính gửi: ${student.parentName}
            Giáo viên chủ nhiệm xin gửi thông báo riêng của nhà trường đến gia đình:

            📌 Tiêu đề: ${request.title}
            📝 Nội dung:
            ${request.content}$deadlineText

            Kính mong Quý Phụ huynh lưu ý phối hợp thực hiện và phản hồi cho GVCN sớm nhất.
            Trân trọng cảm ơn Quý Phụ huynh!
            
            Giáo viên chủ nhiệm Lớp 5A
        """.trimIndent()
    }

    fun buildSemesterResultMessage(student: Student, result: SemesterResult): String {
        return """
            🎓 [KẾT QUẢ ĐÁNH GIÁ HỌC TẬP - ${result.term.title.uppercase()}]
            Trường Tiểu học - Lớp 5A
            
            Kính gửi: ${student.parentName}
            Giáo viên chủ nhiệm trân trọng thông báo kết quả học tập của em: ${student.fullName} (STT: ${student.stt})

            📊 BẢNG ĐIỂM CÁC MÔN HỌC:
            • Toán: ${result.mathScore} điểm
            • Tiếng Việt: ${result.vietnameseScore} điểm
            • Tiếng Anh: ${result.englishScore} điểm
            • Khoa học: ${result.scienceScore} điểm
            • Lịch sử & Địa lý: ${result.historyGeoScore} điểm
            • Tin học & Công nghệ: ${result.informaticsScore} điểm
            -----------------------------
            🎯 Điểm trung bình: ${result.averageScore} điểm
            🌟 Đánh giá Năng lực - Phẩm chất: ${result.conduct}
            🏆 Danh hiệu khen thưởng: ${result.title}

            💬 Nhận xét của Giáo viên chủ nhiệm:
            "${result.teacherNote}"

            Cảm ơn Quý Phụ huynh đã luôn đồng hành, sát cánh cùng nhà trường vì sự tiến bộ của em!
            Trân trọng,
            Giáo viên chủ nhiệm Lớp 5A
        """.trimIndent()
    }

    // --- Actions: Share, Zalo link, Copy, Dial ---

    fun copyToClipboard(context: Context, text: String, label: String = "Sổ Liên Lạc 5A") {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Đã sao chép nội dung tin nhắn!", Toast.LENGTH_SHORT).show()
    }

    fun shareMessage(context: Context, message: String, title: String = "Gửi tin nhắn cho Phụ huynh") {
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, message)
            type = "text/plain"
        }
        val chooser = Intent.createChooser(sendIntent, title)
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }

    fun openZaloChat(context: Context, phone: String, message: String? = null) {
        val cleanPhone = sanitizePhone(phone)
        // Try opening via zalo.me link
        val zaloUrl = "https://zalo.me/$cleanPhone"
        try {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(zaloUrl)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(browserIntent)
            if (!message.isNullOrBlank()) {
                copyToClipboard(context, message)
                Toast.makeText(context, "Đã mở Zalo và sao chép sẵn tin nhắn để bạn dán!", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            if (!message.isNullOrBlank()) {
                shareMessage(context, message)
            } else {
                Toast.makeText(context, "Không thể mở Zalo: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun dialPhone(context: Context, phone: String) {
        try {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Không thể gọi điện: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }
}
