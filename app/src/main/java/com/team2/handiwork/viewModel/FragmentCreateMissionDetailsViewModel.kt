package com.team2.handiwork.viewModel

import android.graphics.Bitmap
import android.net.Uri
import android.view.View
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
    val location: MutableLiveData<String> = MutableLiveData("")
    val imageUriList: MutableLiveData<ArrayList<Uri>> = MutableLiveData()
    val isShowStartTimeErrMsg: MediatorLiveData<Int> = MediatorLiveData()
    val isShowEndTimeErrMsg: MediatorLiveData<Int> = MediatorLiveData()
    val isShowLocationErrMsg: MediatorLiveData<Int> = MediatorLiveData()
    val isEnableBtnNext: MediatorLiveData<Boolean> = MediatorLiveData()

    init {
        startDateTimeStr.addSource(startDateTime) {
            val dateFormatter = SimpleDateFormat("MM/dd/yyyy\nHH:mm")
            startDateTimeStr.value = dateFormatter.format(it.time)
        }

        endDateTimeStr.addSource(endDateTime) {
            val dateFormatter = SimpleDateFormat("MM/dd/yyyy\nHH:mm")
            endDateTimeStr.value = dateFormatter.format(it.time)
        }
        isShowStartTimeErrMsg.addSource(startDateTime) {
            isShowStartTimeErrMsg.value =
                if (isStartTimeValid()) View.INVISIBLE else View.VISIBLE
        }
        isShowEndTimeErrMsg.addSource(endDateTime) {
            isShowEndTimeErrMsg.value =
                if (isEndTimeValid()) View.INVISIBLE else View.VISIBLE
        }
        isShowEndTimeErrMsg.addSource(startDateTime) {
            isShowEndTimeErrMsg.value =
                if (isEndTimeValid()) View.INVISIBLE else View.VISIBLE
        }
        isShowLocationErrMsg.addSource(location) {
            isShowLocationErrMsg.value =
                if (isLocationValid()) View.INVISIBLE else View.VISIBLE
        }
        isEnableBtnNext.addSource(startDateTime) {
            isEnableBtnNext.value = isAllInputsValid()
        }
        isEnableBtnNext.addSource(endDateTime) {
            isEnableBtnNext.value = isAllInputsValid()
        }
        isEnableBtnNext.addSource(location) {
            isEnableBtnNext.value = isAllInputsValid()
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

    fun isAllInputsValid(): Boolean {
        return isStartTimeValid() && isEndTimeValid() && isLocationValid()
    }

    fun isStartTimeValid(): Boolean {
        val curDate = Calendar.getInstance()
        return startDateTime.value != null
                && startDateTime.value!!.after(curDate)
    }

    fun isEndTimeValid(): Boolean {
        val curDate = Calendar.getInstance()
        return endDateTime.value != null
                && endDateTime.value!!.after(curDate)
                && if (startDateTime.value != null)
                    endDateTime.value!!.after(startDateTime.value!!)
                    else true
    }

    fun isLocationValid(): Boolean {
        return location.value != null
                && location.value!! != ""
                && location.value!!.trim() != ""
    }
}