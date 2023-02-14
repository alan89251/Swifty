package com.team2.handiwork.utilities

import android.app.Activity
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import android.widget.Switch
import com.team2.handiwork.R
import java.text.SimpleDateFormat
import java.util.*

class Utility {
    companion object {

        var sTheme = 0;
        var THEME_AGENT = 0
        var THEME_EMPLOYER = 1

        fun changeToTheme(activity: Activity, theme: Int) {
            sTheme = theme
            activity.finish()
            activity.startActivity(Intent(activity, activity.javaClass))
            activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        fun onActivityCreateSetTheme(activity: Activity){
            when (sTheme){
                THEME_EMPLOYER-> activity.setTheme(R.style.AppThemeEmployer)
                THEME_AGENT -> activity.setTheme(R.style.AppThemeAgent)
                else -> {
                    Log.d("hehehe", "onActivityCreateSetTheme: some error happen")
                    activity.setTheme(R.style.AppThemeEmployer)
                }
            }
        }

        fun convertLongToDate(timeStamp : Long): String {
            val date = Date(timeStamp)
            val format = SimpleDateFormat("yyyy/MM/dd")
            return  format.format(date)
        }

        fun convertLongToHour(timeStamp : Long): String {
            val date = Date(timeStamp)
            val format = SimpleDateFormat("HH:mm")
            return  format.format(date)
        }

        fun isValidEmail(target: CharSequence): Boolean {
            return if (TextUtils.isEmpty(target)) {
                false
            } else {
                android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches()
            }
        }


    }


}