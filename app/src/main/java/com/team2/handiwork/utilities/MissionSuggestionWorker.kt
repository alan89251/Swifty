package com.team2.handiwork.utilities

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.preference.PreferenceManager
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.team2.handiwork.AppConst
import com.team2.handiwork.services.MissionNotificationHelper
import com.team2.handiwork.services.api.MissionSuggestionCountApi
import com.team2.handiwork.services.api.RetrofitHelper
import retrofit2.create

class MissionSuggestionWorker(context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

    private val apiService = RetrofitHelper.getInstance()
    private val _context = context

    @RequiresApi(Build.VERSION_CODES.S)
    override suspend fun doWork(): Result {
        val suggestionCountApi = apiService.create(MissionSuggestionCountApi::class.java)
        try {
            val sp = PreferenceManager.getDefaultSharedPreferences(applicationContext)
            val email = sp.getString(AppConst.EMAIL, "")
            if (email != "") {
                val response = suggestionCountApi.getSuggestionCount(email!!)
                response.isSuccessful.let {
                    val countResponse = response.body()
                    countResponse?.missionCount.let { newCount ->
                        val oldCount = sp.getInt(AppConst.PREF_SUGGESTED_MISSION_COUNT, 0)
                        if (newCount!! > oldCount) {
                            MissionNotificationHelper(_context).sendMissionNotification(newCount - oldCount)
                            val editor = sp.edit()
                            editor.putInt(AppConst.PREF_SUGGESTED_MISSION_COUNT, newCount)
                            editor.apply()
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.d("hehehe", "doWork: ${e.message}")
            Result.failure()
        }

        return Result.success()
    }
}