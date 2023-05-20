package com.maximus.moodtrack.util

import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.databinding.BindingAdapter
import com.maximus.moodtrack.util.DateHelpers.toCalendar
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar.DAY_OF_MONTH

object DataBinding {

    @JvmStatic
    @BindingAdapter("app:dayOfTheMonth")
    fun loadError(inputLayout: TextView, date: Date?) {
        val dayOfMonth = date?.toCalendar()?.get(DAY_OF_MONTH)
        val suffix = getDaySuffix(dayOfMonth)
        val format = SimpleDateFormat("d MMMM", Locale("ru"))

        inputLayout.text = date?.let { format.format(it) }
    }

    @JvmStatic
    @BindingAdapter("app:tint")
    fun setImageTint(imageView: ImageView, @ColorInt color: Int) {
        imageView.setColorFilter(color)
    }

    private fun getDaySuffix(dayOfMonth: Int?): String {
        if (dayOfMonth == null) {
            return ""
        }
        return "-ое"
    }
}