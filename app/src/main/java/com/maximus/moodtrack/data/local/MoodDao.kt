package com.maximus.moodtrack.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.maximus.moodtrack.data.model.Mood

@Dao
interface MoodDao {
    @Query("SELECT * FROM mood")
    fun getAll(): List<Mood>

    @Query("SELECT * FROM mood WHERE date BETWEEN :from AND :to")
    fun getMoodsBetween(from: Long, to: Long): LiveData<List<Mood>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addMood(mood: Mood)
}

