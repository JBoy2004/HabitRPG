package com.jwsulzen.habitrpg.ui.screens.dashboard

import androidx.compose.animation.core.exponentialDecay
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.AnchoredDraggableDefaults
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.runtime.SideEffect
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.IntOffset
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.jwsulzen.habitrpg.data.model.Task
import com.jwsulzen.habitrpg.data.repository.GameRepository
import com.jwsulzen.habitrpg.data.seed.DefaultSkills
import com.jwsulzen.habitrpg.domain.RpgEngine
import com.jwsulzen.habitrpg.ui.navigation.Screen

@Composable
fun DashboardScreen(
    navController: NavController,
    repository: GameRepository
) {
    val viewModel: DashboardViewModel = viewModel(
        factory = DashboardViewModel.provideFactory(repository)
    )

    val tasksDaily by viewModel.tasksDaily.collectAsState()
    val tasksWeekly by viewModel.tasksWeekly.collectAsState()
    val tasksMonthly by viewModel.tasksMonthly.collectAsState()


    val currentLevel by viewModel.level.collectAsState(1)
    val currentTotalXp by viewModel.totalXp.collectAsState(0)

    var selectedTaskForDialog by remember { mutableStateOf<Task?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        //PLAYER INFO
        Card(
            shape = CutCornerShape(10),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                //LEVEL DISPLAY
                Card(
                    shape = CutCornerShape(0),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier.size(60.dp) //fixed size for level box
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "LVL", style = MaterialTheme.typography.labelSmall)
                            Text(
                                text = currentLevel.toString(),
                                style = MaterialTheme.typography.headlineMedium,
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                //NAME AND XP BAR
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "John Doe", //TODO add input for player name
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    //XP BAR
                    val progress = RpgEngine.getLevelProgress(currentTotalXp)
                    val xpIntoLevel = RpgEngine.getXpIntoLevel(currentTotalXp)
                    val xpRequiredForLevel = RpgEngine.getXpRequiredForLevel(currentLevel)

                    Column {
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp),
                            strokeCap = StrokeCap.Butt, //sharp edges(?)
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                            drawStopIndicator = {}
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
                                text = "$xpIntoLevel / $xpRequiredForLevel XP",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }
        }

        //TITLE (Current Quests)
        Text(
            text = "Current Quests",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .padding(bottom = 16.dp)
                .fillMaxWidth()
        )

        //TASK LISTS
        val allListsEmpty = tasksDaily.isEmpty() && tasksWeekly.isEmpty() && tasksMonthly.isEmpty()

        if (allListsEmpty) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Create a task to get started!",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                //DAILY SECTION
                if (tasksDaily.isNotEmpty()) {
                    item { SectionHeader("Daily Quests") }
                    items(tasksDaily, key = { it.id }) { task ->
                        TaskItem(
                            task = task,
                            onQuickLog = { amount -> viewModel.onQuickLog(task, amount) },
                            onCardClick = { selectedTaskForDialog = it }
                        )
                    }
                }

                //WEEKLY SECTION
                if (tasksWeekly.isNotEmpty()) {
                    item { SectionHeader("Weekly Quests") }
                    items(tasksWeekly, key = { it.id }) { task ->
                        TaskItem(
                            task = task,
                            onQuickLog = { amount -> viewModel.onQuickLog(task, amount) },
                            onCardClick = { selectedTaskForDialog = it }
                        )
                    }
                }

                //MONTHLY SECTION
                if (tasksMonthly.isNotEmpty()) {
                    item { SectionHeader("Monthly Quests") }
                    items(tasksMonthly, key = { it.id }) { task ->
                        TaskItem(
                            task = task,
                            onQuickLog = { amount -> viewModel.onQuickLog(task, amount) },
                            onCardClick = { selectedTaskForDialog = it }
                        )
                    }
                }
            }
        }



        selectedTaskForDialog?.let { task ->
            ProgressInputDialog(
                task = task,
                onDismiss = { selectedTaskForDialog = null },
                onConfirm = { amount, date, newGoal ->
                    viewModel.onLogProgress(task, amount, date, newGoal)
                    selectedTaskForDialog = null
                },
                onEdit = {
                    selectedTaskForDialog = null
                    navController.navigate("${Screen.TaskSettingsScreen.route}/${task.skillId}/${task.isMeasurable}?taskId=${task.id}")
                },
                repository = repository
            )
        }
    }
}

