package com.maximus.moodtrack.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.maximus.moodtrack.data.model.Mood
import com.maximus.moodtrack.util.DataConverters

@Database(entities = [Mood::class], version = 1, exportSchema = false)
@TypeConverters(DataConverters::class)
abstract class MoodDatabase : RoomDatabase() {
    abstract fun getMoodDao(): MoodDao
}