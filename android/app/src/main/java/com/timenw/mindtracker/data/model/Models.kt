package com.timenw.mindtracker.data.model

import java.time.LocalDate

data class MindRecord(
    val id: Long = System.currentTimeMillis(),
    val emotion: Emotion = Emotion.CALM,
    val stressLevel: Int = 3,           // 1-5
    val happinessLevel: Int = 3,        // 1-5
    val anxietyLevel: Int = 1,          // 1-5
    val energyLevel: Int = 3,           // 1-5
    val mindfulnessMinutes: Int = 0,    // 冥想分钟
    val date: String = LocalDate.now().toString(),
    val note: String = "",
    val activities: List<MindActivity> = emptyList(),
    val sleepQuality: Int = 3,          // 1-5
    val socialLevel: Int = 3            // 1-5 社交满意度
) {
    val overallScore: Float get() = (happinessLevel + (6 - stressLevel) + (6 - anxietyLevel) + energyLevel + sleepQuality + socialLevel) / 6f
}

enum class Emotion(val displayName: String, val emoji: String) {
    JOY("喜悦", "😊"), CALM("平静", "😌"), GRATEFUL("感恩", "🙏"),
    ANXIOUS("焦虑", "😰"), SAD("悲伤", "😢"), ANGRY("愤怒", "😠"),
    TIRED("疲惫", "😪"), NEUTRAL("一般", "😐"), EXCITED("兴奋", "🤩")
}

enum class MindActivity(val displayName: String, val emoji: String) {
    MEDITATION("冥想", "🧘"), EXERCISE("运动", "🏃"), READING("阅读", "📖"),
    MUSIC("音乐", "🎵"), SOCIAL("社交", "👥"), NATURE("亲近自然", "🌿"),
    JOURNAL("写日记", "✍️"), HOBBY("爱好", "🎨"), THERAPY("心理咨询", "💬"),
    NONE("无", "—")
}

data class UserSettings(
    val dailyMindfulnessTargetMin: Int = 10,
    val journalReminderEnabled: Boolean = true,
    val journalReminderHour: Int = 21,
    val weightKg: Float = 70f,
    val age: Int = 30
)

data class DailyMindSummary(
    val date: String = LocalDate.now().toString(),
    val avgStress: Float = 0f,
    val avgHappiness: Float = 0f,
    val avgAnxiety: Float = 0f,
    val avgEnergy: Float = 0f,
    val avgOverall: Float = 0f,
    val totalMindfulnessMin: Int = 0,
    val recordCount: Int = 0,
    val records: List<MindRecord> = emptyList()
)
