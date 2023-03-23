package com.team2.handiwork.utilities

import android.app.Activity
import android.content.Intent
import android.text.TextUtils
import com.team2.handiwork.R
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class Utility {
    companion object {

        var currentDisplayingTheme = 0;
        var THEME_AGENT = 0
        var THEME_EMPLOYER = 1

        fun setThemeToChange(theme: Int) {
            currentDisplayingTheme = theme
        }

        fun changeToTheme(activity: Activity, theme: Int) {
            currentDisplayingTheme = theme
            activity.finish()
            activity.startActivity(Intent(activity, activity.javaClass))
            activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        fun onActivityCreateSetTheme(activity: Activity) {
            when (currentDisplayingTheme) {
                THEME_EMPLOYER -> activity.setTheme(R.style.AppThemeEmployer)
                THEME_AGENT -> activity.setTheme(R.style.AppThemeAgent)
                else -> {
                    activity.setTheme(R.style.AppThemeEmployer)
                }
            }
        }

        fun convertLongToDate(timeStamp: Long): String {
            val date = Date(timeStamp)
            val format = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
            return format.format(date)
        }

        fun convertLongToHour(timeStamp: Long): String {
            val date = Date(timeStamp)
            val format = SimpleDateFormat("HH:mm", Locale.getDefault())
            return format.format(date)
        }

        fun isValidEmail(target: CharSequence): Boolean {
            return if (TextUtils.isEmpty(target)) {
                false
            } else {
                android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches()
            }
        }

        fun getDefaultMissionPhoto(subServiceType: String): Int {
            when (subServiceType) {
                "Furniture Assembly" -> return R.drawable.service_furniture_assembly
                "Product Assembly" -> return R.drawable.service_product_assembly
                "Cable Assembly" -> return R.drawable.service_cable_assembly
                "Mechanical Assembly" -> return R.drawable.service_residential_moving
                "Residential Cleaning" -> return R.drawable.service_residential_cleaning
                "Commercial Cleaning" -> return R.drawable.service_commercial_cleaning
                "Carpet Cleaning" -> return R.drawable.service_carpet_cleaning
                "Window Cleaning" -> return R.drawable.service_window_cleaning
                "Lawn Care" -> return R.drawable.service_lawn_care
                "Tree and Shrub Care" -> return R.drawable.service_tree_and_shrub_care
                "Garden Ned Maintenance" -> return R.drawable.service_garden_ned_maintencance
                "Residential Moving" -> return R.drawable.service_residential_moving
                "Commercial Moving" -> return R.drawable.service_commercial_moving
                "Long-Distance Moving" -> return R.drawable.service_long_distance_moving
                "Packing and Unpacking" -> return R.drawable.service_pack_and_unpack
                "Painting and Wall Finishing" -> return R.drawable.service_wall_painting
                "Flooring" -> return R.drawable.service_flooring
                "Home Addition" -> return R.drawable.service_home_addition
                "Bathroom Renovation" -> return R.drawable.service_bathroom_renovation
                "Appliance" -> return R.drawable.service_repair_appliance
                "Electrical" -> return R.drawable.service_repair_electrical
                "Furniture" -> return R.drawable.service_repair_furniture
                "Plumber" -> return R.drawable.service_repair_plumber
                "Food Delivery" -> return R.drawable.service_food_delivery
                "Package Delivery" -> return R.drawable.service_package_delivery
                "Grocery Delivery" -> return R.drawable.service_grocery_delivery
                "Furniture Delivery" -> return R.drawable.service_furniture_delivery
                "Show Shoveling" -> return R.drawable.service_snow_shoveling
                "Gutter Cleaning" -> return R.drawable.service_gutter_cleaning
                "Leaf Clean Up" -> return R.drawable.service_leaf_cleaning
                "Yard Work" -> return R.drawable.service_yard_work
                else -> return R.drawable.item_bg
            }
        }

        fun getDefaultServiceTypePhoto(serviceType: String): Int {
            when (serviceType) {
                "Assembling" -> return R.drawable.service_furniture_assembly
                "Cleaning" -> return R.drawable.service_residential_cleaning
                "Gardening" -> return R.drawable.service_lawn_care
                "Moving" -> return R.drawable.service_residential_moving
                "Renovation" -> return R.drawable.service_bathroom_renovation
                "Repair" -> return R.drawable.service_repair_appliance
                "Delivering" -> return R.drawable.service_food_delivery
                "Seasonal" -> return R.drawable.service_snow_shoveling
                else -> return R.drawable.item_bg
            }
        }

        fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
            val r = 6371 // radius of the earth in kilometers
            val dLat = Math.toRadians(lat2 - lat1)
            val dLon = Math.toRadians(lon2 - lon1)
            val a = sin(dLat / 2) * sin(dLat / 2) +
                    cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                    sin(dLon / 2) * sin(dLon / 2)
            val c = 2 * atan2(sqrt(a), sqrt(1 - a))
            return r * c // distance in kilometers
        }
    }


}