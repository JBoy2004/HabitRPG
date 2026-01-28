package com.jwsulzen.habitrpg.ui.screens.tasksettings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.jwsulzen.habitrpg.data.model.Difficulty
import com.jwsulzen.habitrpg.data.model.Schedule
import com.jwsulzen.habitrpg.data.repository.GameRepository
import kotlinx.coroutines.launch
import java.time.LocalDate

class TaskSettingsViewModel(
    private val repository: GameRepository,
    private val taskId: String?
) : ViewModel() {

    var title by mutableStateOf("")
    var goal by mutableStateOf("")
    var unit by mutableStateOf("")
    var selectedDifficulty by mutableStateOf(Difficulty.MEDIUM)
    var repeatType by mutableStateOf("Does not repeat")
    var intervalValue by mutableStateOf("1")
    var selectedDate by mutableStateOf(LocalDate.now())

    init {
        //If taskId is provided, fetch and autofill
        if (!taskId.isNullOrBlank()) {
            viewModelScope.launch {
                repository.getTaskById(taskId)?.let { task ->
                    title = task.title
                    goal = task.goal.toString()
                    unit = task.unit ?: ""
                    selectedDifficulty = task.difficulty
                    selectedDate = LocalDate.now() //TODO or stored date? REMOVE THIS??
                    repeatType = when(task.schedule) {
                        is Schedule.Daily -> "Every day"
                        is Schedule.Weekly -> "Every week"
                        is Schedule.Monthly -> "Every month"
                        is Schedule.Interval -> "Custom"
                    }
                }
            }
        }
    }

    fun onSaveTask(
        skillId: String,
        schedule: Schedule,
        isMeasurable: Boolean
    ) {
        viewModelScope.launch {
            if (taskId.isNullOrBlank()) {
                repository.createTask(
                    title,
                    skillId,
                    selectedDifficulty,
                    schedule,
                    goal.toIntOrNull() ?: 1,
                    unit, isMeasurable
                )
            } else {
                //Fetch current task to preserve ID and other fields, then update
                repository.getTaskById(taskId)?.let { existingTask ->
                    val updatedTask = existingTask.copy(
                        title = title,
                        difficulty = selectedDifficulty,
                        schedule = schedule,
                        goal = goal.toIntOrNull() ?: 1,
                        unit = unit,
                        isMeasurable = isMeasurable
                    )
                    repository.updateTask(updatedTask)
                }
            }
        }
    }

    companion object {
        fun provideFactory(repository: GameRepository, taskId: String?): ViewModelProvider.Factory = object :
            ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return TaskSettingsViewModel(repository, taskId) as T
            }
        }
    }
}