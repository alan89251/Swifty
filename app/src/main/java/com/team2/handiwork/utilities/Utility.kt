package com.team2.handiwork.utilities

import android.app.Activity
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import android.widget.Switch
import com.team2.handiwork.R

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
                    Log.d("DLLM", "onActivityCreateSetTheme: some error happen")
                    activity.setTheme(R.style.AppThemeEmployer)
                }
            }
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