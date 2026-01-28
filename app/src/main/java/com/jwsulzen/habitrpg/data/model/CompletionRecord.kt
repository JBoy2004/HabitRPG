package com.jwsulzen.habitrpg.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "completion_history",
    indices = [Index(value = ["taskId", "date"], unique = true)]
    )
data class CompletionRecord(
    @PrimaryKey(autoGenerate = true) val recordId: Int = 0,
    val taskId: String,
    val date: LocalDate,
    val xpGained: Int,
    val progressAmount: Int
)