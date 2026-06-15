package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.domain.model.Stage

@Entity(tableName = "sources")
data class SourceEntity(
    @PrimaryKey val id: String,
    val title: String,
    val content: String,
    val readingTimeMn: Int = 0,
    val yearTag: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sourceId: String,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "questions")
data class QuestionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sourceId: String,
    val text: String,
    val isAnswered: Boolean = false
)
