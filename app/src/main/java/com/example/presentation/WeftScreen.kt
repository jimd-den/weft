package com.example.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ui.theme.LcarsBlack

@Composable
fun WeftScreen(viewModel: MainViewModel) {
    val currentStage by viewModel.currentStage.collectAsStateWithLifecycle()
    val isSoundOn by viewModel.isSoundOn.collectAsStateWithLifecycle()
    val notes by viewModel.notes.collectAsStateWithLifecycle()
    val questions by viewModel.questions.collectAsStateWithLifecycle()
    val sources by viewModel.sources.collectAsStateWithLifecycle()
    val currentSource by viewModel.currentSource.collectAsStateWithLifecycle()
    
    val showCapture by viewModel.isCaptureSheetVisible.collectAsStateWithLifecycle()
    val showSettings by viewModel.isSettingsSheetVisible.collectAsStateWithLifecycle()
    val apiKey by viewModel.openRouterApiKey.collectAsStateWithLifecycle()
    val agent by viewModel.selectedAgent.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = LcarsBlack,
        topBar = {
            Column {
                LcarsTopBar(
                    isSoundOn = isSoundOn,
                    onSoundToggle = { viewModel.toggleSound() },
                    onSettingsClick = { viewModel.showSettingsSheet(true) }
                )
                LcarsDivider()
            }
        },
        bottomBar = {
            if (currentSource != null) {
                LcarsBottomDock(
                    currentStage = currentStage,
                    onStageSelect = { viewModel.setStage(it) },
                    onNext = { viewModel.nextStage() },
                    onPrev = { viewModel.prevStage() },
                    onCapture = { viewModel.showCaptureSheet(true) }
                )
            }
        }
    ) { innerPadding ->
        if (currentSource == null) {
            BridgeScreen(
                sources = sources,
                onSourceClick = { viewModel.setSourceId(it) },
                onCaptureClick = { viewModel.showCaptureSheet(true) },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 22.dp)
                    .padding(top = 20.dp, bottom = 6.dp)
            )
        } else {
            StagePanels(
                currentStage = currentStage,
                currentSource = currentSource,
                notes = notes,
                questions = questions,
                onCaptureHighlight = { viewModel.captureHighlight(it) },
                onExitThread = { viewModel.clearSourceId() },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 22.dp)
                    .padding(top = 20.dp, bottom = 6.dp)
            )
        }
    }
    
    if (showCapture) {
        CaptureSheet(
            onDismiss = { viewModel.showCaptureSheet(false) },
            onSubmit = { viewModel.captureSource(it) }
        )
    }
    
    if (showSettings) {
        SettingsSheet(
            apiKey = apiKey ?: "",
            selectedAgent = agent,
            onDismiss = { viewModel.showSettingsSheet(false) },
            onSave = { k, a -> 
                viewModel.saveApiKey(k)
                viewModel.saveAgent(a)
                viewModel.showSettingsSheet(false)
            }
        )
    }
}
