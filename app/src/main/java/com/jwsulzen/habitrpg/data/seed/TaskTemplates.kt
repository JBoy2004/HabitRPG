package com.jwsulzen.habitrpg.data.seed

import com.jwsulzen.habitrpg.data.model.Task
import com.jwsulzen.habitrpg.data.model.Schedule
import com.jwsulzen.habitrpg.data.model.Difficulty

object TaskTemplates {
    val templates = listOf(
        //region Reading
        Task(
            id = "task_reading_measurable",
            title = "Reading",
            skillId = "reading_id",
            difficulty = Difficulty.MEDIUM,
            schedule = Schedule.Daily,
            goal = 6,
            currentProgress = 0,
            isGoalReached = false,
            isMeasurable = true,
            unit = "pages"
        ),
        Task(
            id = "task_reading_completion",
            title = "Reading",
            skillId = "reading_id",
            difficulty = Difficulty.MEDIUM,
            schedule = Schedule.Daily,
            goal = 1,
            currentProgress = 0,
            isGoalReached = false,
            isMeasurable = false,
            unit = ""
        ),
        //endregion
        //region Writing
        Task(
            id = "task_writing_measurable",
            title = "Writing",
            skillId = "writing_id",
            difficulty = Difficulty.MEDIUM,
            schedule = Schedule.Daily,
            goal = 20,
            currentProgress = 0,
            isGoalReached = false,
            isMeasurable = true,
            unit = "minutes"
        ),
        Task(
            id = "task_writing_completion",
            title = "Writing",
            skillId = "writing_id",
            difficulty = Difficulty.MEDIUM,
            schedule = Schedule.Daily,
            goal = 1,
            currentProgress = 0,
            isGoalReached = false,
            isMeasurable = false,
            unit = ""
        ),
        //endregion
        //region Programming
        Task(
            id = "task_programming_measurable",
            title = "Programming",
            skillId = "programming_id",
            difficulty = Difficulty.MEDIUM,
            schedule = Schedule.Daily,
            goal = 20,
            currentProgress = 0,
            isGoalReached = false,
            isMeasurable = true,
            unit = "minutes"
        ),
        Task(
            id = "task_programming_completion",
            title = "Programming",
            skillId = "programming_id",
            difficulty = Difficulty.MEDIUM,
            schedule = Schedule.Daily,
            goal = 20,
            currentProgress = 0,
            isGoalReached = false,
            isMeasurable = false,
            unit = ""
        ),
        //endregion
        //region Fitness
        Task(
            id = "task_fitness_measurable",
            title = "Push-Ups",
            skillId = "fitness_id",
            difficulty = Difficulty.MEDIUM,
            schedule = Schedule.Daily,
            goal = 20,
            currentProgress = 0,
            isGoalReached = false,
            isMeasurable = true,
            unit = "push-ups"
        ),
        Task(
            id = "task_fitness_completion",
            title = "Gym",
            skillId = "fitness_id",
            difficulty = Difficulty.MEDIUM,
            schedule = Schedule.Daily,
            goal = 1,
            currentProgress = 0,
            isGoalReached = false,
            isMeasurable = false,
            unit = ""
        ),
        //endregion
        //region Studying
        Task(
            id = "task_studying_measurable",
            title = "Studying",
            skillId = "studying_id",
            difficulty = Difficulty.MEDIUM,
            schedule = Schedule.Daily,
            goal = 20,
            currentProgress = 0,
            isGoalReached = false,
            isMeasurable = true,
            unit = "minutes"
        ),
        Task(
            id = "task_studying_completion",
            title = "Studying",
            skillId = "studying_id",
            difficulty = Difficulty.MEDIUM,
            schedule = Schedule.Daily,
            goal = 1,
            currentProgress = 0,
            isGoalReached = false,
            isMeasurable = false,
            unit = ""
        ),
        //endregion
        //region Language Learning
        Task(
            id = "task_language_learning_measurable",
            title = "Study Vocab",
            skillId = "language_learning_id",
            difficulty = Difficulty.EASY,
            schedule = Schedule.Daily,
            goal = 5,
            currentProgress = 0,
            isGoalReached = false,
            isMeasurable = true,
            unit = "vocab"
        ),
        Task(
            id = "task_language_learning_completion",
            title = "Practice output",
            skillId = "language_learning_id",
            difficulty = Difficulty.EASY,
            schedule = Schedule.Daily,
            goal = 5,
            currentProgress = 0,
            isGoalReached = false,
            isMeasurable = false,
            unit = ""
        ),
        //endregion
        //region Sleep
        Task(
            id = "task_sleep_measurable",
            title = "Sleep",
            skillId = "sleep_id",
            difficulty = Difficulty.EASY,
            schedule = Schedule.Daily,
            goal = 8,
            currentProgress = 0,
            isGoalReached = false,
            isMeasurable = true,
            unit = "hours"
        ),
        Task(
            id = "task_sleep_completion",
            title = "Go to bed on time",
            skillId = "sleep_id",
            difficulty = Difficulty.EASY,
            schedule = Schedule.Daily,
            goal = 1,
            currentProgress = 0,
            isGoalReached = false,
            isMeasurable = false,
            unit = ""
        ),
        //endregion
        //region Hygiene
        Task( //TODO this could be completion remake this example task
            id = "task_hygiene_measurable",
            title = "Brush Teeth",
            skillId = "hygiene_id",
            difficulty = Difficulty.TRIVIAL,
            schedule = Schedule.Daily,
            goal = 2,
            currentProgress = 0,
            isGoalReached = false,
            isMeasurable = true,
            unit = "times"
        ),
        Task(
            id = "task_hygiene_completion",
            title = "Flossing",
            skillId = "hygiene_id",
            difficulty = Difficulty.EASY,
            schedule = Schedule.Daily,
            goal = 1,
            currentProgress = 0,
            isGoalReached = false,
            isMeasurable = false,
            unit = ""
        ),
        //endregion
    )

    fun getTemplateForSkill(skillId: String, isMeasurable: Boolean): Task? {
        return templates.find {
            it.skillId == skillId && it.isMeasurable == isMeasurable
        }
    }
}