package com.maximus.moodtrack.ui.moodlist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations.map
import androidx.lifecycle.Transformations.switchMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maximus.moodtrack.data.repository.MoodRepository
import com.maximus.moodtrack.data.model.Mood
import com.maximus.moodtrack.util.DateHelpers.isSameDay
import com.maximus.moodtrack.util.Event
import com.maximus.moodtrack.util.joinToString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MoodListViewModel@Inject constructor(private val moodRepositoryImpl: MoodRepository) : ViewModel() {

    val selectedDate = MutableLiveData(Date())
    val exportEvent = MutableLiveData<Event<String>?>()

    val moods = switchMap(selectedDate) { moodRepositoryImpl.getMoodsForMonth(it) }

    val currentMood = map(moods) { monthMoods ->
        monthMoods
            .firstOrNull { mood -> selectedDate.value?.let { mood.date.isSameDay(it) } == true }
            ?.mood
    }

    fun addMood(moodScore: Int) {
        val date = selectedDate.value ?: Date()
        val mood = Mood(date, moodScore)

        viewModelScope.launch {
            moodRepositoryImpl.addMood(mood)
        }
    }

    fun export() {
        viewModelScope.launch {
            val moods = moodRepositoryImpl.getAllMoods()
            val data = joinToString(moods)
            viewModelScope.launch(Dispatchers.Main) { exportEvent.value = Event(data) }
        }
    }

}