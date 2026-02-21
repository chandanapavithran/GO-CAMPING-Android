package com.gocamping.data

import androidx.room.*

@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: String,
    val name: String,
    val role: String,
    val contactNo: String,
    val password: String,
    val extraInfo: String? = null // e.g., "Class" for students
)

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM users WHERE id = :id AND password = :password")
    suspend fun login(id: String, password: String): User?
}

@Database(entities = [User::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}
