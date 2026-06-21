package com.timenw.mindtracker.data.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.timenw.mindtracker.data.model.*
import java.time.LocalDate

class MindRepository(private val context: Context) {
    private val prefs = context.getSharedPreferences("mind_tracker", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun getMindRecords(date: LocalDate = LocalDate.now()): List<MindRecord> {
        val key = "minds_${date}"
        val json = prefs.getString(key, "[]") ?: "[]"
        val type = object : TypeToken<List<MindRecord>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    fun addMindRecord(record: MindRecord) {
        val records = getMindRecords(LocalDate.parse(record.date)).toMutableList()
        records.add(record)
        saveMindRecords(records, LocalDate.parse(record.date))
    }

    fun removeMindRecord(id: Long, date: LocalDate = LocalDate.now()) {
        val records = getMindRecords(date).toMutableList()
        records.removeAll { it.id == id }
        saveMindRecords(records, date)
    }

    private fun saveMindRecords(records: List<MindRecord>, date: LocalDate) {
        val key = "minds_${date}"
        prefs.edit().putString(key, gson.toJson(records)).apply()
    }

    fun getDailySummary(date: LocalDate = LocalDate.now()): DailyMindSummary {
        val records = getMindRecords(date)
        if (records.isEmpty()) return DailyMindSummary(date = date.toString())
        return DailyMindSummary(
            date = date.toString(),
            avgStress = records.map { it.stressLevel }.average().toFloat(),
            avgHappiness = records.map { it.happinessLevel }.average().toFloat(),
            avgAnxiety = records.map { it.anxietyLevel }.average().toFloat(),
            avgEnergy = records.map { it.energyLevel }.average().toFloat(),
            avgOverall = records.map { it.overallScore }.average().toFloat(),
            totalMindfulnessMin = records.sumOf { it.mindfulnessMinutes },
            recordCount = records.size,
            records = records
        )
    }

    fun getWeeklyData(): List<DailyMindSummary> {
        val today = LocalDate.now()
        return (0..6).map { daysAgo -> getDailySummary(today.minusDays(daysAgo.toLong())) }.reversed()
    }

    fun getEmotionFrequency(days: Int = 30): Map<String, Int> {
        val today = LocalDate.now()
        val freq = mutableMapOf<String, Int>()
        for (i in 0 until days) {
            getMindRecords(today.minusDays(i.toLong())).forEach { record ->
                freq[record.emotion.name] = (freq[record.emotion.name] ?: 0) + 1
            }
        }
        return freq
    }

    data class EmotionTotal(val emotion: String, val count: Int, val avgOverall: Float)
    fun getEmotionAnalysis(): List<EmotionTotal> {
        val today = LocalDate.now()
        val earliestDate = today.minusDays(365)
        val emotionMap = mutableMapOf<String, MutableList<MindRecord>>()
        var date = earliestDate
        while (!date.isAfter(today)) {
            getMindRecords(date).forEach { record -> emotionMap.getOrPut(record.emotion.name) { mutableListOf() }.add(record) }
            date = date.plusDays(1)
        }
        return emotionMap.map { (name, records) -> EmotionTotal(name, records.size, records.map { it.overallScore }.average().toFloat()) }.sortedByDescending { it.count }
    }

    fun getSettings(): UserSettings {
        val json = prefs.getString("settings", null)
        return if (json != null) gson.fromJson(json, UserSettings::class.java) else UserSettings()
    }

    fun saveSettings(settings: UserSettings) { prefs.edit().putString("settings", gson.toJson(settings)).apply() }
}
