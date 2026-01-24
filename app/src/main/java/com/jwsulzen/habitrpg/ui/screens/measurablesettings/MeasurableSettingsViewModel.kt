package com.jwsulzen.habitrpg.ui.screens.measurablesettings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.jwsulzen.habitrpg.data.model.Difficulty
import com.jwsulzen.habitrpg.data.model.Schedule
import com.jwsulzen.habitrpg.data.repository.GameRepository
import kotlinx.coroutines.launch

class MeasurableSettingsViewModel(private val repository: GameRepository) : ViewModel() {

    fun onAddTask(
        title : String,
        skillId : String,
        difficulty : Difficulty,
        schedule: Schedule,
        goal: Int,
        unit: String,
        isMeasurable: Boolean
    ) {
        viewModelScope.launch { //coroutine
            repository.createTask(
                title,
                skillId,
                difficulty,
                schedule,
                goal,
                unit,
                isMeasurable
            )
        }
    }

    companion object {
        fun provideFactory(repository: GameRepository): ViewModelProvider.Factory = object :
            ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MeasurableSettingsViewModel(repository) as T
            }
        }
    }
}