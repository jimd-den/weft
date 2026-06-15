package com.example.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.Stage
import com.example.ui.theme.*

@Composable
fun LcarsTopBar(
    isSoundOn: Boolean,
    onSoundToggle: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 22.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "LCARS·WEFT 47-R",
            style = MaterialTheme.typography.labelMedium,
            color = LcarsRead,
            modifier = Modifier.clickable { onSettingsClick() }
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "THREAD: ROME",
                style = MaterialTheme.typography.labelSmall,
                color = LcarsDim
            )
            Surface(
                modifier = Modifier.clickable { onSoundToggle() },
                shape = RoundedCornerShape(999.dp),
                color = Color.Transparent,
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSoundOn) LcarsRead else Color(0xFF2A2A34))
            ) {
                Text(
                    text = if (isSoundOn) "◍ SND" else "◌ SND",
                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isSoundOn) LcarsRead else LcarsDim
                )
            }
        }
    }
}

@Composable
fun LcarsDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 22.dp)
            .height(2.dp)
            .background(Color(0xFF2A2A3A)) // Placeholder linear gradient via drawBehind can be added
            .drawBehind {
                drawRect(
                    color = LcarsRead,
                    size = Size(width = size.width * 0.4f, height = size.height), // partial fade line
                    alpha = 0.5f
                )
            }
    )
}

@Composable
fun LcarsBottomDock(
    currentStage: Stage,
    onStageSelect: (Int) -> Unit,
    onNext: () -> Unit,
    onPrev: () -> Unit,
    onCapture: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(LcarsBlack)
            .navigationBarsPadding()
            .padding(bottom = 14.dp)
    ) {
        // Rail
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 9.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Stage.entries.forEachIndexed { index, stage ->
                val isActive = currentStage == stage
                val stageColor = when (stage) {
                    Stage.SURVEY -> LcarsSurvey
                    Stage.QUESTION -> LcarsQuestion
                    Stage.READ -> LcarsRead
                    Stage.RECORD -> LcarsRecord
                    Stage.RECITE -> LcarsRecite
                    Stage.REVIEW -> LcarsReview
                    Stage.REFLECT -> LcarsReflect
                }
                val weight by animateFloatAsState(targetValue = if (isActive) 2.1f else 1f, label = "weight")
                val alpha by animateFloatAsState(targetValue = if (isActive) 1f else 0.45f, label = "alpha")
                
                Box(
                    modifier = Modifier
                        .weight(weight)
                        .height(34.dp)
                        .alpha(alpha)
                        .clip(RoundedCornerShape(4.dp))
                        .background(stageColor)
                        .clickable { onStageSelect(index) },
                    contentAlignment = Alignment.Center
                ) {
                    Row {
                        Text(
                            text = stage.name.take(3),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = LcarsBlack
                        )
                        if (isActive) {
                            Text(
                                text = " · ${stage.title}",
                                style = LocalTextStyle.current.copy(
                                    fontSize = 10.sp, 
                                    letterSpacing = 0.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = LcarsBlack
                                )
                            )
                        }
                    }
                }
            }
        }
        
        // Elbow action bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(62.dp)
                .padding(horizontal = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val dynamicColor = when(currentStage) {
                    Stage.SURVEY -> LcarsSurvey
                    Stage.QUESTION -> LcarsQuestion
                    Stage.READ -> LcarsRead
                    Stage.RECORD -> LcarsRecord
                    Stage.RECITE -> LcarsRecite
                    Stage.REVIEW -> LcarsReview
                    Stage.REFLECT -> LcarsReflect
            }
            // Elbow
            Box(
                modifier = Modifier
                    .width(74.dp)
                    .fillMaxHeight()
                    .background(
                        color = dynamicColor,
                        shape = RoundedCornerShape(topStart = 18.dp, bottomStart = 18.dp)
                    )
                    .clickable { onCapture() },
                contentAlignment = Alignment.BottomCenter
            ) {
                // To create the 'swept inner corner', we overlay a black square with bottom-left rounded radius
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 1.dp)
                        .size(26.dp)
                        .background(LcarsBlack, RoundedCornerShape(bottomStart = 22.dp))
                )
                Text(
                    text = "⊕",
                    modifier = Modifier.padding(bottom = 9.dp),
                    fontSize = 26.sp,
                    color = LcarsBlack,
                    fontWeight = FontWeight.Bold
                )
            }
            
            // Nav Content
            Row(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .background(
                        color = LcarsBg2,
                        shape = RoundedCornerShape(topEnd = 18.dp, bottomEnd = 18.dp)
                    )
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "◂",
                    color = dynamicColor,
                    fontSize = 22.sp,
                    modifier = Modifier
                        .clickable { onPrev() }
                        .padding(6.dp)
                )
                Text(
                    text = currentStage.title,
                    style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 2.5.sp),
                    color = LcarsText
                )
                Text(
                    text = "▸",
                    color = dynamicColor,
                    fontSize = 22.sp,
                    modifier = Modifier
                        .clickable { onNext() }
                        .padding(6.dp)
                )
                Text(
                    text = "⠿ grep",
                    style = MaterialTheme.typography.labelSmall,
                    color = LcarsDim
                )
            }
        }
    }
}
