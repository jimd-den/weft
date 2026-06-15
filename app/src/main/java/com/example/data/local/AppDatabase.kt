package com.example.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [SourceEntity::class, NoteEntity::class, QuestionEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun documentDao(): DocumentDao
}
