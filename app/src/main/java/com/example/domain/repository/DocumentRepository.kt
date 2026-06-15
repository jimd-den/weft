package com.example.domain.repository

import com.example.data.local.NoteEntity
import com.example.data.local.QuestionEntity
import com.example.data.local.SourceEntity
import kotlinx.coroutines.flow.Flow

interface DocumentRepository {
    fun getSources(): Flow<List<SourceEntity>>
    fun getSourceById(id: String): Flow<SourceEntity?>
    suspend fun addSource(source: SourceEntity)

    fun getNotes(sourceId: String): Flow<List<NoteEntity>>
    suspend fun addNote(sourceId: String, text: String)
    
    fun getQuestions(sourceId: String): Flow<List<QuestionEntity>>
    suspend fun addQuestion(sourceId: String, text: String)
}
