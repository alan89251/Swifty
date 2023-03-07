package com.team2.handiwork.viewModel

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.team2.handiwork.R

class LayoutCreateMissionStepperViewModel : ViewModel() {
    var step1Desc: MutableLiveData<Int> = MutableLiveData(R.string.create_mission_step_1)
    var step2Desc: MutableLiveData<Int> = MutableLiveData(R.string.create_mission_step_2)
    var step3Desc: MutableLiveData<Int> = MutableLiveData(R.string.create_mission_step_3)

    var step1Background: MediatorLiveData<Int?> = MediatorLiveData()
    var step1BackgroundTint: MediatorLiveData<Int> = MediatorLiveData()
    var step1ImageSrc: MediatorLiveData<Int> = MediatorLiveData()
    var step2Background: MediatorLiveData<Int?> = MediatorLiveData()
    var step2BackgroundTint: MediatorLiveData<Int> = MediatorLiveData()
    var step2ImageSrc: MediatorLiveData<Int> = MediatorLiveData()
    var step3Background: MediatorLiveData<Int?> = MediatorLiveData()
    var step3BackgroundTint: MediatorLiveData<Int> = MediatorLiveData()
    var step3ImageSrc: MediatorLiveData<Int> = MediatorLiveData()

    var step1Status: MediatorLiveData<StepStatus> = MediatorLiveData()
    var step2Status: MediatorLiveData<StepStatus> = MediatorLiveData()
    var step3Status: MediatorLiveData<StepStatus> = MediatorLiveData()

    var step: MutableLiveData<Int> = MutableLiveData(1)

    init {
        step1Background.addSource(step1Status) {
            step1Background.value = null//getStepBackgroundByStatus(it)
        }
        step2Background.addSource(step2Status) {
            step2Background.value = getStepBackgroundByStatus(it)
        }
        step3Background.addSource(step3Status) {
            step3Background.value = getStepBackgroundByStatus(it)
        }
        step1BackgroundTint.addSource(step1Status) {
            step1BackgroundTint.value = getStepBackgroundTintByStatus(it)
        }
        step2BackgroundTint.addSource(step2Status) {
            step2BackgroundTint.value = getStepBackgroundTintByStatus(it)
        }
        step3BackgroundTint.addSource(step3Status) {
            step3BackgroundTint.value = getStepBackgroundTintByStatus(it)
        }
        step1ImageSrc.addSource(step1Status) {
            step1ImageSrc.value = getStep1ImageSrc(it)
        }
        step2ImageSrc.addSource(step2Status) {
            step2ImageSrc.value = getStep2ImageSrc(it)
        }
        step3ImageSrc.addSource(step3Status) {
            step3ImageSrc.value = getStep3ImageSrc(it)
        }
        step1Status.addSource(step) {
            step1Status.value = getStep1StatusByStep(it)
        }
        step2Status.addSource(step) {
            step2Status.value = getStep2StatusByStep(it)
        }
        step3Status.addSource(step) {
            step3Status.value = getStep3StatusByStep(it)
        }
    }

    private fun getStepBackgroundByStatus(status: StepStatus): Int? {
        return when (status) {
            StepStatus.ACTIVE -> R.drawable.circle_outline
            StepStatus.INACTIVE -> R.drawable.circle_outline
            StepStatus.COMPLETED -> null
        }
    }

    private fun getStepBackgroundTintByStatus(status: StepStatus): Int {
        return when (status) {
            StepStatus.ACTIVE -> R.color.very_soft_blue_100
            StepStatus.INACTIVE -> R.color.very_soft_blue_100
            StepStatus.COMPLETED -> R.color.white_0
        }
    }

    private fun getStep1ImageSrc(status: StepStatus): Int {
        return when (status) {
            StepStatus.ACTIVE -> R.drawable.stepper_active_1
            StepStatus.INACTIVE -> R.drawable.stepper_active_1 // should never hit this case
            StepStatus.COMPLETED -> R.drawable.stepper_completed
        }
    }

    private fun getStep2ImageSrc(status: StepStatus): Int {
        return when (status) {
            StepStatus.ACTIVE -> R.drawable.stepper_active_2
            StepStatus.INACTIVE -> R.drawable.stepper_inactive_2
            StepStatus.COMPLETED -> R.drawable.stepper_completed
        }
    }

    private fun getStep3ImageSrc(status: StepStatus): Int {
        return when (status) {
            StepStatus.ACTIVE -> R.drawable.stepper_active_3
            StepStatus.INACTIVE -> R.drawable.stepper_inactive_3
            StepStatus.COMPLETED -> R.drawable.stepper_completed
        }
    }

    private fun getStep1StatusByStep(step: Int): StepStatus {
        return when (step) {
            1 -> StepStatus.ACTIVE
            2 -> StepStatus.COMPLETED
            else -> StepStatus.COMPLETED
        }
    }

    private fun getStep2StatusByStep(step: Int): StepStatus {
        return when (step) {
            1 -> StepStatus.INACTIVE
            2 -> StepStatus.ACTIVE
            else -> StepStatus.COMPLETED
        }
    }

    private fun getStep3StatusByStep(step: Int): StepStatus {
        return when (step) {
            1 -> StepStatus.INACTIVE
            2 -> StepStatus.INACTIVE
            else -> StepStatus.ACTIVE
        }
    }

    enum class StepStatus {
        ACTIVE,
        INACTIVE,
        COMPLETED
    }
}