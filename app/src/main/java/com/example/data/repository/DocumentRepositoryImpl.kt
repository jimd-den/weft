package com.example.data.repository

import com.example.data.local.DocumentDao
import com.example.data.local.NoteEntity
import com.example.data.local.QuestionEntity
import com.example.data.local.SourceEntity
import com.example.domain.repository.DocumentRepository
import kotlinx.coroutines.flow.Flow

class DocumentRepositoryImpl(
    private val dao: DocumentDao
) : DocumentRepository {
    
    override fun getSources(): Flow<List<SourceEntity>> {
        return dao.getAllSources()
    }

    override fun getSourceById(id: String): Flow<SourceEntity?> {
        return dao.getSourceById(id)
    }

    override suspend fun addSource(source: SourceEntity) {
        dao.insertSource(source)
    }

    override fun getNotes(sourceId: String): Flow<List<NoteEntity>> {
        return dao.getNotesForSource(sourceId)
    }

    override suspend fun addNote(sourceId: String, text: String) {
        dao.insertNote(NoteEntity(sourceId = sourceId, text = text))
    }

    override fun getQuestions(sourceId: String): Flow<List<QuestionEntity>> {
        return dao.getQuestionsForSource(sourceId)
    }

    override suspend fun addQuestion(sourceId: String, text: String) {
        dao.insertQuestion(QuestionEntity(sourceId = sourceId, text = text))
    }
}
