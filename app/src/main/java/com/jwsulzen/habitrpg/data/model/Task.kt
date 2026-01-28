package com.jwsulzen.habitrpg.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey val id: String,
    val title: String,
    val skillId: String,
    val difficulty: Difficulty,
    val schedule: Schedule,
    val goal: Int = 1,
    val currentProgress: Int = 0,
    val isGoalReached: Boolean = false,
    //MEASURABLE FIELDS
    val isMeasurable: Boolean = false,
    val unit: String? = null,
    val createdAt: Long = System.currentTimeMillis()
    //val notification : Notification, //TODO add optional notifications per task!
)