@Composable
fun TaskItem(
    task: Task,
    onQuickLog: (Int) -> Unit,
    onCardClick: (Task) -> Unit
    ) {
    val skillEmoji = DefaultSkills.skills.find { it.id == task.skillId }?.emoji ?: "❓"
    val density = LocalDensity.current

    //Swipe distance
    val anchorRange = with(density) { 80.dp.toPx() }

    //Haptics
    val haptic = LocalHapticFeedback.current

    //Initialize Draggable State
    val state = remember {
        AnchoredDraggableState(
            initialValue = DragValue.Center
        )
    }

    //Define specs
    val snapAnimationSpec = spring<Float>()
    val decayAnimationSpec = exponentialDecay<Float>()

    //Define fling behavior
    val flingBehavior = AnchoredDraggableDefaults.flingBehavior(
        state = state,
        positionalThreshold = { distance: Float -> distance * 0.5f },
        animationSpec = snapAnimationSpec,
    )


    //Update anchors
    SideEffect {
        state.updateAnchors(
            DraggableAnchors {
                DragValue.Left at -anchorRange
                DragValue.Center at 0f
                DragValue.Right at anchorRange
            }
        )
    }

    //Trigger actions when the state changes
    LaunchedEffect(state.settledValue) {
        if (state.settledValue != DragValue.Center) {
            val amount = if (state.settledValue == DragValue.Right) 1 else -1

            onQuickLog(amount)
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)

            //Return to center
            state.animateTo(DragValue.Center)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CutCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        val offset = if (state.offset.isNaN()) 0f else state.offset
        val swipeProgress = (kotlin.math.abs(offset) / anchorRange).coerceIn(0f, 1f)

        //RED BACKGROUND
        if (offset < 0) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color(0xFFD93F3F).copy(alpha = swipeProgress))
            )
        }

        //GREEN BACKGROUND
        if (offset > 0) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color(0xFF53D93F).copy(alpha = swipeProgress))
            )
        }

        //Icons that show behind card while swiping
        Row(
            modifier = Modifier
                .matchParentSize()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "+", fontSize = 40.sp, color = Color.White)
            Text(text = "-", fontSize = 40.sp, color = Color.White)
        }

        //CARD FOR TASK CONTENT DISPLAY
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(state.requireOffset().toInt(), 0) }
                .anchoredDraggable(
                    state,
                    Orientation.Horizontal,
                    flingBehavior = flingBehavior
                ),
            onClick = { onCardClick(task) },
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
            colors = CardDefaults.cardColors( //Set color to green if task is met
                containerColor = if (task.isGoalReached) { Color(0xFFC8E6C9) } else { MaterialTheme.colorScheme.surfaceVariant }
            ),
            shape = CutCornerShape(8.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .padding(top = 16.dp, bottom = 6.dp, start = 16.dp, end = 16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    //EMOJI BOX
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = skillEmoji, fontSize = 30.sp)
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    //DIFFICULTY + XP DISPLAY
                    Column (modifier = Modifier.weight(1f)) {
                        Text(text = task.title, style = MaterialTheme.typography.titleLarge)

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = task.difficulty.name,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                text = "  +${task.difficulty.baseXp} XP",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    //PROGRESS DISPLAY (e.g. 1/3)
                    Text(
                        text = "${task.currentProgress} / ${task.goal}",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (task.isGoalReached) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurface
                    )
                }
                //PROGRESS BAR
                val progressFraction = if (task.goal > 0) {
                    (task.currentProgress.toFloat() / task.goal.toFloat()).coerceIn(0f, 1f)
                } else 0f

                val progressColor = when {
                    progressFraction < 0.5 -> {
                        //Interpolate between Red and Yellow (0% to 50%)
                        androidx.compose.ui.graphics.lerp(
                            start = Color(0xFFD93F3F), //Red
                            stop = Color(0xFFFFD700), //Yellow
                            fraction = progressFraction * 2f //Scale to 0-1
                        )
                    }
                    progressFraction < 1f -> {
                        //Interpolate between Yellow and Green (50% to 100%)
                        androidx.compose.ui.graphics.lerp(
                            start = Color(0xFFFFD700), //Yellow
                            stop = Color(0xFF53D93F),  //Green
                            fraction = (progressFraction - 0.5f) * 2f //Scale to 0-1
                        )
                    }
                    else -> Color(0xFF2E7D32) //Dark green for 100%+
                }

                LinearProgressIndicator(
                    progress = { task.currentProgress.toFloat()/task.goal.toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp),
//                        .drawBehind {} //TODO add sexy glow behind the progress bar
                    strokeCap = StrokeCap.Butt, //sharp edges
                    color = progressColor,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    drawStopIndicator = {}
                    )
            }
        }
    }
}

