package com.example

import com.example.data.db.SeedData
import com.example.data.model.AttendanceRecord
import com.example.data.model.AttendanceStatus
import com.example.util.ExcelReportExporter
import com.example.util.ZaloShareHelper
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testOfficialStudentsCountIs31() {
        val students = SeedData.officialStudents
        assertEquals(31, students.size)
        // Verify all students have unique STT from 1 to 31
        val sttSet = students.map { it.stt }.toSet()
        assertEquals(31, sttSet.size)
        assertTrue(sttSet.contains(1))
        assertTrue(sttSet.contains(31))
    }

    @Test
    fun testZaloGroupUrl() {
        assertEquals("https://zalo.me/g/ehbagp244", ZaloShareHelper.CLASS_ZALO_GROUP_URL)
    }

    @Test
    fun testReportRowComputation() {
        val students = SeedData.officialStudents
        val dates = listOf("14-Sep", "15-Sep", "16-Sep", "17-Sep", "18-Sep")
        val sampleAttendance = listOf(
            AttendanceRecord(studentId = 1, date = "14-Sep", status = AttendanceStatus.DI_HOC),
            AttendanceRecord(studentId = 1, date = "15-Sep", status = AttendanceStatus.NGHI_CO_PHEP),
            AttendanceRecord(studentId = 1, date = "16-Sep", status = AttendanceStatus.DI_HOC),
            AttendanceRecord(studentId = 1, date = "17-Sep", status = AttendanceStatus.DI_HOC),
            AttendanceRecord(studentId = 1, date = "18-Sep", status = AttendanceStatus.DI_HOC)
        )
        val rows = ExcelReportExporter.computeReportRows(students, dates, sampleAttendance, emptyList())
        assertEquals(31, rows.size)

        val row1 = rows.first { it.student.id == 1 }
        assertEquals(4, row1.presentCount)
        assertEquals(1, row1.excusedCount)
        assertEquals(0, row1.unexcusedCount)
        assertEquals("Tốt", row1.conductRanking)
    }
}
