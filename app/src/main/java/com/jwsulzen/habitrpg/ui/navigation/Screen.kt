package com.jwsulzen.habitrpg.ui.navigation

sealed class Screen(val route: String) {
    object TasklistScreen : Screen("task_list_screen")
    object SelectSkillScreen : Screen("select_skill_screen")
    object TaskSettingsScreen : Screen("task_settings_screen")
    object StatsScreen : Screen("stats_screen")
}