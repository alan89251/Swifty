package com.team2.handiwork.utilities

import android.text.TextUtils

class Utility {
    companion object {
        fun isValidEmail(target: CharSequence): Boolean {
            return if (TextUtils.isEmpty(target)) {
                false;
            } else {
                android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
            }
        }
    }
}