package com.jwsulzen.habitrpg.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.jwsulzen.habitrpg.data.model.Task
import com.jwsulzen.habitrpg.data.repository.GameRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DashboardViewModel(private val repository: GameRepository) : ViewModel() {

    //"collect" tasks from the repository
    //UI will "observe" this list
    val level = repository.globalLevel //Flow<Int>
    val totalXp = repository.totalXp // Flow<Int>


    val allTasks = repository.tasksCurrentList
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
    fun onTaskCompleted(task: Task) {
        viewModelScope.launch { //coroutine!
            repository.completeTask(task)
        }
    }
}