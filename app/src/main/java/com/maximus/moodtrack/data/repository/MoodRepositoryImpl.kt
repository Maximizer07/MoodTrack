package com.maximus.moodtrack.data.repository

import androidx.lifecycle.LiveData
import com.maximus.moodtrack.data.local.MoodDao
import com.maximus.moodtrack.data.model.Mood
import com.maximus.moodtrack.util.DateHelpers
import com.maximus.moodtrack.util.DateHelpers.atStartOfDay
import java.util.*
import javax.inject.Inject

class MoodRepositoryImpl  @Inject constructor(private val moodDao: MoodDao): MoodRepository {

    override fun addMood(mood: Mood) {
        val startOfDate = Calendar.getInstance()
            .apply { time = mood.date }
            .atStartOfDay()
            .time

        val validatedMood = mood.copy(date = startOfDate)

        moodDao.addMood(validatedMood)
    }

    override fun getAllMoods(): List<Mood> {
        return moodDao.getAll()
    }

    override fun getMoodsForMonth(date: Date): LiveData<List<Mood>> {
        val monthRange = DateHelpers.getMonthRange(date)
        return moodDao.getMoodsBetween(monthRange.first, monthRange.second)
    }
}

