package com.example.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.Stage
import com.example.data.local.NoteEntity
import com.example.data.local.QuestionEntity
import com.example.data.local.SourceEntity
import com.example.ui.theme.*

@Composable
fun StagePanels(
    currentStage: Stage,
    currentSource: SourceEntity?,
    notes: List<NoteEntity>,
    questions: List<QuestionEntity>,
    onCaptureHighlight: (String) -> Unit,
    onCaptureQuestion: (String) -> Unit,
    onExitThread: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accentColor = when (currentStage) {
        Stage.SURVEY -> LcarsSurvey
        Stage.QUESTION -> LcarsQuestion
        Stage.READ -> LcarsRead
        Stage.RECORD -> LcarsRecord
        Stage.RECITE -> LcarsRecite
        Stage.REVIEW -> LcarsReview
        Stage.REFLECT -> LcarsReflect
    }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.padding(bottom = 14.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = currentStage.title,
                    style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 3.sp),
                    color = accentColor
                )
                Text(
                    text = "· ${currentStage.tag}",
                    style = MaterialTheme.typography.labelSmall,
                    color = LcarsDim
                )
            }
            Text(
                text = "✕ EXIT THREAD",
                style = MaterialTheme.typography.labelSmall,
                color = LcarsDim,
                modifier = Modifier.clickable { onExitThread() }
            )
        }

        Box(modifier = Modifier.weight(1f)) {
            if (currentSource == null) return@Box
            when (currentStage) {
                Stage.SURVEY -> SurveyPanel(currentSource, accentColor)
                Stage.QUESTION -> QuestionPanel(questions, accentColor, onCaptureQuestion)
                Stage.READ -> ReadPanel(currentSource, accentColor, onCaptureHighlight)
                Stage.RECORD -> RecordPanel(notes, accentColor, onCaptureHighlight)
                Stage.RECITE -> RecitePanel()
                Stage.REVIEW -> ReviewPanel()
                Stage.REFLECT -> ReflectPanel(accentColor)
            }
        }
    }
}

@Composable
fun Hint(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = LcarsDim,
        modifier = Modifier.padding(top = 16.dp)
    )
}

@Composable
fun SurveyPanel(source: com.example.data.local.SourceEntity, accentColor: Color) {
    Column {
        Text(
            text = source.title.ifBlank { "UNTITLED SOURCE" }.uppercase(),
            style = MaterialTheme.typography.headlineMedium
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("SOURCE ${source.id}", "≈ ${source.readingTimeMn} MIN READ", "EST ${source.yearTag}").forEach { tag ->
                Text(
                    text = tag,
                    style = MaterialTheme.typography.labelSmall,
                    color = accentColor,
                    modifier = Modifier
                        .background(LcarsBg2, RoundedCornerShape(999.dp))
                        .border(1.dp, accentColor.copy(alpha = 0.4f), RoundedCornerShape(999.dp))
                        .padding(horizontal = 11.dp, vertical = 5.dp)
                )
            }
        }
        Text(
            text = source.content.take(200) + "...",
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFFD9D6CF)
        )
        Hint("SKIM THE SHAPE ▸ THEN WRITE YOUR QUESTIONS")
    }
}

@Composable
fun QuestionPanel(questions: List<QuestionEntity>, accentColor: Color, onAddQuestion: (String) -> Unit) {
    var newQuestion by remember { mutableStateOf("") }
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        if (questions.isEmpty()) {
            NoteItem(src = "GENERATING...", text = "Agents are processing the text or no questions found.", accentColor = accentColor)
        } else {
            questions.forEach { q ->
                NoteItem(src = "Q · BEFORE READING", text = q.text, accentColor = accentColor)
            }
        }
        Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = newQuestion,
                onValueChange = { newQuestion = it },
                modifier = Modifier.weight(1f),
                textStyle = MaterialTheme.typography.bodyMedium,
                placeholder = { Text("Add a manual question...", color = LcarsDim) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = accentColor,
                    unfocusedBorderColor = Color(0xFF2A2A34),
                    focusedTextColor = LcarsText,
                    unfocusedTextColor = LcarsText
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "ADD",
                style = MaterialTheme.typography.labelSmall,
                color = LcarsBlack,
                modifier = Modifier
                    .background(accentColor, RoundedCornerShape(4.dp))
                    .clickable { 
                        if (newQuestion.isNotBlank()) {
                            onAddQuestion(newQuestion)
                            newQuestion = ""
                        }
                    }
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            )
        }
        Hint("TWO QUESTIONS EARN YOU A FOCUSED READ")
    }
}

