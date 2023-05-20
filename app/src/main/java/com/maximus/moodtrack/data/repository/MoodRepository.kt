package com.maximus.moodtrack.data.repository

import androidx.lifecycle.LiveData
import com.maximus.moodtrack.data.model.Mood
import java.util.*

interface MoodRepository {
    fun addMood(mood: Mood)
    fun getAllMoods(): List<Mood>
    fun getMoodsForMonth(date: Date): LiveData<List<Mood>>
}


