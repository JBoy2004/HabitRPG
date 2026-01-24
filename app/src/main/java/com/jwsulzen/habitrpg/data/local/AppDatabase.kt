package com.jwsulzen.habitrpg.data.local

import android.content.Context
import androidx.room.*
import com.google.gson.Gson
import com.jwsulzen.habitrpg.data.model.CompletionRecord
import com.jwsulzen.habitrpg.data.model.Difficulty
import com.jwsulzen.habitrpg.data.model.SkillProgress
import com.jwsulzen.habitrpg.data.model.SystemMetadata
import com.jwsulzen.habitrpg.data.model.Task
import java.time.LocalDate

@Database(entities = [Task::class, SkillProgress::class, SystemMetadata::class, CompletionRecord::class],
    version = 7, //Increment when adding/modifying classes in DB
    exportSchema = false)
@TypeConverters(AppConverters::class, ScheduleConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "habit_rpg_database"
                ).fallbackToDestructiveMigration(dropAllTables = true) //TODO IMPORTANT for full release must change this! Otherwise updates will delete all user data!!!
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

//Helper to store DIFFICULTY enum as string in database
class AppConverters {
    @TypeConverter
    fun fromDifficulty(value: Difficulty) = value.name
    @TypeConverter
    fun toDifficulty(value: String) = Difficulty.valueOf(value)

    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? = date?.toString()
    @TypeConverter
    fun toLocalDate(dateString: String?): LocalDate? = dateString?.let { LocalDate.parse(it) }

}