package com.maximus.moodtrack.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.maximus.moodtrack.util.SharedPrefConstants
import com.google.gson.Gson
import com.maximus.moodtrack.NotificationHelper
import com.maximus.moodtrack.data.local.MoodDao
import com.maximus.moodtrack.data.local.MoodDatabase
import com.maximus.moodtrack.data.repository.MoodRepository
import com.maximus.moodtrack.data.repository.MoodRepositoryImpl
import com.maximus.moodtrack.util.Constants.DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Provides
    @Singleton
    fun provideSharedPref(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(SharedPrefConstants.LOCAL_SHARED_PREF,Context.MODE_PRIVATE)
    }


    @Provides
    @Singleton
    fun provideGson(): Gson {
        return Gson()
    }

    @Provides
    @Singleton
    fun provideNotificationHelper(@ApplicationContext context: Context): NotificationHelper {
        return NotificationHelper(context)
    }

    @Provides
    @Singleton
    fun provideMoodTrackerDatabase(@ApplicationContext context: Context): MoodDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            MoodDatabase::class.java,
            DATABASE_NAME
        )
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideMoodTrackerDao(database: MoodDatabase): MoodDao {
        return database.getMoodDao()
    }

    @Provides
    @Singleton
    fun provideMoodTrackerRepository(moodTrackerDao: MoodDao): MoodRepository {
        return MoodRepositoryImpl(moodTrackerDao)
    }
}