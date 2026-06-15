package com.example.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.NoteEntity
import com.example.data.local.QuestionEntity
import com.example.data.local.SourceEntity
import com.example.data.local.UserPreferencesRepository
import com.example.domain.model.Stage
import com.example.domain.repository.AiRepository
import com.example.domain.repository.DocumentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class MainViewModel(
    private val repository: DocumentRepository,
    private val aiRepository: AiRepository,
    private val prefs: UserPreferencesRepository
) : ViewModel() {

    private val _currentSourceId = MutableStateFlow<String?>(null)
    val currentSourceId = _currentSourceId.asStateFlow()

    private val _currentStage = MutableStateFlow(Stage.SURVEY)
    val currentStage = _currentStage.asStateFlow()

    private val _isSoundOn = MutableStateFlow(true)
    val isSoundOn = _isSoundOn.asStateFlow()

    private val _isCaptureSheetVisible = MutableStateFlow(false)
    val isCaptureSheetVisible = _isCaptureSheetVisible.asStateFlow()
    
    private val _isSettingsSheetVisible = MutableStateFlow(false)
    val isSettingsSheetVisible = _isSettingsSheetVisible.asStateFlow()

    val openRouterApiKey = prefs.apiKeyFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    val selectedAgent = prefs.selectedAgentFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "google/gemini-2.5-flash")

    val sources = repository.getSources()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentSource = _currentSourceId.flatMapLatest { id ->
        if (id == null) kotlinx.coroutines.flow.flowOf(null)
        else repository.getSourceById(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun captureSource(text: String) {
        viewModelScope.launch {
            val id = UUID.randomUUID().toString().take(6)
            val newSource = SourceEntity(
                id = "0x$id",
                title = "Processing...",
                content = text,
                readingTimeMn = text.split(Regex("\\s+")).size / 200,
                yearTag = "NEW"
            )
            repository.addSource(newSource)
            _isCaptureSheetVisible.value = false
            
            // Process AI
            launch {
                val summary = aiRepository.generateSourceSummary(text) ?: "Unknown Document"
                repository.addSource(newSource.copy(title = summary.take(40).trim()))
                
                val qs = aiRepository.generateQuestions(text)
                qs?.forEach { q ->
                    repository.addQuestion(newSource.id, q)
                }
            }
        }
    }
    
    val notes: StateFlow<List<NoteEntity>> = _currentSourceId.flatMapLatest { id ->
        if (id == null) kotlinx.coroutines.flow.flowOf(emptyList())
        else repository.getNotes(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val questions: StateFlow<List<QuestionEntity>> = _currentSourceId.flatMapLatest { id ->
        if (id == null) kotlinx.coroutines.flow.flowOf(emptyList())
        else repository.getQuestions(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSourceId(id: String) {
        _currentSourceId.value = id
        setStage(0)
    }

    fun clearSourceId() {
        _currentSourceId.value = null
    }

    fun setStage(index: Int) {
        _currentStage.value = Stage.fromIndex(index)
    }

    fun nextStage() {
        val nextOrd = (_currentStage.value.ordinal + 1) % Stage.entries.size
        _currentStage.value = Stage.fromIndex(nextOrd)
    }

    fun prevStage() {
        val prevOrd = if (_currentStage.value.ordinal - 1 < 0) Stage.entries.size - 1 else _currentStage.value.ordinal - 1
        _currentStage.value = Stage.fromIndex(prevOrd)
    }

    fun toggleSound() {
        _isSoundOn.value = !_isSoundOn.value
    }

    fun showCaptureSheet(show: Boolean) {
        _isCaptureSheetVisible.value = show
    }

    fun showSettingsSheet(show: Boolean) {
        _isSettingsSheetVisible.value = show
    }

    fun captureHighlight(text: String) {
        val sid = _currentSourceId.value ?: return
        viewModelScope.launch {
            repository.addNote(sid, text)
        }
    }
    
    fun captureQuestion(text: String) {
        val sid = _currentSourceId.value ?: return
        viewModelScope.launch {
            repository.addQuestion(sid, text)
        }
    }

    fun saveApiKey(key: String) {
        viewModelScope.launch {
            prefs.saveApiKey(key)
        }
    }

    fun saveAgent(agent: String) {
        viewModelScope.launch {
            prefs.saveSelectedAgent(agent)
        }
    }
    
    // Actually we need a reference to document dao in repo for sources, 
    // let's assume we implement it
}

class MainViewModelFactory(
    private val repository: DocumentRepository,
    private val aiRepository: AiRepository,
    private val prefs: UserPreferencesRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(repository, aiRepository, prefs) as T
    }
}
