package com.jwsulzen.habitrpg.ui.screens.stats

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.jwsulzen.habitrpg.data.model.SkillProgress
import com.jwsulzen.habitrpg.data.repository.GameRepository
import com.jwsulzen.habitrpg.data.seed.DefaultSkills
import com.jwsulzen.habitrpg.domain.RpgEngine
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults

@Composable
fun StatsScreen(
    navController: NavController,
    repository: GameRepository
) {
    val viewModel: StatsViewModel = viewModel(
        factory = StatsViewModel.provideFactory(repository)
    )

    val playerState by viewModel.playerState.collectAsState(
        initial = com.jwsulzen.habitrpg.data.model.PlayerState(emptyMap())
    )
    val level by viewModel.globalLevel.collectAsState(initial = 1)

    Column (modifier = Modifier.padding(16.dp)) {
        Text("Character Stats", style = MaterialTheme.typography.headlineMedium)
        Text ("Global Level: $level")

        Spacer(Modifier.height(16.dp))

        LazyColumn (
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(playerState.skills.values.toList()) { skillProgress ->
                SkillStatRow(skillProgress)
            }
        }

        //DANGER ZONE
        //TODO add danger zone area with multi-confirmation protection against accidentally deleting data
        //TODO upon reset and having no data the stats seems to display a set 3 categories?
        Spacer(Modifier.weight(1f)) //push button to bottom
        Button(
            onClick = { viewModel.onResetData() },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Reset All Data")
        }
    }
}

@Composable
fun SkillStatRow(progress: SkillProgress) {
    val skillDef = DefaultSkills.skills.find { it.id == progress.skillId }
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
                            text = progress.level.toString(),
                            style = MaterialTheme.typography.headlineMedium,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                //NAME AND XP BAR
                Text(
                    text = "${skillDef?.emoji ?: "❓"}  ${skillDef?.name ?: "Unknown Skill"}",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(8.dp))

                //XP BAR
                val levelProgress = RpgEngine.getLevelProgress(progress.xp)
                val xpIntoLevel = RpgEngine.getXpIntoLevel(progress.xp)
                val xpRequiredForLevel = RpgEngine.getXpRequiredForLevel(progress.level)

                Column {
                    LinearProgressIndicator(
                        progress = { levelProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp),
                        strokeCap = StrokeCap.Butt, //sharp edges(?)
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
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
}