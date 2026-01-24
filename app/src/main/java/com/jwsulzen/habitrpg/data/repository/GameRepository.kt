package com.jwsulzen.habitrpg.data.repository

//TODO: split into TaskRepository, PlayerRepository, SkillRepository?

import com.jwsulzen.habitrpg.data.local.TaskDao
import com.jwsulzen.habitrpg.data.model.*
import com.jwsulzen.habitrpg.data.seed.DefaultSkills
import com.jwsulzen.habitrpg.data.seed.TaskTemplates
import com.jwsulzen.habitrpg.data.model.SystemMetadata
import com.jwsulzen.habitrpg.domain.RpgEngine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.util.UUID

class GameRepository(private val taskDao: TaskDao) {
    //------------------- DATA STREAMS -------------------
    //STATIC: Suggested blueprints
    private val _tasksSuggestedList = MutableStateFlow(TaskTemplates.templates)
    val tasksSuggestedList = _tasksSuggestedList.asStateFlow()
    //STATIC: Skill definitions
    private val _skills = MutableStateFlow(DefaultSkills.skills)
    val skills = _skills.asStateFlow()

    //DYNAMIC: Pull all tasks
    val tasksCurrentList: Flow<List<Task>> = taskDao.getAllTasks()
    //DYNAMIC: Player Progress mapped from DB
    val playerState: Flow<PlayerState> = taskDao.getSkillProgress().map { list ->
        //convert list from database into map
        val skillMap = list.associateBy { it.skillId }

        //If db is empty initialize from hardcoded defaults (0 xp, lvl 1)
        if (skillMap.isEmpty()) {
            PlayerState(skills = DefaultSkills.skills.associate { it.id to SkillProgress(it.id, 0, 1) })
        } else {
            PlayerState(skills = skillMap)
        }
    }

    //Level State
    val totalXp: Flow<Int> = playerState.map { state ->
        state.skills.values.sumOf { it.xp }
    }

    val globalLevel: Flow<Int> = totalXp.map { xp ->
        RpgEngine.getLevelFromTotalXp(xp)
    }

    //History (List of completed task Ids and timestamps)
    private val _completionHistory = MutableStateFlow<List<CompletionRecord>>(emptyList())
    val completionHistory = _completionHistory.asStateFlow()

    // ---------------------- ACTIONS ----------------------

    suspend fun completeTask(task: Task) {
        //1. Add to history
        val record = CompletionRecord(
            taskId = task.id,
            date = LocalDate.now(),
            xpGained = task.difficulty.baseXp
        )
        taskDao.insertCompletionRecord(record)

        //2. Increment progress and update goal status
        val newProgress = task.currentProgress + 1
        val reachedGoal = newProgress >= task.goal

        val updatedTask = task.copy(
            currentProgress = newProgress,
            isGoalReached = reachedGoal
        )
        taskDao.insertTask(updatedTask)

        //3. Update player stats (XP/Level)
        val currentSkillProgress = taskDao.getSkillProgressAsList()
            .find { it.skillId == task.skillId }
            ?: SkillProgress(task.skillId, 0, 1) //failsafe: if DNE, create new skill

        val newXp = currentSkillProgress.xp + task.difficulty.baseXp
        val newLevel = RpgEngine.getLevelFromTotalXp(newXp)

        val updatedSkill = currentSkillProgress.copy(xp = newXp, level = newLevel)
        //Save to DB and update state
        taskDao.updateSkillProgress(updatedSkill)
    }

    suspend fun createTask(title: String, skillId: String, difficulty: Difficulty,schedule: Schedule, goal: Int, unit: String, isMeasurable: Boolean) {
        val newTask = Task(
            id = UUID.randomUUID().toString(),
            title = title,
            skillId = skillId,
            difficulty = difficulty,
            schedule = schedule,
            goal = goal,
            currentProgress = 0,
            isGoalReached = false,
            unit = unit,
            isMeasurable = isMeasurable
        )
        taskDao.insertTask(newTask)
    }

    suspend fun resetGameData() {
        taskDao.clearAllTasks()
        taskDao.clearAllProgress()
        _completionHistory.value = emptyList()
    }

    suspend fun refreshDailyTasks() {
        val today = LocalDate.now()
        val metadata = taskDao.getMetadata()
        val lastRefreshDate = metadata?.lastRefreshDate ?: LocalDate.MIN

        //If already refreshed today, stop
        if (lastRefreshDate == today) return

        //Get all tasks to check their individual schedules
        val allTasks = taskDao.getAllTasksAsList()

        allTasks.forEach { task ->
            val shouldReset = when (val x = task.schedule) {
                is Schedule.Daily -> true //Always reset daily tasks
                is Schedule.Weekly -> {
                    //Reset if today is a new week
                    //TODO allow user to set start of week to Sunday or Monday
                    val lastWeek = lastRefreshDate.with(java.time.DayOfWeek.MONDAY)
                    val currentWeek = today.with(java.time.DayOfWeek.MONDAY)
                    currentWeek.isAfter(lastWeek)
                }
                is Schedule.Monthly -> {
                    //Reset if today is a new month
                    today.monthValue != lastRefreshDate.monthValue || today.year != lastRefreshDate.year
                }
                is Schedule.Interval -> {
                    //Currently handling like "session" reset TODO Make robust interval logic or scrap it
                    task.isGoalReached
                }
            }

            if (shouldReset) {
                taskDao.insertTask(task.copy(currentProgress = 0, isGoalReached = false))
            }
        }

        //4. Update DB with today's date
        taskDao.updateMetadata(SystemMetadata(lastRefreshDate = today))
    }
}