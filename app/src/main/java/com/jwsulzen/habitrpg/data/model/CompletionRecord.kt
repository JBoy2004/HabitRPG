package com.jwsulzen.habitrpg.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant
import java.time.LocalDate

@Entity(tableName = "completion_history")
data class CompletionRecord(
    @PrimaryKey(autoGenerate = true) val recordId: Int = 0,
    val taskId: String,
    val date: LocalDate,
    val xpGained: Int
)