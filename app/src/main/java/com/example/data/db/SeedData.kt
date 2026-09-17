package com.example.data.db

import com.example.data.model.AttendanceRecord
import com.example.data.model.AttendanceStatus
import com.example.data.model.BehaviorLog
import com.example.data.model.BehaviorType
import com.example.data.model.SchoolRequest
import com.example.data.model.SemesterResult
import com.example.data.model.Student
import com.example.data.model.TermType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object SeedData {
    fun getTodayString(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    val officialStudents: List<Student> get() = students

    val students = listOf(
        Student(
            id = 1, stt = 1, fullName = "Nguyễn Bảo Anh", gender = "Nam", dob = "18/12/2016",
            parentPhone = "0363912959", parentName = "Phạm Thị Thùy Dương", notes = "",
            fatherName = "Nguyễn Hữu Cầu", fatherJob = "CN", motherName = "Phạm Thị Thùy Dương", motherJob = "CN",
            address = "Tân Bình", birthPlace = "BVĐK khu vực Cai Lậy, Đồng Tháp", citizenId = "080216001637"
        ),
        Student(
            id = 2, stt = 2, fullName = "Nguyễn Bách Thiên Ân", gender = "Nam", dob = "25/05/2016",
            parentPhone = "0901624532", parentName = "Nguyễn Thị Ngọc Giao", notes = "",
            fatherName = "Nguyễn Công Hậu", fatherJob = "CN", motherName = "Nguyễn Thị Ngọc Giao", motherJob = "CN",
            address = "Tân Bình", birthPlace = "Trung tâm y tế Xã Tân Thạnh, Tây Ninh", citizenId = "080216004823"
        ),
        Student(
            id = 3, stt = 3, fullName = "Nguyễn Minh Đăng", gender = "Nam", dob = "31/05/2016",
            parentPhone = "0344256690", parentName = "Lê Thị Diệu", notes = "",
            fatherName = "Nguyễn Minh Kỳ", fatherJob = "LR", motherName = "Lê Thị Diệu", motherJob = "LR",
            address = "Thạnh An", birthPlace = "Trung tâm y tế Xã Tân Thạnh, Tây Ninh", citizenId = "082216002070"
        ),
        Student(
            id = 4, stt = 4, fullName = "Nguyễn Quốc Đông", gender = "Nam", dob = "07/10/2016",
            parentPhone = "0354312291", parentName = "Nguyễn Thị Oanh", notes = "",
            fatherName = "", fatherJob = "", motherName = "Nguyễn Thị Oanh", motherJob = "LR",
            address = "Hiệp Thành", birthPlace = "BVĐK khu vực Cai Lậy, Đồng Tháp", citizenId = "080216009295"
        ),
        Student(
            id = 5, stt = 5, fullName = "Nguyễn Ngọc Kim Huệ", gender = "Nữ", dob = "16/08/2016",
            parentPhone = "0326393831", parentName = "Phạm Thị Phương", notes = "",
            fatherName = "Nguyễn Văn Sang", fatherJob = "LR", motherName = "Phạm Thị Phương", motherJob = "LR",
            address = "Hiệp Thành", birthPlace = "BV Phụ Sản Nhi Bình Dương,TP HCM", citizenId = "080316006104"
        ),
        Student(
            id = 6, stt = 6, fullName = "Nguyễn Ngọc Huy", gender = "Nam", dob = "12/11/2016",
            parentPhone = "0982609517", parentName = "Nguyễn Ngọc Phướng", notes = "",
            fatherName = "", fatherJob = "", motherName = "Nguyễn Ngọc Phướng", motherJob = "CN",
            address = "Tân Bình", birthPlace = "BVĐK khu vực Cai Lậy, Đồng Tháp", citizenId = "080216006073"
        ),
        Student(
            id = 7, stt = 7, fullName = "Ngô Thị Mai Huyền", gender = "Nữ", dob = "24/06/2016",
            parentPhone = "0388831622", parentName = "Nguyễn Thị Bé Thơ", notes = "",
            fatherName = "Ngô Văn Cang", fatherJob = "LR", motherName = "Nguyễn Thị Bé Thơ", motherJob = "LR",
            address = "Hiệp Thành", birthPlace = "BVĐK khu vực Cai Lậy, Đồng Tháp", citizenId = "080316012475"
        ),
        Student(
            id = 8, stt = 8, fullName = "Nguyễn Chí Khang", gender = "Nam", dob = "03/03/2016",
            parentPhone = "0339927913", parentName = "Nguyễn Thị Thúy An", notes = "",
            fatherName = "Nguyễn Chí Hiếu", fatherJob = "LR", motherName = "Nguyễn Thị Thúy An", motherJob = "LR",
            address = "Tân Bình", birthPlace = "Trung tâm y tế Xã Tân Thạnh, Tây Ninh", citizenId = "080216003466"
        ),
        Student(
            id = 9, stt = 9, fullName = "Bùi Hoàng Đăng Khôi", gender = "Nam", dob = "27/10/2016",
            parentPhone = "0987029043", parentName = "Nguyễn Thị Trúc Ly", notes = "",
            fatherName = "Bùi Quang Pha", fatherJob = "LR", motherName = "Nguyễn Thị Trúc Ly", motherJob = "LR",
            address = "Tân Bình", birthPlace = "Trung tâm y tế Xã Tân Thạnh, Tây Ninh", citizenId = "080216008815"
        ),
        Student(
            id = 10, stt = 10, fullName = "Hồ Đăng Khôi", gender = "Nam", dob = "25/05/2016",
            parentPhone = "0784768852", parentName = "Hồ Thị Thùy Trang", notes = "",
            fatherName = "", fatherJob = "", motherName = "Hồ Thị Thùy Trang", motherJob = "CN",
            address = "Hiệp Thành", birthPlace = "Trung tâm y tế Xã Tân Thạnh, Tây Ninh", citizenId = "080216012118"
        ),
        Student(
            id = 11, stt = 11, fullName = "Lê Trọng Duy Khôi", gender = "Nam", dob = "22/08/2016",
            parentPhone = "0364745975", parentName = "Lê Thị Bé Thi", notes = "",
            fatherName = "Lê Trọng Ân", fatherJob = "LR", motherName = "Lê Thị Bé Thi", motherJob = "LR",
            address = "Hiệp Thành", birthPlace = "BVĐK khu vực Cai Lậy, Đồng Tháp", citizenId = "080216005495"
        ),
        Student(
            id = 12, stt = 12, fullName = "Nguyễn Đăng Khôi", gender = "Nam", dob = "02/08/2016",
            parentPhone = "0392646494", parentName = "Phan T Kiều Diểm", notes = "",
            fatherName = "Nguyễn Hoàng Phong", fatherJob = "LR", motherName = "Phan T Kiều Diểm", motherJob = "LR",
            address = "Hiệp Thành", birthPlace = "Trung tâm y tế Xã Tân Thạnh, Tây Ninh", citizenId = "080216007874"
        ),
        Student(
            id = 13, stt = 13, fullName = "Phan Ngọc Thiên Kim", gender = "Nữ", dob = "16/06/2016",
            parentPhone = "0976677922", parentName = "Nguyễn Thị Cẩm Tú", notes = "",
            fatherName = "Phan Hoàng Nam", fatherJob = "CN", motherName = "Nguyễn Thị Cẩm Tú", motherJob = "CN",
            address = "Hiệp Thành", birthPlace = "BVĐK khu vực Cai Lậy, Đồng Tháp", citizenId = "080316010465"
        ),
        Student(
            id = 14, stt = 14, fullName = "Trịnh Hoàng Lâm", gender = "Nam", dob = "23/11/2016",
            parentPhone = "0946338570", parentName = "Võ Thị Thùy Diễm", notes = "",
            fatherName = "Trịnh Đình Phú", fatherJob = "Tài xế", motherName = "Võ Thị Thùy Diễm", motherJob = "Nội trợ",
            address = "Hiệp Thành", birthPlace = "Bệnh viện phụ sản Mỹ Tho Đồng Tháp", citizenId = "080216007520"
        ),
        Student(
            id = 15, stt = 15, fullName = "Lê Gia Long", gender = "Nam", dob = "03/11/2016",
            parentPhone = "0877353608", parentName = "Dương Thị Kha Ly", notes = "",
            fatherName = "Lê Vũ Phong", fatherJob = "LR", motherName = "Dương Thị Kha Ly", motherJob = "LR",
            address = "Hiệp Thành", birthPlace = "Trung tâm y tế Xã Tân Thạnh, Tây Ninh", citizenId = "080216004146"
        ),
        Student(
            id = 16, stt = 16, fullName = "Phùng Nguyễn Trọng Nghĩa", gender = "Nam", dob = "29/09/2016",
            parentPhone = "0365058603", parentName = "Nguyễn Lê Mỹ Hiền", notes = "",
            fatherName = "Phùng Văn Rạch", fatherJob = "LR", motherName = "Nguyễn Lê Mỹ Hiền", motherJob = "CN",
            address = "Tân Bình", birthPlace = "Bệnh viện Đa khoa Long An Tây Ninh", citizenId = "080216013203"
        ),
        Student(
            id = 17, stt = 17, fullName = "Phạm Thị Quỳnh Như", gender = "Nữ", dob = "02/03/2016",
            parentPhone = "0387707530", parentName = "Bùi Thị Đẹp", notes = "",
            fatherName = "Phạm Văn Hiệp", fatherJob = "LR", motherName = "Bùi Thị Đẹp", motherJob = "LR",
            address = "Tân Bình", birthPlace = "BVĐK khu vực Cai Lậy, Đồng Tháp", citizenId = "080316007239"
        ),
        Student(
            id = 18, stt = 18, fullName = "Phan Phạm Như Quỳnh", gender = "Nữ", dob = "29/11/2016",
            parentPhone = "0919630402", parentName = "Phạm Thị Hon", notes = "",
            fatherName = "Phan Thạch Vũ", fatherJob = "Tài xế", motherName = "Phạm Thị Hon", motherJob = "Nội trợ",
            address = "Hiệp Thành", birthPlace = "BVĐK khu vực Cai Lậy, Đồng Tháp", citizenId = "080316008165"
        ),
        Student(
            id = 19, stt = 19, fullName = "Nguyễn Thành Sĩ", gender = "Nam", dob = "02/08/2016",
            parentPhone = "0973525276", parentName = "Nguyễn Thị Kim Pha", notes = "",
            fatherName = "Nguyễn Thành Thuật", fatherJob = "LR", motherName = "Nguyễn Thị Kim Pha", motherJob = "LR",
            address = "Hiệp Thành", birthPlace = "Trung tâm y tế Xã Tân Thạnh, Tây Ninh", citizenId = "080216010250"
        ),
        Student(
            id = 20, stt = 20, fullName = "Nguyễn Ngọc Đan Thanh", gender = "Nữ", dob = "14/10/2016",
            parentPhone = "0589149402", parentName = "Võ Thanh Thảo", notes = "",
            fatherName = "Nguyễn Tấn Đá Vàng", fatherJob = "CN", motherName = "Võ Thanh Thảo", motherJob = "CN",
            address = "Hiệp Thành", birthPlace = "PKKV Huỳnh Việt Thanh,Hậu Thạnh Tây Ninh", citizenId = "080316008166"
        ),
        Student(
            id = 21, stt = 21, fullName = "Lê Hoàng Bảo Thy", gender = "Nữ", dob = "14/05/2015",
            parentPhone = "0348566325", parentName = "Lê Thi Dậm", notes = "",
            fatherName = "Lê Hoàng Minh Hiếu", fatherJob = "CN", motherName = "Lê Thi Dậm", motherJob = "CN",
            address = "Tân Bình", birthPlace = "BV Hậu Nghĩa, Đức Hòa, Tây Ninh", citizenId = "080315008999"
        ),
        Student(
            id = 22, stt = 22, fullName = "Lê Văn Tình", gender = "Nam", dob = "11/05/2016",
            parentPhone = "0374509831", parentName = "Dương Thị Phương Duy", notes = "",
            fatherName = "Lê Văn Vẹn", fatherJob = "LR", motherName = "Dương Thị Phương Duy", motherJob = "LR",
            address = "Hiệp Thành", birthPlace = "Trung tâm y tế Xã Tân Thạnh, Tây Ninh", citizenId = "080216011455"
        ),
        Student(
            id = 23, stt = 23, fullName = "Nguyễn Minh Tiến", gender = "Nam", dob = "22/02/2015",
            parentPhone = "0917346637", parentName = "Nguyễn Tú Non", notes = "",
            fatherName = "Huỳnh Văn Áp", fatherJob = "CN", motherName = "Nguyễn Tú Non", motherJob = "CN",
            address = "Tân Thạnh", birthPlace = "Phú Tân Cà Mau", citizenId = "096215012238"
        ),
        Student(
            id = 24, stt = 24, fullName = "Huỳnh Thị Ngọc Trâm", gender = "Nữ", dob = "07/06/2016",
            parentPhone = "0925875541", parentName = "Phạm Thị Trúc Ly", notes = "",
            fatherName = "Huỳnh Công Bốn", fatherJob = "LR", motherName = "Phạm Thị Trúc Ly", motherJob = "CN",
            address = "Ấp Đông", birthPlace = "BVĐK khu vực Cai Lậy, Đồng Tháp", citizenId = "080316007831"
        ),
        Student(
            id = 25, stt = 25, fullName = "Nguyễn Thị Bảo Trân", gender = "Nữ", dob = "13/08/2016",
            parentPhone = "0978367127", parentName = "Võ Thị Mỹ Duyên", notes = "",
            fatherName = "Nguyễn Lê Vinh", fatherJob = "CN", motherName = "Võ Thị Mỹ Duyên", motherJob = "CN",
            address = "Hiệp Thành", birthPlace = "BVĐK khu vực Cai Lậy, Đồng Tháp", citizenId = "080316011387"
        ),
        Student(
            id = 26, stt = 26, fullName = "Lê Phạm Nhã Trúc", gender = "Nữ", dob = "27/09/2016",
            parentPhone = "0389906763", parentName = "Phạm Thị Hồng Hòa", notes = "",
            fatherName = "Lê Hoàng Vũ", fatherJob = "LR", motherName = "Phạm Thị Hồng Hòa", motherJob = "LR",
            address = "Hiệp Thành", birthPlace = "BVĐK khu vực Cai Lậy, Đồng Tháp", citizenId = "082316009589"
        ),
        Student(
            id = 27, stt = 27, fullName = "Nguyễn Huỳnh Thanh Trúc", gender = "Nữ", dob = "10/07/2016",
            parentPhone = "0382671291", parentName = "Huỳnh Thị Tuyết Mai", notes = "",
            fatherName = "Nguyễn Văn Đợi", fatherJob = "CN", motherName = "Huỳnh Thị Tuyết Mai", motherJob = "CN",
            address = "Hiệp Thành", birthPlace = "BVĐK khu vực Cai Lậy, Đồng Tháp", citizenId = "080316002805"
        ),
        Student(
            id = 28, stt = 28, fullName = "Trần Kim Trúc", gender = "Nữ", dob = "26/05/2016",
            parentPhone = "0948002519", parentName = "Trần Thị Kim Vân", notes = "",
            fatherName = "", fatherJob = "", motherName = "Trần Thị Kim Vân", motherJob = "CN",
            address = "Hiệp Thành", birthPlace = "BV ĐK Mỹ Phước Tây, Đồng Tháp", citizenId = "080316012048"
        ),
        Student(
            id = 29, stt = 29, fullName = "Nguyễn Thúy Vy", gender = "Nữ", dob = "04/03/2016",
            parentPhone = "0989606451", parentName = "Nguyễn Thị Út Em", notes = "",
            fatherName = "", fatherJob = "", motherName = "Nguyễn Thị Út Em", motherJob = "LR",
            address = "Hiệp Thành", birthPlace = "BV Hùng Vương, TP HCM", citizenId = "080316008465"
        ),
        Student(
            id = 30, stt = 30, fullName = "Phạm Thị Như Ý", gender = "Nữ", dob = "05/10/2016",
            parentPhone = "0366618077", parentName = "Lê Thị Thanh Diện", notes = "",
            fatherName = "Phạm Minh Tân", fatherJob = "LR", motherName = "Lê Thị Thanh Diện", motherJob = "LR",
            address = "Tân Bình", birthPlace = "Bệnh viện Đa khoa Long An Tây Ninh", citizenId = "080316010290"
        ),
        Student(
            id = 31, stt = 31, fullName = "Võ Ngọc Như Ý", gender = "Nữ", dob = "10/11/2016",
            parentPhone = "0924793706", parentName = "Võ Thị Minh Châu", notes = "",
            fatherName = "", fatherJob = "", motherName = "Võ Thị Minh Châu", motherJob = "CN",
            address = "Hiệp Thành", birthPlace = "Bệnh viện Đa khoa Long An Tây Ninh", citizenId = "080316005532"
        )
    )

    fun createInitialAttendance(date: String): List<AttendanceRecord> {
        val list = mutableListOf<AttendanceRecord>()
        
        // Populate full week 14/09/2026 -> 18/09/2026 matching sample document
        val weekDates = listOf("2026-09-14", "2026-09-15", "2026-09-16", "2026-09-17", "2026-09-18")
        
        // 2026-09-15: In sample, all 31 students are present (✓)
        students.forEach { s ->
            list.add(AttendanceRecord(studentId = s.id, date = "2026-09-15", status = AttendanceStatus.DI_HOC, note = ""))
        }
        
        // If current date is not 2026-09-15, populate current date as well
        if (date != "2026-09-15") {
            students.forEach { s ->
                list.add(AttendanceRecord(studentId = s.id, date = date, status = AttendanceStatus.DI_HOC, note = ""))
            }
        }
        
        return list
    }

    fun createInitialBehaviorLogs(date: String): List<BehaviorLog> {
        return listOf(
            // Week 14/09 - 18/09/2026 sample data (Phát biểu = 1 for students 5, 6, 16, 18, 28, 30, 31)
            BehaviorLog(studentId = 5, date = "2026-09-15", type = BehaviorType.PRAISE, category = "Phát biểu", detail = "Hăng hái phát biểu xây dựng bài"),
            BehaviorLog(studentId = 6, date = "2026-09-15", type = BehaviorType.PRAISE, category = "Phát biểu", detail = "Xung phong giải bài tập"),
            BehaviorLog(studentId = 16, date = "2026-09-15", type = BehaviorType.PRAISE, category = "Phát biểu", detail = "Tích cực phát biểu"),
            BehaviorLog(studentId = 18, date = "2026-09-15", type = BehaviorType.PRAISE, category = "Phát biểu", detail = "Phát biểu to rõ"),
            BehaviorLog(studentId = 28, date = "2026-09-15", type = BehaviorType.PRAISE, category = "Phát biểu", detail = "Hăng hái xây dựng bài"),
            BehaviorLog(studentId = 30, date = "2026-09-15", type = BehaviorType.PRAISE, category = "Phát biểu", detail = "Phát biểu chính xác"),
            BehaviorLog(studentId = 31, date = "2026-09-15", type = BehaviorType.PRAISE, category = "Phát biểu", detail = "Nhiệt tình phát biểu"),
            // General praise logs
            BehaviorLog(studentId = 1, date = date, type = BehaviorType.PRAISE, category = "Phát biểu", detail = "Tích cực xung phong lên bảng giải bài toán nâng cao."),
            BehaviorLog(studentId = 3, date = date, type = BehaviorType.PRAISE, category = "Làm bài tập tốt", detail = "Hoàn thành xuất sắc bài tập Tiếng Việt, chữ viết rất sạch đẹp."),
            BehaviorLog(studentId = 5, date = date, type = BehaviorType.PRAISE, category = "Giúp đỡ bạn bè", detail = "Nhiệt tình hướng dẫn bạn cùng bàn làm bài tập nhóm."),
            BehaviorLog(studentId = 24, date = date, type = BehaviorType.PRAISE, category = "Trực nhật tốt", detail = "Tự giác quét dọn lớp học, lau bảng sạch sẽ trước giờ vào lớp.")
        )
    }

    val initialSchoolRequests = listOf(
        SchoolRequest(
            studentId = 4,
            title = "Nộp bổ sung thẻ Bảo hiểm Y tế",
            content = "Kính đề nghị phụ huynh chụp gửi bản photo thẻ BHYT năm 2025 để nhà trường cập nhật hồ sơ y tế học sinh.",
            deadline = "Trước thứ Sáu tuần này",
            isSent = true,
            sentDate = "Hôm nay",
            status = "Đã gửi Zalo"
        ),
        SchoolRequest(
            studentId = 8,
            title = "Nhắc nhở chuẩn bị bài vở ở nhà",
            content = "Nhờ phụ huynh kiểm tra cặp và đôn đốc em hoàn thành đủ bài tập về nhà trước khi đến lớp.",
            deadline = "Hàng ngày",
            isSent = true,
            sentDate = "Hôm nay",
            status = "Đã gửi Zalo"
        ),
        SchoolRequest(
            studentId = 16,
            title = "Cam kết đội mũ bảo hiểm khi tham gia giao thông",
            content = "Nhà trường nhắc nhở phụ huynh trang bị và nhắc em luôn đội mũ bảo hiểm đạt chuẩn khi đến trường.",
            deadline = "Thực hiện ngay",
            isSent = false,
            sentDate = "",
            status = "Chờ gửi Zalo"
        ),
        SchoolRequest(
            studentId = 22,
            title = "Mời phụ huynh trao đổi nề nếp của em",
            content = "Kính mời phụ huynh sắp xếp đến gặp GVCN vào lúc 16h30 thứ Năm để trao đổi việc rèn luyện nề nếp của em.",
            deadline = "16h30 Thứ Năm",
            isSent = false,
            sentDate = "",
            status = "Chờ gửi Zalo"
        ),
        SchoolRequest(
            studentId = 1,
            title = "Đăng ký tham gia vòng thi IOE cấp Trường",
            content = "Em có năng khiếu Tiếng Anh tốt, GVCN đề xuất phụ huynh đăng ký cho em tham gia thi Olympic Tiếng Anh.",
            deadline = "Hạn chót 25/10",
            isSent = true,
            sentDate = "Hôm qua",
            status = "Đã phản hồi đồng ý"
        )
    )

    fun createInitialSemesterResults(): List<SemesterResult> {
        val list = mutableListOf<SemesterResult>()
        students.forEach { student ->
            // Base variance based on student id
            val base = when {
                student.id in listOf(1, 3, 5, 13, 20, 24, 27, 30) -> 9.0
                student.id in listOf(8, 15, 16, 22) -> 7.0
                else -> 8.0
            }
            val offset = (student.id % 3) * 0.5

            TermType.values().forEach { term ->
                val math = (base + offset).coerceIn(6.0, 10.0)
                val tv = (base + if (student.gender == "Nữ") 0.5 else 0.0).coerceIn(6.0, 10.0)
                val eng = (base + offset * 0.5).coerceIn(6.0, 10.0)
                val sci = (base + 0.5).coerceIn(6.0, 10.0)
                val his = (base).coerceIn(6.0, 10.0)
                val info = (base + 0.5).coerceIn(6.0, 10.0)

                val avg = (math + tv + eng + sci + his + info) / 6.0
                val title = when {
                    avg >= 9.0 -> "Học sinh Xuất sắc"
                    avg >= 8.0 -> "Học sinh Tiêu biểu"
                    else -> "Hoàn thành tốt"
                }
                val conduct = if (avg >= 8.0) "Tốt" else "Đạt"
                val teacherNote = when {
                    avg >= 9.0 -> "Tiếp thu bài nhanh, gương mẫu, hăng hái xây dựng bài."
                    avg >= 8.0 -> "Chăm ngoan, học lực khá tốt, chữ viết cẩn thận."
                    else -> "Cần rèn thêm kỹ năng tính toán và chú ý hoàn thành bài tập về nhà."
                }

                list.add(
                    SemesterResult(
                        studentId = student.id,
                        term = term,
                        mathScore = (math * 10).toInt() / 10.0,
                        vietnameseScore = (tv * 10).toInt() / 10.0,
                        englishScore = (eng * 10).toInt() / 10.0,
                        scienceScore = (sci * 10).toInt() / 10.0,
                        historyGeoScore = (his * 10).toInt() / 10.0,
                        informaticsScore = (info * 10).toInt() / 10.0,
                        conduct = conduct,
                        title = title,
                        teacherNote = teacherNote
                    )
                )
            }
        }
        return list
    }
}
