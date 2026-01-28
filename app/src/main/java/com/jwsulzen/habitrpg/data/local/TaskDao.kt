package com.jwsulzen.habitrpg.data.local

import androidx.room.*
import com.jwsulzen.habitrpg.data.model.CompletionRecord
import com.jwsulzen.habitrpg.data.model.SkillProgress
import com.jwsulzen.habitrpg.data.model.Task
import com.jwsulzen.habitrpg.data.model.SystemMetadata
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface TaskDao {
    //region HISTORY (CompletionRecord)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletionRecord(record: CompletionRecord)

    @Query("SELECT * FROM completion_history WHERE taskId = :taskId")
    fun getHistoryForTask(taskId: String): Flow<List<CompletionRecord>>

    @Query("SELECT DISTINCT date FROM completion_history WHERE taskId = :taskId AND progressAmount > 0")
    suspend fun getDatesWithProgress(taskId: String): List<LocalDate>
    //endregion

    //region METADATA (lastRefreshDate)
    @Query("SELECT * FROM system_metadata WHERE id = 0")
    suspend fun getMetadata(): SystemMetadata?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateMetadata(metadata: SystemMetadata)
    //endregion

    //region TASKS
    //Get FLOW of tasks for UI (sorted by createdAt)
    @Query("SELECT * FROM tasks ORDER BY createdAt ASC")
    fun getAllTasks(): Flow<List<Task>>

    //Get FLOW of Daily tasks
    @Query("SELECT * FROM tasks WHERE schedule LIKE '%\"type\":\"DAILY\"%' ORDER BY createdAt ASC")
    fun getDailyTasks(): Flow<List<Task>>

    //Get FLOW of Weekly tasks
    @Query("SELECT * FROM tasks WHERE schedule LIKE '%\"type\":\"WEEKLY\"%' ORDER BY createdAt ASC")
    fun getWeeklyTasks(): Flow<List<Task>>

    //Get FLOW of Monthly tasks
    @Query("SELECT * FROM tasks WHERE schedule LIKE '%\"type\":\"MONTHLY\"%' ORDER BY createdAt ASC")
    fun getMonthlyTasks(): Flow<List<Task>>

    //Get LIST of tasks for BACKEND (Daily Refresh logic)
    @Query("SELECT * FROM tasks")
    suspend fun getAllTasksAsList(): List<Task>

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: String): Task

    //Reset logic for a new day/period
    @Query("UPDATE tasks SET currentProgress = 0, isGoalReached = 0")
    suspend fun resetAllTaskProgress()

    //Updating tasks
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)

    //Deleting tasks TODO: replace with task archive functionality!
    @Delete
    suspend fun deleteTask(task: Task)

    //Delete all tasks (data clearing)
    @Query("DELETE FROM tasks")
    suspend fun clearAllTasks()

    //Get the sum of progress for a specific task on a specific date
    @Query("SELECT SUM(progressAmount) FROM completion_history WHERE taskId = :taskId AND date = :date")
    suspend fun getProgressForTaskOnDate(taskId: String, date: LocalDate): Int?

    @Query("UPDATE tasks SET goal = :newGoal WHERE id = :taskId")
    suspend fun updateTaskGoal(taskId: String, newGoal: Int)

    //Sum progress for a specific task within a specific date range
    @Query("SELECT SUM(progressAmount) FROM completion_history WHERE taskId = :taskId AND date BETWEEN :startDate AND :endDate")
    suspend fun getProgressSumForRange(taskId: String, startDate: LocalDate, endDate: LocalDate): Int?
    //endregion

    //region SKILLS
    @Query("SELECT * FROM skill_progress")
    fun getSkillProgress(): Flow<List<SkillProgress>>

    @Query("SELECT * FROM skill_progress")
    suspend fun getSkillProgressAsList(): List<SkillProgress>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateSkillProgress(progress: SkillProgress)

    //Delete all skill progress (skill clearing
    @Query("DELETE FROM skill_progress")
    suspend fun clearAllProgress()
    //endregion
}