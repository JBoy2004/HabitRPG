package com.jwsulzen.habitrpg.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.jwsulzen.habitrpg.data.model.Task
import com.jwsulzen.habitrpg.data.repository.GameRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class DashboardViewModel(private val repository: GameRepository) : ViewModel() {

    //"collect" tasks from the repository
    //UI will "observe" this list
    val level = repository.globalLevel //Flow<Int>
    val totalXp = repository.totalXp // Flow<Int>


    val tasksDaily = repository.tasksDaily
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    val tasksWeekly = repository.tasksWeekly
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    val tasksMonthly = repository.tasksMonthly
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    companion object {
        fun provideFactory(repository: GameRepository): ViewModelProvider.Factory = object :
            ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return DashboardViewModel(repository) as T
            }
        }
    }

    //Called when user clicks checkbox
    fun onQuickLog(task: Task, amount: Int) {
        viewModelScope.launch { //coroutine!
            repository.completeTask(task, amount)
        }
    }

    fun onLogProgress(task: Task, amount: Int, date: LocalDate, newGoal: Int) {
        viewModelScope.launch {
            repository.logTaskProgress(task, amount, date, newGoal)
        }
    }
}