package com.jwsulzen.habitrpg.data.seed

import com.jwsulzen.habitrpg.data.model.Skill
import com.jwsulzen.habitrpg.data.model.ProgressionType

object DefaultSkills {
    val skills = listOf(
        // Growth Skills:
        Skill(
            id = "reading_id",
            name = "Reading",
            progressionType = ProgressionType.Growth,
            emoji = "📖"
        ),
        Skill(
            id = "writing_id",
            name = "Writing",
            progressionType = ProgressionType.Growth,
            emoji = "📝"
        ),
        Skill(
            id = "art_id",
            name = "Art",
            progressionType = ProgressionType.Growth,
            emoji = "🎨"
        ),
        Skill(
            id = "programming_id",
            name = "Programming",
            progressionType = ProgressionType.Growth,
            emoji = "💻"
        ),
        Skill(
            id = "fitness_id",
            name = "Fitness",
            progressionType = ProgressionType.Growth,
            emoji = "💪"
        ),
        Skill(
            id = "studying_id",
            name = "Studying",
            progressionType = ProgressionType.Growth,
            emoji = "📓"
        ),
        Skill(
            id = "language_learning_id",
            name = "Language",
            progressionType = ProgressionType.Growth,
            emoji = "🌐"
        ),
        // Maintenance Skills:
        Skill(
            id = "sleep_id",
            name = "Sleep",
            progressionType = ProgressionType.Maintenance (
                maxLevel = 7,
                decayAfterDays = 2
            ),
            emoji = "👁"
        ),
        Skill(
            id = "hygiene_id",
            name = "Hygiene",
            progressionType = ProgressionType.Maintenance (
                maxLevel = 7,
                decayAfterDays = 2
            ),
            emoji = "🧼"
        )
    )
}