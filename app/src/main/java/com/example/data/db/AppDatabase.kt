package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.AttendanceRecord
import com.example.data.model.AttendanceStatus
import com.example.data.model.BehaviorLog
import com.example.data.model.BehaviorType
import com.example.data.model.SchoolRequest
import com.example.data.model.SemesterResult
import com.example.data.model.Student
import com.example.data.model.TermType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Converters {
    @TypeConverter
    fun fromAttendanceStatus(status: AttendanceStatus?): String? = status?.name

    @TypeConverter
    fun toAttendanceStatus(value: String?): AttendanceStatus? =
        value?.let { runCatching { AttendanceStatus.valueOf(it) }.getOrDefault(AttendanceStatus.DI_HOC) }

    @TypeConverter
    fun fromBehaviorType(type: BehaviorType?): String? = type?.name

    @TypeConverter
    fun toBehaviorType(value: String?): BehaviorType? =
        value?.let { runCatching { BehaviorType.valueOf(it) }.getOrDefault(BehaviorType.PRAISE) }

    @TypeConverter
    fun fromTermType(term: TermType?): String? = term?.name

    @TypeConverter
    fun toTermType(value: String?): TermType? =
        value?.let { runCatching { TermType.valueOf(it) }.getOrDefault(TermType.GHKI) }
}

@Database(
    entities = [
        Student::class,
        AttendanceRecord::class,
        BehaviorLog::class,
        SchoolRequest::class,
        SemesterResult::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "so_lien_lac_5a_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database.appDao())
                    }
                }
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        if (database.appDao().getStudentCount() == 0) {
                            populateDatabase(database.appDao())
                        }
                    }
                }
            }

            suspend fun populateDatabase(dao: AppDao) {
                dao.insertStudents(SeedData.students)
                val today = SeedData.getTodayString()
                dao.insertAttendances(SeedData.createInitialAttendance(today))
                SeedData.createInitialBehaviorLogs(today).forEach { dao.insertBehaviorLog(it) }
                SeedData.initialSchoolRequests.forEach { dao.insertSchoolRequest(it) }
                dao.insertSemesterResults(SeedData.createInitialSemesterResults())
            }
        }
    }
}
