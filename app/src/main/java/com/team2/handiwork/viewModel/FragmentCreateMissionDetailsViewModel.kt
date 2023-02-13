package com.team2.handiwork.viewModel

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.team2.handiwork.models.Mission
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class FragmentCreateMissionDetailsViewModel {
    var mission: Mission = Mission()
    val startDateTime: MutableLiveData<Calendar> = MutableLiveData(Calendar.getInstance())
    val startDateTimeStr: MediatorLiveData<String> = MediatorLiveData()
    val endDateTime: MutableLiveData<Calendar> = MutableLiveData(Calendar.getInstance())
    val endDateTimeStr: MediatorLiveData<String> = MediatorLiveData()
    val imageUriList: MutableLiveData<ArrayList<Uri>> = MutableLiveData()

    init {
        startDateTimeStr.addSource(startDateTime) {
            val dateFormatter = SimpleDateFormat("MM/dd/yyyy\nHH:mm")
            startDateTimeStr.value = dateFormatter.format(it.time)
        }

        endDateTimeStr.addSource(endDateTime) {
            val dateFormatter = SimpleDateFormat("MM/dd/yyyy\nHH:mm")
            endDateTimeStr.value = dateFormatter.format(it.time)
        }
    }

    fun setStartDate(year: Int, month: Int, day: Int) {
        val calendar = startDateTime.value!!
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, day)
        startDateTime.value = calendar
    }

    fun setStartTime(hour: Int, minute: Int) {
        val calendar = startDateTime.value!!
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        startDateTime.value = calendar
    }

    fun setEndDate(year: Int, month: Int, day: Int) {
        val calendar = endDateTime.value!!
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, day)
        endDateTime.value = calendar
    }

    fun setEndTime(hour: Int, minute: Int) {
        val calendar = endDateTime.value!!
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        endDateTime.value = calendar
    }

    fun setMissionPhotos(missionPhotos: ArrayList<Bitmap>) {
        for (photo in missionPhotos) {
            val outputStream = ByteArrayOutputStream()
            photo.compress(Bitmap.CompressFormat.JPEG, 98, outputStream)
            val byteArray = outputStream.toByteArray()
            mission.missionPhotos.add(
                Base64.getEncoder().encodeToString(byteArray)
            )
        }
    }
}