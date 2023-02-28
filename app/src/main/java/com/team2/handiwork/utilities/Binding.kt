package com.team2.handiwork.utilities

import android.widget.TextView
import androidx.databinding.BindingAdapter
import java.text.SimpleDateFormat
import java.util.*


object Binding {
    @BindingAdapter("android:text")
    @JvmStatic
    fun setText(view: TextView, timestamp: Long) {
        val sdf = SimpleDateFormat("yyyy-MM-dd, HH:mm", Locale.getDefault())
        view.text = sdf.format(timestamp * 1000)
    }
}