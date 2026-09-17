package com.example.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Class
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.NotificationImportant
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.NotificationImportant
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.MessagePreviewDialog
import com.example.ui.screens.AttendanceScreen
import com.example.ui.screens.BehaviorScreen
import com.example.ui.screens.GradesScreen
import com.example.ui.screens.ReportsScreen
import com.example.ui.screens.SchoolRequestsScreen
import com.example.ui.screens.StudentsScreen
import com.example.ui.theme.SchoolPrimary
import com.example.ui.viewmodel.ClassViewModel
import com.example.util.ZaloShareHelper
import kotlinx.coroutines.launch

data class PendingMessage(
    val recipientName: String,
    val phone: String,
    val messageText: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: ClassViewModel = viewModel()) {
    val context = LocalContext.current
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var subTabSchoolIndex by remember { mutableIntStateOf(0) } // For Tab 4: 0 = Grades, 1 = Requests
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Observe state
    val students by viewModel.students.collectAsStateWithLifecycle()
    val filteredStudents by viewModel.filteredStudents.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val genderFilter by viewModel.genderFilter.collectAsStateWithLifecycle()

    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val attendanceMap by viewModel.attendanceMap.collectAsStateWithLifecycle()
    val allAttendanceRecords by viewModel.allAttendanceRecords.collectAsStateWithLifecycle()

    val behaviorLogs by viewModel.behaviorLogs.collectAsStateWithLifecycle()
    val schoolRequests by viewModel.schoolRequests.collectAsStateWithLifecycle()

    val selectedTerm by viewModel.selectedTerm.collectAsStateWithLifecycle()
    val semesterResults by viewModel.semesterResults.collectAsStateWithLifecycle()
    val semesterResultsMap by viewModel.semesterResultsMap.collectAsStateWithLifecycle()

    // Message dialog state
    var pendingMessage by remember { mutableStateOf<PendingMessage?>(null) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Sổ Liên Lạc 5A",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                        Surface(
                            color = Color.White.copy(alpha = 0.2f),
                            shape = MaterialTheme.shapes.extraSmall
                        ) {
                            Text(
                                text = "Lớp 5A • ${students.size} học sinh • GVCN",
                                fontSize = 11.sp,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = { ZaloShareHelper.openClassZaloGroup(context) },
                        modifier = Modifier.testTag("open_zalo_group_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Groups,
                            contentDescription = "Nhóm Zalo 5A",
                            tint = Color.White
                        )
                    }
                    IconButton(
                        onClick = {
                            viewModel.syncOfficialStudents()
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Đã đồng bộ đầy đủ danh sách 31 học sinh và số điện thoại phụ huynh!")
                            }
                        },
                        modifier = Modifier.testTag("sync_students_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sync,
                            contentDescription = "Đồng bộ",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = SchoolPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .navigationBarsPadding()
                    .testTag("bottom_nav_bar")
            ) {
                // Tab 0: Students
                NavigationBarItem(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    icon = {
                        Icon(
                            if (selectedTabIndex == 0) Icons.Default.People else Icons.Outlined.People,
                            contentDescription = "Học sinh"
                        )
                    },
                    label = { Text("Lớp 5A", fontSize = 11.sp) },
                    modifier = Modifier.testTag("tab_students")
                )

                // Tab 1: Attendance
                NavigationBarItem(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    icon = {
                        Icon(
                            if (selectedTabIndex == 1) Icons.Default.CheckCircle else Icons.Outlined.CheckCircle,
                            contentDescription = "Chuyên cần"
                        )
                    },
                    label = { Text("Chuyên cần", fontSize = 11.sp) },
                    modifier = Modifier.testTag("tab_attendance")
                )

                // Tab 2: Behavior
                NavigationBarItem(
                    selected = selectedTabIndex == 2,
                    onClick = { selectedTabIndex = 2 },
                    icon = {
                        Icon(
                            if (selectedTabIndex == 2) Icons.Default.Star else Icons.Outlined.Star,
                            contentDescription = "Nề nếp"
                        )
                    },
                    label = { Text("Nề nếp", fontSize = 11.sp) },
                    modifier = Modifier.testTag("tab_behavior")
                )

                // Tab 3: Reports (Weekly & Monthly Summary + Excel Export)
                NavigationBarItem(
                    selected = selectedTabIndex == 3,
                    onClick = { selectedTabIndex = 3 },
                    icon = {
                        Icon(
                            if (selectedTabIndex == 3) Icons.Default.Description else Icons.Outlined.Description,
                            contentDescription = "Báo cáo"
                        )
                    },
                    label = { Text("Báo cáo", fontSize = 11.sp) },
                    modifier = Modifier.testTag("tab_reports")
                )

                // Tab 4: Grades & Requests
                NavigationBarItem(
                    selected = selectedTabIndex == 4,
                    onClick = { selectedTabIndex = 4 },
                    icon = {
                        Icon(
                            if (selectedTabIndex == 4) Icons.Default.School else Icons.Outlined.School,
                            contentDescription = "Học tập"
                        )
                    },
                    label = { Text("Học tập", fontSize = 11.sp) },
                    modifier = Modifier.testTag("tab_grades")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTabIndex) {
                0 -> {
                    StudentsScreen(
                        students = students,
                        filteredStudents = filteredStudents,
                        searchQuery = searchQuery,
                        onSearchQueryChange = viewModel::setSearchQuery,
                        genderFilter = genderFilter,
                        onGenderFilterChange = viewModel::setGenderFilter,
                        attendanceMap = attendanceMap,
                        behaviorLogs = behaviorLogs,
                        schoolRequests = schoolRequests,
                        semesterResults = semesterResults,
                        onUpdateStudent = viewModel::updateStudentInfo,
                        onSendMessage = { name, phone, message ->
                            pendingMessage = PendingMessage(name, phone, message)
                        }
                    )
                }
                1 -> {
                    AttendanceScreen(
                        students = students,
                        selectedDate = selectedDate,
                        onDateChange = viewModel::setSelectedDate,
                        attendanceMap = attendanceMap,
                        onMarkAttendance = viewModel::markAttendance,
                        onMarkAllPresent = viewModel::markAllPresent,
                        onSendMessage = { name, phone, message ->
                            pendingMessage = PendingMessage(name, phone, message)
                        }
                    )
                }
                2 -> {
                    BehaviorScreen(
                        students = students,
                        behaviorLogs = behaviorLogs,
                        onAddLog = viewModel::addBehaviorLog,
                        onDeleteLog = viewModel::deleteBehaviorLog,
                        onSendMessage = { name, phone, message ->
                            pendingMessage = PendingMessage(name, phone, message)
                        }
                    )
                }
                3 -> {
                    ReportsScreen(
                        students = students,
                        allAttendanceRecords = allAttendanceRecords,
                        behaviorLogs = behaviorLogs,
                        onSendMessage = { name, phone, message ->
                            pendingMessage = PendingMessage(name, phone, message)
                        }
                    )
                }
                4 -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        TabRow(
                            selectedTabIndex = subTabSchoolIndex,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Tab(
                                selected = subTabSchoolIndex == 0,
                                onClick = { subTabSchoolIndex = 0 },
                                text = { Text("Bảng Điểm Học Kỳ", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                            )
                            Tab(
                                selected = subTabSchoolIndex == 1,
                                onClick = { subTabSchoolIndex = 1 },
                                text = { Text("Yêu Cầu Nhà Trường", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                            )
                        }

                        if (subTabSchoolIndex == 0) {
                            GradesScreen(
                                students = students,
                                selectedTerm = selectedTerm,
                                onSelectTerm = viewModel::setSelectedTerm,
                                semesterResultsMap = semesterResultsMap,
                                onUpdateResult = viewModel::updateSemesterResult,
                                onSendMessage = { name, phone, message ->
                                    pendingMessage = PendingMessage(name, phone, message)
                                }
                            )
                        } else {
                            SchoolRequestsScreen(
                                students = students,
                                schoolRequests = schoolRequests,
                                onAddRequest = viewModel::addSchoolRequest,
                                onUpdateRequest = viewModel::updateSchoolRequest,
                                onDeleteRequest = viewModel::deleteSchoolRequest,
                                onSendMessage = { name, phone, message ->
                                    pendingMessage = PendingMessage(name, phone, message)
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Message Preview and Zalo Dispatch Dialog
    pendingMessage?.let { pending ->
        MessagePreviewDialog(
            recipientName = pending.recipientName,
            recipientPhone = pending.phone,
            message = pending.messageText,
            onDismiss = { pendingMessage = null }
        )
    }
}
