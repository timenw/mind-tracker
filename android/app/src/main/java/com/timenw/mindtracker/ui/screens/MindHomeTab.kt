package com.timenw.mindtracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.timenw.mindtracker.data.model.*
import com.timenw.mindtracker.ui.components.MoodMeter
import com.timenw.mindtracker.ui.components.EmptyStateView
import com.timenw.mindtracker.ui.components.SummaryCard
import com.timenw.mindtracker.ui.theme.MindDanger
import com.timenw.mindtracker.ui.theme.MindWarning
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MindHomeTab(
    summary: DailyMindSummary,
    records: List<MindRecord>,
    targetMindfulMin: Int,
    onAddRecord: (MindRecord) -> Unit,
    onRemoveRecord: (Long) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    val formatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    Column(modifier = Modifier.fillMaxSize()) {
        CenterAlignedTopAppBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🧘", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("静了么", fontWeight = FontWeight.Bold)
                }
            }
        )

        LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    MoodMeter(score = summary.avgOverall, size = 150)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "冥想 ${summary.totalMindfulnessMin}min / 目标 ${targetMindfulMin}min",
                    style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth(), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            }

            item {
                Row(modifier = Modifier.fillMaxWidth()) {
                    SummaryCard(title = "压力", value = "${String.format("%.1f", summary.avgStress)}", modifier = Modifier.weight(1f), emoji = "😰")
                    Spacer(modifier = Modifier.width(8.dp))
                    SummaryCard(title = "快乐", value = "${String.format("%.1f", summary.avgHappiness)}", modifier = Modifier.weight(1f), emoji = "😊")
                    Spacer(modifier = Modifier.width(8.dp))
                    SummaryCard(title = "焦虑", value = "${String.format("%.1f", summary.avgAnxiety)}", modifier = Modifier.weight(1f), emoji = "😟")
                }
            }

            item {
                FilledTonalButton(onClick = { showAddDialog = true }, modifier = Modifier.fillMaxWidth().height(48.dp)) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("记录心情")
                }
            }

            item { Text("今日记录", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold) }

            if (records.isEmpty()) {
                item { EmptyStateView(emoji = "🧘", title = "还没有心情记录", subtitle = "记录一下今天的情绪和感受吧") }
            } else {
                items(records.reversed(), key = { it.id }) { record ->
                    Card(modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))) {
                        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = record.emotion.emoji, fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = "${record.emotion.displayName} · ${String.format("%.1f", record.overallScore)}分",
                                        style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                                    Text(text = "压力${record.stressLevel} · 快乐${record.happinessLevel} · 焦虑${record.anxietyLevel} · 精力${record.energyLevel}",
                                        style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    if (record.mindfulnessMinutes > 0) {
                                        Text(text = "冥想 ${record.mindfulnessMinutes} 分钟",
                                            style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                                    }
                                }
                                Text(text = record.date,
                                    style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                IconButton(onClick = { onRemoveRecord(record.id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "删除", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                            if (record.activities.isNotEmpty() && record.activities.first() != MindActivity.NONE) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    record.activities.take(5).forEach { activity ->
                                        SuggestionChip(onClick = {},
                                            label = { Text("${activity.emoji} ${activity.displayName}", style = MaterialTheme.typography.labelSmall) },
                                            modifier = Modifier.height(28.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }

    if (showAddDialog) {
        MindRecordDialog(onDismiss = { showAddDialog = false },
            onSave = { record -> onAddRecord(record); showAddDialog = false },
            targetMindfulMin = targetMindfulMin)
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MindRecordDialog(onDismiss: () -> Unit, onSave: (MindRecord) -> Unit, targetMindfulMin: Int) {
    var emotion by remember { mutableStateOf(Emotion.CALM) }
    var stress by remember { mutableIntStateOf(3) }
    var happiness by remember { mutableIntStateOf(3) }
    var anxiety by remember { mutableIntStateOf(1) }
    var energy by remember { mutableIntStateOf(3) }
    var mindfulMin by remember { mutableStateOf("") }
    var selectedActivities by remember { mutableStateOf(setOf<MindActivity>()) }
    var sleep by remember { mutableIntStateOf(3) }
    var social by remember { mutableIntStateOf(3) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("记录心情") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("当前情绪", style = MaterialTheme.typography.labelMedium)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Emotion.values().forEach { e ->
                        FilterChip(selected = emotion == e, onClick = { emotion = e },
                            label = { Text("${e.emoji} ${e.displayName}", style = MaterialTheme.typography.labelSmall) })
                    }
                }
                Text("压力: $stress", style = MaterialTheme.typography.labelMedium)
                Slider(value = stress.toFloat(), onValueChange = { stress = it.toInt() }, valueRange = 1f..5f, steps = 3)
                Text("快乐: $happiness", style = MaterialTheme.typography.labelMedium)
                Slider(value = happiness.toFloat(), onValueChange = { happiness = it.toInt() }, valueRange = 1f..5f, steps = 3)
                Text("焦虑: $anxiety", style = MaterialTheme.typography.labelMedium)
                Slider(value = anxiety.toFloat(), onValueChange = { anxiety = it.toInt() }, valueRange = 1f..5f, steps = 3)
                Text("精力: $energy", style = MaterialTheme.typography.labelMedium)
                Slider(value = energy.toFloat(), onValueChange = { energy = it.toInt() }, valueRange = 1f..5f, steps = 3)
                OutlinedTextField(value = mindfulMin,
                    onValueChange = { mindfulMin = it.filter { c -> c.isDigit() } },
                    label = { Text("冥想分钟 (目标 ${targetMindfulMin}min)") }, singleLine = true)
                Text("活动", style = MaterialTheme.typography.labelMedium)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    MindActivity.values().filter { it != MindActivity.NONE }.forEach { activity ->
                        FilterChip(selected = selectedActivities.contains(activity),
                            onClick = { selectedActivities = if (selectedActivities.contains(activity)) selectedActivities - activity else selectedActivities + activity },
                            label = { Text("${activity.emoji} ${activity.displayName}", style = MaterialTheme.typography.labelSmall) })
                    }
                }
                Text("睡眠质量: $sleep", style = MaterialTheme.typography.labelMedium)
                Slider(value = sleep.toFloat(), onValueChange = { sleep = it.toInt() }, valueRange = 1f..5f, steps = 3)
                Text("社交满意度: $social", style = MaterialTheme.typography.labelMedium)
                Slider(value = social.toFloat(), onValueChange = { social = it.toInt() }, valueRange = 1f..5f, steps = 3)
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onSave(MindRecord(
                    emotion = emotion, stressLevel = stress, happinessLevel = happiness,
                    anxietyLevel = anxiety, energyLevel = energy,
                    mindfulnessMinutes = mindfulMin.toIntOrNull() ?: 0,
                    activities = selectedActivities.toList(),
                    sleepQuality = sleep, socialLevel = social
                ))
            }) { Text("保存") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("取消") } }
    )
}
