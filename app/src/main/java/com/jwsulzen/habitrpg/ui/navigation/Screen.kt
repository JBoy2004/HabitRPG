package com.jwsulzen.habitrpg.ui.navigation

sealed class Screen(val route: String) {
    object TasklistScreen : Screen("task_list_screen")
    object SelectSkillScreen : Screen("select_skill_screen")
    object CompletionSettingsScreen : Screen("completion_settings_screen")
    object MeasurableSettingsScreen : Screen("measurable_settings_screen")
    object StatsScreen : Screen("stats_screen")

    /* //For building dynamic routes (currently unused)
    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
    */
}