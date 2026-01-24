package com.jwsulzen.habitrpg.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.jwsulzen.habitrpg.data.repository.GameRepository
import com.jwsulzen.habitrpg.ui.screens.completionsettings.CompletionSettingsScreen
import com.jwsulzen.habitrpg.ui.screens.measurablesettings.MeasurableSettingsScreen
import com.jwsulzen.habitrpg.ui.screens.selectskill.SelectSkillScreen
import com.jwsulzen.habitrpg.ui.screens.dashboard.TaskListScreen
import com.jwsulzen.habitrpg.ui.screens.stats.StatsScreen


@Composable
fun Navigation(repository: GameRepository) {
    val navController = rememberNavController()

    // This helps us track which screen is currently active
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val isAddingTask = currentRoute?.startsWith(Screen.CompletionSettingsScreen.route) == true || currentRoute == Screen.SelectSkillScreen.route

    //TODO add swipe gesture to navigate between TaskListView and Stats screens somehow
    Scaffold(
        bottomBar = {
            if (!isAddingTask) { //hide bar for add task screen
                NavigationBar {
                    // Home Button
                    NavigationBarItem(
                        selected = currentRoute == Screen.TasklistScreen.route,
                        onClick = {
                            navController.navigate(Screen.TasklistScreen.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        label = { Text("Home") },
                        icon = { Icon(Icons.Default.Home, contentDescription = null) }
                    )

                    // Add Button (Central)
                    NavigationBarItem(
                        selected = false, //override default pill appearance
                        onClick = {
                            navController.navigate(Screen.SelectSkillScreen.route)
                        },
                        icon = {
                            //surface/card to make diamond shape
                            androidx.compose.material3.Surface(
                                modifier = androidx.compose.ui.Modifier.size(82.dp),
                                shape = androidx.compose.foundation.shape.CutCornerShape(50), //50% cut = diamond
                                color = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                                shadowElevation = 4.dp
                            ) {
                                Box(contentAlignment = androidx.compose.ui.Alignment.Center) {
                                    Icon(
                                        modifier = Modifier.size(48.dp),
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Add"
                                    )
                                }
                            }
                        },
                        alwaysShowLabel = false //keep focus on diamond(?)
                    )

                    // Stats Button
                    NavigationBarItem(
                        selected = currentRoute == Screen.StatsScreen.route, // Update this when you have a stats route
                        onClick = {
                            navController.navigate(Screen.StatsScreen.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        label = { Text("Stats") },
                        icon = { Icon(Icons.Default.Info, contentDescription = null) }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavHost(
                navController = navController,
                startDestination = Screen.TasklistScreen.route
            ) {
                //TASKLIST SCREEN
                composable(
                    route = Screen.TasklistScreen.route,
                    enterTransition =  { slideIntoContainer(towards = AnimatedContentTransitionScope.SlideDirection.Right) },
                    exitTransition =  { slideOutOfContainer(towards = AnimatedContentTransitionScope.SlideDirection.Left) }
                    ) {
                    TaskListScreen(navController = navController, repository = repository) //pass repository to the screen for viewmodel
                }

                //SELECT SKILL SCREEN
                composable(
                    route = Screen.SelectSkillScreen.route,
                    enterTransition = { slideIntoContainer(towards = AnimatedContentTransitionScope.SlideDirection.Up) },
                    exitTransition = { slideOutOfContainer(towards = AnimatedContentTransitionScope.SlideDirection.Down) }
                ) {
                    SelectSkillScreen(navController = navController, repository = repository)
                }

                //COMPLETION SETTINGS SCREEN
                composable(
                    route = Screen.CompletionSettingsScreen.route + "/{skillId}",
                    enterTransition = { slideIntoContainer(towards = AnimatedContentTransitionScope.SlideDirection.Up) },
                    exitTransition = { slideOutOfContainer(towards = AnimatedContentTransitionScope.SlideDirection.Down) }
                ) { backStackEntry ->
                    //Extract ID from the nav args
                    val skillId = backStackEntry.arguments?.getString("skillId") ?: ""
                    CompletionSettingsScreen(
                        navController = navController,
                        repository = repository,
                        skillId = skillId
                    )
                }

                //MEASURABLE SETTINGS SCREEN
                composable(
                    route = Screen.MeasurableSettingsScreen.route + "/{skillId}",
                    enterTransition = { slideIntoContainer(towards = AnimatedContentTransitionScope.SlideDirection.Up) },
                    exitTransition = { slideOutOfContainer(towards = AnimatedContentTransitionScope.SlideDirection.Down) }
                ) { backStackEntry ->
                    //Extract ID from the nav args
                    val skillId = backStackEntry.arguments?.getString("skillId") ?: ""
                    MeasurableSettingsScreen(
                        navController = navController,
                        repository = repository,
                        skillId = skillId
                    )
                }

                //STATS SCREEN
                composable(
                    route = Screen.StatsScreen.route,
                    enterTransition =  { slideIntoContainer(towards = AnimatedContentTransitionScope.SlideDirection.Left) },
                    exitTransition =  { slideOutOfContainer(towards = AnimatedContentTransitionScope.SlideDirection.Right) }
                ) {
                    StatsScreen(navController = navController, repository = repository)
                }

            }
        }
    }
}