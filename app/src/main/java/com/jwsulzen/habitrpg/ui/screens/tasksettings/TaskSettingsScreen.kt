package com.jwsulzen.habitrpg.ui.screens.tasksettings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.jwsulzen.habitrpg.data.model.Difficulty
import com.jwsulzen.habitrpg.data.repository.GameRepository
import com.jwsulzen.habitrpg.data.seed.DefaultSkills
import com.jwsulzen.habitrpg.data.seed.TaskTemplates
import com.jwsulzen.habitrpg.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskSettingsScreen(
    navController: NavController,
    repository: GameRepository,
    skillId: String,
    isMeasurable: Boolean,
    taskId: String?
) {
    val viewModel: TaskSettingsViewModel = viewModel(
        factory = TaskSettingsViewModel.provideFactory(repository, taskId)
    )
    var showRepeatMenu by remember { mutableStateOf(false) }
    val repeatOptions = listOf("Does not repeat", "Every day", "Every week", "Every month", "Custom")
    val hintTask = TaskTemplates.getTemplateForSkill(skillId, isMeasurable = true)
    val isEditMode = !taskId.isNullOrBlank()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = if (isEditMode) "Edit Quest" else "Create ${DefaultSkills.skills.find { it.id == skillId }?.name} Quest",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        //Contain the information for current customized task
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = CutCornerShape(8.dp)
        ) {
            Column (
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                //region TITLE
                OutlinedTextField(
                    value = viewModel.title,
                    onValueChange = { viewModel.title = it },
                    label = { Text("Task Title") },
                    placeholder = { Text("e.g. ${hintTask?.title ?: "e.g. Read for 20 mins"}") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                //endregion

                //region GOAL
                OutlinedTextField(
                    value = viewModel.goal,
                    onValueChange = { if (it.all { char -> char.isDigit() }) viewModel.goal = it },
                    label = { Text("Goal") },
                    placeholder = { Text("e.g. ${hintTask?.goal?.toString() ?: "1"}") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                //endregion

                //region UNIT
                if (isMeasurable) {
                    OutlinedTextField(
                        value = viewModel.unit,
                        onValueChange = { viewModel.unit = it },
                        label = { Text("Unit") },
                        placeholder = { Text("e.g. ${hintTask?.unit ?: "e.g. minutes"}") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
                //endregion

                //region FREQUENCY
                Text("Frequency", style = MaterialTheme.typography.labelLarge)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    //Date Button
                    OutlinedButton(
                        onClick = { /* TODO Show DatePickerDialog */},
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(painterResource(id = android.R.drawable.ic_menu_my_calendar), contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text(viewModel.selectedDate.toString())
                    }

                    //Repeat Button
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedButton(
                            onClick = { showRepeatMenu = true },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(viewModel.repeatType, maxLines = 1, fontSize = 12.sp)
                        }

                        //Dropdown Menu for Repeat options
                        DropdownMenu(
                            expanded = showRepeatMenu,
                            onDismissRequest = { showRepeatMenu = false }
                        ) {
                            repeatOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        viewModel.repeatType = option
                                        showRepeatMenu = false
                                    }
                                )
                            }
                        }
                    }
                }

                //region CUSTOM INTERVAL SUB-MENU
                if (viewModel.repeatType == "Custom") {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Every ", style = MaterialTheme.typography.bodyMedium)
                        OutlinedTextField(
                            value = viewModel.intervalValue,
                            onValueChange = { if (it.all { char -> char.isDigit() }) viewModel.intervalValue = it },
                            modifier = Modifier.width(80.dp),
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodySmall
                        )
                        Text(" days", style = MaterialTheme.typography.bodyMedium)
                    }
                }
                //endregion

                //region DIFFICULTY SELECTION
                Text("Difficulty", style = MaterialTheme.typography.labelLarge)
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    Difficulty.entries.forEachIndexed { index, difficulty ->
                        SegmentedButton(
                            shape = SegmentedButtonDefaults.itemShape(index = index, count = Difficulty.entries.size),
                            onClick = { viewModel.selectedDifficulty = difficulty },
                            selected = difficulty == viewModel.selectedDifficulty,
                            label = { Text(difficulty.name.take(3), fontSize = 10.sp) }
                        )
                    }
                }
                //endregion

                //region CREATE TASK BUTTON
                Button(
                    onClick = {
                        //VALIDATION LOGIC
                        val finalTitle = viewModel.title.ifBlank { hintTask?.title } ?: ""
                        val finalGoal = viewModel.goal.toIntOrNull() ?: hintTask?.goal ?: 1

                        if (finalTitle.isBlank() || finalGoal < 1) {
                            // TODO Show Toast or Snack bar error message here
                            return@Button
                        }

                        //MAP SCHEDULE
                        val taskSchedule = when (viewModel.repeatType) {
                            "Every day" -> com.jwsulzen.habitrpg.data.model.Schedule.Daily
                            "Every week" -> com.jwsulzen.habitrpg.data.model.Schedule.Weekly(setOf(viewModel.selectedDate.dayOfWeek))
                            "Every month" -> com.jwsulzen.habitrpg.data.model.Schedule.Monthly(viewModel.selectedDate.dayOfMonth)
                            "Custom" -> com.jwsulzen.habitrpg.data.model.Schedule.Interval(
                                everyXDays = viewModel.intervalValue.toIntOrNull() ?: 1,
                                startDate = viewModel.selectedDate
                            )
                            else -> com.jwsulzen.habitrpg.data.model.Schedule.Daily // Fallback
                        }

                        //CREATE TASK
                        viewModel.onSaveTask(
                            skillId = skillId,
                            schedule = taskSchedule,
                            isMeasurable = true
                        )
                        navController.popBackStack(Screen.TasklistScreen.route, inclusive = false)
                    },
                    modifier = Modifier.align(Alignment.End),
                    shape = RoundedCornerShape(size = 12.dp)
                ) {
                    Text(text = if (isEditMode) "Save Changes" else "Create Quest")
                }
                //endregion
            }
        }
    }
}