@Composable
fun ReadPanel(source: com.example.data.local.SourceEntity, accentColor: Color, onCaptureHighlight: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        val paragraph = source.content.split("\n\n").firstOrNull() ?: source.content
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(background = accentColor.copy(alpha = 0.32f), color = LcarsText)) {
                    append(paragraph.take(150))
                }
                append(paragraph.drop(150))
            },
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.clickable { onCaptureHighlight(paragraph.take(150)) }
        )
        Text(
            text = source.content.drop(paragraph.length).take(500),
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFFD9D6CF)
        )
        Hint("HIGHLIGHT A LINE ▸ IT PIPES TO RECORD")
    }
}

@Composable
fun RecordPanel(notes: List<NoteEntity>, accentColor: Color, onAddNote: (String) -> Unit) {
    var newNote by remember { mutableStateOf("") }
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        if (notes.isEmpty()) {
            NoteItem(src = "AWAITING NOTES", text = "Tap lines in READ to capture notes.", accentColor = accentColor)
        } else {
            notes.forEach { note ->
                NoteItem(src = "NOTE ◂ from highlight", text = note.text, accentColor = accentColor)
            }
        }
        Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = newNote,
                onValueChange = { newNote = it },
                modifier = Modifier.weight(1f),
                textStyle = MaterialTheme.typography.bodyMedium,
                placeholder = { Text("Add manual note...", color = LcarsDim) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = accentColor,
                    unfocusedBorderColor = Color(0xFF2A2A34),
                    focusedTextColor = LcarsText,
                    unfocusedTextColor = LcarsText
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "ADD",
                style = MaterialTheme.typography.labelSmall,
                color = LcarsBlack,
                modifier = Modifier
                    .background(accentColor, RoundedCornerShape(4.dp))
                    .clickable { 
                        if (newNote.isNotBlank()) {
                            onAddNote(newNote)
                            newNote = ""
                        }
                    }
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            )
        }
        Hint("ONE IDEA PER NOTE ▸ IN YOUR OWN WORDS")
    }
}

@Composable
fun RecitePanel() {
    var flipped by remember { mutableStateOf(false) }
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 190.dp)
                .background(LcarsBg2, RoundedCornerShape(14.dp))
                .border(1.dp, Color(0xFF1C1C26), RoundedCornerShape(14.dp))
                .clickable { flipped = !flipped }
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = "Q — How did Rome stop one consul from seizing lasting power?",
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 19.sp, lineHeight = 26.sp),
                    color = Color(0xFFE6E3DB)
                )
                if (flipped) {
                    Text(
                        text = "A — Two consuls, mutual veto, one-year terms.",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                        color = LcarsRecite
                    )
                } else {
                    Text(
                        text = "▸ TAP TO REVEAL",
                        style = MaterialTheme.typography.labelSmall,
                        color = LcarsDim
                    )
                }
            }
        }
        if (flipped) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("AGAIN" to LcarsAlert, "HARD" to LcarsQuestion, "EASY" to LcarsReview).forEach { (label, color) ->
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        color = color,
                        modifier = Modifier
                            .weight(1f)
                            .background(LcarsBg2, RoundedCornerShape(topEnd = 999.dp, bottomEnd = 999.dp, topStart = 6.dp, bottomStart = 6.dp))
                            .border(1.dp, color, RoundedCornerShape(topEnd = 999.dp, bottomEnd = 999.dp, topStart = 6.dp, bottomStart = 6.dp))
                            .padding(vertical = 12.dp)
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }
}

@Composable
fun ReviewPanel() {
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 190.dp)
                .background(LcarsBg2, RoundedCornerShape(14.dp))
                .border(1.dp, Color(0xFF1C1C26), RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "12",
                    fontSize = 64.sp,
                    color = LcarsReview,
                    letterSpacing = 1.sp,
                    fontFamily = MaterialTheme.typography.headlineMedium.fontFamily,
                    lineHeight = 64.sp
                )
                Text(
                    text = "CARDS DUE TODAY",
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 2.5.sp),
                    color = LcarsDim,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
        Hint("TWO MINUTES ▸ THE DAILY RITUAL")
    }
}

@Composable
fun ReflectPanel(accentColor: Color) {
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(LcarsBg2, RoundedCornerShape(14.dp))
                .border(1.dp, Color(0xFF1C1C26), RoundedCornerShape(14.dp))
        ) {
            // Simplified static graph for MVP
        }
        Hint("LONG-PRESS A NOTE ▸ WEAVE A LINK")
    }
}

@Composable
fun NoteItem(src: String, text: String, accentColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(LcarsBg2, RoundedCornerShape(topEnd = 6.dp, bottomEnd = 16.dp, topStart = 6.dp, bottomStart = 6.dp))
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .padding(end = 16.dp)
                .width(4.dp)
                .height(44.dp)
                .background(accentColor)
        )
        Column {
            Text(
                text = src,
                style = MaterialTheme.typography.labelSmall,
                color = LcarsDim,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFFDDD9D1)
            )
        }
    }
}