enum class DragValue { Left, Center, Right }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressInputDialog(
    task: Task,
    repository: GameRepository,
    onDismiss: () -> Unit,
    onConfirm: (Int, java.time.LocalDate, Int) -> Unit,
    onEdit: () -> Unit
) {
    var inputText by remember { mutableStateOf("") }
    var goalText by remember { mutableStateOf(task.goal.toString()) }

    var selectedDate by remember { mutableStateOf(java.time.LocalDate.now()) }
    var showDatePicker by remember { mutableStateOf(false) }

    var currentProgressPlaceholder by remember { mutableStateOf("0")}

    var activityDates by remember { mutableStateOf<List<java.time.LocalDate>>(emptyList()) }

    LaunchedEffect(task.id) {
        activityDates = repository.getDatesWithProgress(task.id)
    }

    LaunchedEffect(selectedDate, task) {
        val historicalProgress = repository.getProgressForTaskOnDate(task.id, selectedDate)
        currentProgressPlaceholder = historicalProgress.toString()
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate.atStartOfDay(java.time.ZoneId.of("UTC")).toInstant().toEpochMilli(),
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    return utcTimeMillis <= System.currentTimeMillis() //Prevent future dates
                }
            }
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        selectedDate = java.time.Instant.ofEpochMilli(it)
                            .atZone(java.time.ZoneId.of("UTC"))
                            .toLocalDate()
                    }
                    showDatePicker = false
                }) { Text("OK") }
            }
        ) {
            DatePicker(state = datePickerState, showModeToggle = false)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.Edit,
                        contentDescription = "Edit Task"
                    )
                }
            }
        },
        text = {
            Column (verticalArrangement = Arrangement.spacedBy(12.dp)) {
                //PROGRESS LOGGING
                Column {
                    Text("Update Progress:", style = MaterialTheme.typography.labelLarge)
                    Spacer(Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.height(IntrinsicSize.Min)
                        ) {
                        OutlinedTextField(
                            value = inputText,
                            onValueChange = { if (it.all { char -> char.isDigit() }) inputText = it },
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            placeholder = { Text(currentProgressPlaceholder) },
                            singleLine = true,
                            trailingIcon = {
                                val unitText = if (!task.unit.isNullOrBlank()) task.unit else "times"
                                Text(unitText, modifier = Modifier.padding(end = 8.dp))
                            }
                        )

                        Spacer(Modifier.width(8.dp))

                        OutlinedButton(
                            onClick = { showDatePicker = true },
                            contentPadding = PaddingValues(horizontal = 12.dp),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.fillMaxHeight()
                        ) {
                            val buttonLabel = if (selectedDate == java.time.LocalDate.now())  {
                                "Today"
                            } else  {
                                val formatter = java.time.format.DateTimeFormatter.ofPattern("MMM d")
                                selectedDate.format(formatter)
                            }
                            Text(buttonLabel)
                        }
                    }
                }

                //QUICK GOAL EDIT
                Column {
                    Text("Current Goal:", style = MaterialTheme.typography.labelLarge)
                    Spacer(Modifier.height(4.dp))
                    OutlinedTextField(
                        value = goalText,
                        onValueChange = { if (it.all { char -> char.isDigit() }) goalText = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        trailingIcon = {
                            val unitText = if (!task.unit.isNullOrBlank()) task.unit else "times"
                            val periodLabel = when (task.schedule) {
                                is com.jwsulzen.habitrpg.data.model.Schedule.Daily -> "day"
                                is com.jwsulzen.habitrpg.data.model.Schedule.Weekly -> "week"
                                is com.jwsulzen.habitrpg.data.model.Schedule.Monthly -> "month"
                                is com.jwsulzen.habitrpg.data.model.Schedule.Interval -> "${task.schedule.everyXDays} days"
                            }
                            Text(
                                "$unitText/$periodLabel",
                                modifier = Modifier.padding(end = 8.dp)
                            )
                        }
                    )
                }

                //LAST WEEK HEATMAP TODO Use a different calendar library so I can tag days with data saved
                /*
                Text("Recent Activity", style = MaterialTheme.typography.labelSmall)
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    //Generate last 7 days
                    (0..6).reversed().forEach { daysAgo ->
                        val date = java.time.LocalDate.now().minusDays(daysAgo.toLong())
                        val hasData = activityDates.contains(date)

                        Box (
                            modifier = Modifier
                                .size(12.dp)
                                .background(
                                    color = if (hasData) MaterialTheme.colorScheme.primary else Color.LightGray,
                                    shape = RoundedCornerShape(2.dp)
                                )
                        )
                    }
                }
                */
            }
        },
        shape = CutCornerShape(16.dp),
        confirmButton = {
            Button(onClick = {
                val amount = inputText.toIntOrNull() ?: 0
                val finalGoal = goalText.toIntOrNull() ?: task.goal

                onConfirm(amount, selectedDate, finalGoal)
            }) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun SectionHeader(title: String) {
    Column(modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)) {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = 1.sp
        )
        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )
    }
}