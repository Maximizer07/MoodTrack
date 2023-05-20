package com.maximus.moodtrack.data.model

import com.maximus.moodtrack.R
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Mood(
    @PrimaryKey val date: Date,
    val mood: Int
) {
    fun getInvertedMood() = 5 - mood + 1

    fun getMoodColor() = when (mood) {
        1 -> R.color.colorCalendarMood1
        2 -> R.color.colorCalendarMood2
        3 -> R.color.colorCalendarMood3
        4 -> R.color.colorCalendarMood4
        5 -> R.color.colorCalendarMood5
        else -> R.color.colorBackground
    }

}

