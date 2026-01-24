package com.jwsulzen.habitrpg.data.local

import androidx.room.*
import com.jwsulzen.habitrpg.data.model.CompletionRecord
import com.jwsulzen.habitrpg.data.model.SkillProgress
import com.jwsulzen.habitrpg.data.model.Task
import com.jwsulzen.habitrpg.data.model.SystemMetadata
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    //region HISTORY (CompletionRecord)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletionRecord(record: CompletionRecord)

    @Query("SELECT * FROM completion_history WHERE taskId = :taskId")
    fun getHistoryForTask(taskId: String): Flow<List<CompletionRecord>>
    //endregion

    //region METADATA (lastRefreshDate)
    @Query("SELECT * FROM system_metadata WHERE id = 0")
    suspend fun getMetadata(): SystemMetadata?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateMetadata(metadata: SystemMetadata)
    //endregion

    //region TASKS
    //Get FLOW of tasks for UI
    @Query("SELECT * FROM tasks")
    fun getAllTasks(): Flow<List<Task>>

    //Get LIST of tasks for BACKEND (Daily Refresh logic)
    @Query("SELECT * FROM tasks")
    suspend fun getAllTasksAsList(): List<Task>

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