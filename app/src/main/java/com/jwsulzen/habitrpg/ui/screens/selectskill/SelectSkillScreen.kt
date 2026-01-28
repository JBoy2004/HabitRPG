package com.jwsulzen.habitrpg.ui.screens.selectskill


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.jwsulzen.habitrpg.data.model.Skill
import com.jwsulzen.habitrpg.data.repository.GameRepository
import com.jwsulzen.habitrpg.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectSkillScreen(
    navController: NavController,
    repository: GameRepository
) {
    val viewModel: SelectSkillViewModel = viewModel(
        factory = SelectSkillViewModel.provideFactory(repository)
    )

    val skills by viewModel.skills.collectAsState()

    //State for BottomSheet
    var showSheet by remember { mutableStateOf(false) }
    var selectedSkill by remember { mutableStateOf<Skill?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Select Skill Category",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
            )

        LazyVerticalGrid(
            columns = GridCells.Fixed(3), //Currently forcing 3 columns, change for tablets?
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
           items(skills) { skill ->
               SkillGridItem(
                   skill = skill,
                   onClick = {
                       selectedSkill = skill
                       showSheet = true
                   }
               )
           }
        }
    }

    //Modal Bottom Sheet for selection
    if (showSheet) {
        AlertDialog(
            onDismissRequest = { showSheet = false },
            title = {
                Text(
                    text = "What type of quest is this?",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    //Option 1: Completion
                    Button(
                        onClick = {
                            showSheet = false
                            navController.navigate("${Screen.TaskSettingsScreen.route}/${selectedSkill?.id}/false")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = CutCornerShape(8.dp)
                    ) {
                        Text("Completion (e.g. Clean my room)")
                    }

                    //Option 2: Measurable
                    OutlinedButton( //TODO change to Button for consistency?
                        onClick = {
                            showSheet = false
                            navController.navigate("${Screen.TaskSettingsScreen.route}/${selectedSkill?.id}/true")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = CutCornerShape(8.dp)
                    ) {
                        Text("Measurable (e.g. Read 6 pages)")
                    }
                }
            },
            shape = CutCornerShape(16.dp),
            confirmButton = {}
        )
    }
}

@Composable
fun SkillGridItem(skill: Skill, onClick: () -> Unit) {
    Card(
        onClick = onClick, //cards can be clickable!
        shape = CutCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = Modifier
            .aspectRatio(1f) //make card a perfect square
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
            ) {
            Text(text = skill.emoji, fontSize = 32.sp)
            Spacer(Modifier.height(4.dp))
            Text(
                text = skill.name,
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}