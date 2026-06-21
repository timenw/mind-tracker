package com.timenw.mindtracker.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.timenw.mindtracker.data.model.DailyMindSummary
import com.timenw.mindtracker.data.model.Emotion
import com.timenw.mindtracker.data.repository.MindRepository
import com.timenw.mindtracker.ui.components.SummaryCard
import com.timenw.mindtracker.ui.theme.MindSafe
import com.timenw.mindtracker.ui.theme.MindDanger
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsTab(weeklyData: List<DailyMindSummary>, emotionAnalysis: List<MindRepository.EmotionTotal>) {
    Column(modifier = Modifier.fillMaxSize()) {
        CenterAlignedTopAppBar(title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.BarChart, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("数据统计", fontWeight = FontWeight.Bold)
            }
        })
        LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item { Spacer(modifier = Modifier.height(8.dp)) }
            item {
                val avgOverall = if (weeklyData.isNotEmpty()) weeklyData.map { it.avgOverall }.average().toFloat() else 0f
                val avgStress = if (weeklyData.isNotEmpty()) weeklyData.map { it.avgStress }.average().toFloat() else 0f
                val avgHappy = if (weeklyData.isNotEmpty()) weeklyData.map { it.avgHappiness }.average().toFloat() else 0f
                val totalMindful = weeklyData.sumOf { it.totalMindfulnessMin }
                Text("本周总览", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    SummaryCard(title = "综合评分", value = "${String.format("%.1f", avgOverall)}", modifier = Modifier.weight(1f), emoji = "🧘")
                    Spacer(modifier = Modifier.width(8.dp))
                    SummaryCard(title = "平均压力", value = "${String.format("%.1f", avgStress)}", modifier = Modifier.weight(1f), emoji = "😰")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    SummaryCard(title = "平均快乐", value = "${String.format("%.1f", avgHappy)}", modifier = Modifier.weight(1f), emoji = "😊")
                    Spacer(modifier = Modifier.width(8.dp))
                    SummaryCard(title = "冥想时长", value = "${totalMindful}min", modifier = Modifier.weight(1f), emoji = "⏱️")
                }
            }
            item {
                Text("本周情绪趋势", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        if (weeklyData.all { it.recordCount == 0 }) {
                            Text(text = "暂无数据，开始记录心情吧 🧘", style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(vertical = 24.dp))
                        } else {
                            MoodBarChart(weeklyData)
                        }
                    }
                }
            }
            item {
                Text("情绪分析", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        if (emotionAnalysis.isEmpty()) {
                            Text(text = "还没有心情记录", style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(vertical = 16.dp))
                        } else {
                            emotionAnalysis.forEach { et ->
                                val emotion = try { Emotion.valueOf(et.emotion) } catch (e: Exception) { Emotion.NEUTRAL }
                                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(text = "${emotion.emoji} ${emotion.displayName}", style = MaterialTheme.typography.bodyMedium)
                                    Text(text = "${et.count}次 · 均分${String.format("%.1f", et.avgOverall)}",
                                        style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                    }
                }
            }
            item {
                Text("健康提示", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                Card(modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f))) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("🧘 心理健康建议：", fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("• 每天花 10 分钟进行正念冥想", style = MaterialTheme.typography.bodySmall)
                        Text("• 保持规律运动，每周至少 150 分钟", style = MaterialTheme.typography.bodySmall)
                        Text("• 维持良好的社交关系", style = MaterialTheme.typography.bodySmall)
                        Text("• 保证充足睡眠，每晚 7-9 小时", style = MaterialTheme.typography.bodySmall)
                        Text("• 如持续感到焦虑或抑郁，请及时寻求专业帮助", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun MoodBarChart(data: List<DailyMindSummary>) {
    val maxScore = 5f
    val dayFormatter = SimpleDateFormat("E", Locale.getDefault())
    Row(modifier = Modifier.fillMaxWidth().height(160.dp), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.Bottom) {
        data.forEach { summary ->
            val barHeight = (summary.avgOverall / maxScore).coerceIn(0f, 1f)
            val isGood = summary.avgOverall >= 3.5f
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Bottom, modifier = Modifier.weight(1f)) {
                Text(text = String.format("%.1f", summary.avgOverall), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(4.dp))
                Canvas(modifier = Modifier.fillMaxWidth(0.6f).height(100.dp)) {
                    val barWidth = size.width; val barH = size.height * barHeight
                    drawRect(color = if (isGood) MindSafe else MindDanger, topLeft = Offset(0f, size.height - barH),
                        size = androidx.compose.ui.geometry.Size(barWidth, barH))
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = try { dayFormatter.format(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(summary.date) ?: Date()) } catch (e: Exception) { summary.date.takeLast(2) },
                    style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
