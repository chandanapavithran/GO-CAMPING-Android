package com.gocamping.data

import androidx.room.*

@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: String, // Std_id, Staff_id, or P_id
    val name: String,
    val role: String, // "Student", "Staff", "Parent"
    val contactNo: String,
    val password: String,
    val roleSpecific1: String? = null, // Class (Student) or Department (Staff) or Student_id (Parent)
    val roleSpecific2: String? = null, // Address (Student)
    val isArchived: Boolean = false
)

@Entity(tableName = "attendance")
data class Attendance(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String,
    val studentId: String,
    val status: String,
    val isArchived: Boolean = false
)

@Entity(tableName = "payments")
data class Payment(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String,
    val studentId: String,
    val parentId: String,
    val amount: Double,
    val status: String,
    val isArchived: Boolean = false
)

@Entity(tableName = "alerts")
data class Alert(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String,
    val type: String,
    val content: String,
    val targetGroup: String, // "All", "Student", "Parent"
    val isArchived: Boolean = false
)

@Entity(tableName = "feedback")
data class Feedback(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val studentId: String,
    val content: String,
    val date: String,
    val isArchived: Boolean = false
)

@Dao
interface AppDao {
    // Auth
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM users WHERE id = :id AND password = :password AND isArchived = 0")
    suspend fun login(id: String, password: String): User?

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserById(id: String): User?

    @Query("SELECT * FROM users WHERE id = :id AND role = 'Student'")
    suspend fun getStudentById(id: String): User?

    // Attendance
    @Insert
    suspend fun insertAttendance(attendance: Attendance)

    @Query("SELECT * FROM attendance WHERE studentId = :studentId AND isArchived = 0")
    suspend fun getAttendanceForStudent(studentId: String): List<Attendance>

    @Query("SELECT * FROM attendance WHERE isArchived = 0")
    suspend fun getAllActiveAttendance(): List<Attendance>

    // Payments
    @Insert
    suspend fun insertPayment(payment: Payment)

    @Query("SELECT * FROM payments WHERE parentId = :parentId AND isArchived = 0")
    suspend fun getPaymentsForParent(parentId: String): List<Payment>

    @Query("SELECT * FROM payments WHERE isArchived = 0")
    suspend fun getAllActivePayments(): List<Payment>

    // Alerts
    @Insert
    suspend fun insertAlert(alert: Alert)

    @Query("SELECT * FROM alerts WHERE (targetGroup = :role OR targetGroup = 'All') AND isArchived = 0")
    suspend fun getAlertsForRole(role: String): List<Alert>

    @Query("SELECT * FROM alerts WHERE isArchived = 0")
    suspend fun getAllActiveAlerts(): List<Alert>

    // Feedback
    @Insert
    suspend fun insertFeedback(feedback: Feedback)

    @Query("SELECT * FROM feedback WHERE isArchived = 0")
    suspend fun getAllActiveFeedback(): List<Feedback>

    @Query("SELECT * FROM users WHERE role = 'Student' AND isArchived = 0")
    suspend fun getAllStudents(): List<User>

    // Archiving
    @Query("UPDATE users SET isArchived = 1 WHERE role = :role")
    suspend fun archiveUsersByRole(role: String)

    @Query("UPDATE attendance SET isArchived = 1")
    suspend fun archiveAllAttendance()

    @Query("UPDATE payments SET isArchived = 1")
    suspend fun archiveAllPayments()
}

@Database(entities = [User::class, Attendance::class, Payment::class, Alert::class, Feedback::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: android.content.Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "camping_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
