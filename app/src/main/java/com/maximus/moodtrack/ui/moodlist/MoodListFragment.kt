package com.maximus.moodtrack.ui.moodlist

import android.content.ClipData
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import com.applandeo.materialcalendarview.CalendarDay
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.listeners.OnDayClickListener
import com.google.android.material.datepicker.MaterialDatePicker
import com.maximus.moodtrack.R
import com.maximus.moodtrack.data.model.Mood
import com.maximus.moodtrack.databinding.FragmentMoodListBinding
import com.maximus.moodtrack.util.ChartConfiguration.addMoods
import com.maximus.moodtrack.util.ChartConfiguration.configure
import com.maximus.moodtrack.util.Constants
import com.maximus.moodtrack.util.DateHelpers
import com.maximus.moodtrack.util.DateHelpers.toCalendar
import com.maximus.moodtrack.util.Event
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.util.Calendar
import java.util.Date

@AndroidEntryPoint
class MoodListFragment : Fragment() {

    lateinit var binding: FragmentMoodListBinding
    private val viewModel: MoodListViewModel by viewModels ()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentMoodListBinding.inflate(layoutInflater).also {
            it.viewModel = viewModel
            it.lifecycleOwner = this
        }
        initialiseView()
        watchData()
        return binding.root
    }



    private fun initialiseView() {
        binding.date.setOnClickListener { showDatePicker() }
        configureCalendar()
        binding.chart.configure(getString(R.string.no_moods))
    }

    private fun watchData() {
        viewModel.moods.observe(this) { moods ->
            updateCalendar(moods)
            binding.chart.addMoods(moods, ContextCompat.getColor(requireActivity(), R.color.line))
        }

        viewModel.selectedDate.observe(this) { initialiseView() }
        viewModel.exportEvent.observe(this) { exportMoodFile(it) }
    }

    private fun showDatePicker() {
        MaterialDatePicker.Builder
            .datePicker()
            .build()
            .apply {
                addOnPositiveButtonClickListener {
                    viewModel.selectedDate.value = Date().apply { time = it }
                }
            }
            .show(requireActivity().supportFragmentManager, Constants.TAG_DATE_PICKER)
    }

    private fun configureCalendar() {
        val date = viewModel.selectedDate.value ?: return
        val range = DateHelpers.getMonthRange(date)
        val from = Calendar.getInstance().apply { timeInMillis = range.first }
        val to = Calendar.getInstance().apply { timeInMillis = range.second }

        binding.calendar.apply {
            setMinimumDate(from)
            setMaximumDate(to)
            setDate(date.toCalendar())
            setSwipeEnabled(false)
            setOnDayClickListener(object : OnDayClickListener {
                override fun onDayClick(eventDay: EventDay) {
                    viewModel.selectedDate.value = eventDay.calendar.time
                }
            })
        }
    }

    private fun updateCalendar(moods: List<Mood>) {
        val days = moods.map { mood ->
            CalendarDay(mood.date.toCalendar()).also { day ->
                day.backgroundDrawable = ContextCompat.getDrawable(requireActivity(), R.drawable.calendar_event)
                    ?.also {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            it.setTint(ContextCompat.getColor(requireActivity(), mood.getMoodColor()))
                        }
                    }
            }
        }

        binding.calendar.setCalendarDays(days)
    }

    private fun exportMoodFile(event: Event<String>?) {
        createMoodFile(event)?.let { exportFile(it) }
    }

    private fun createMoodFile(event: Event<String>?): File? {
        return event?.unhandledData?.let {
            val exportsDir = File(requireContext().filesDir, "exports").apply { mkdirs() }
            File(exportsDir, "${Date()}.csv").apply { writeBytes(it.toByteArray()) }
        }
    }

    private fun exportFile(file: File) {
        val uri = FileProvider.getUriForFile(requireActivity(), requireActivity().packageName, file)
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/csv"
            clipData = ClipData("Mood Export", arrayOf(type), ClipData.Item(uri))
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val shareIntent = Intent.createChooser(sendIntent, getString(R.string.export))
        startActivity(shareIntent)
    }



}