package com.team2.handiwork.viewModel

import android.location.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.GroundOverlay

class FragmentRegistrationWorkerProfileViewModel : ViewModel() {
    var workerLocationMap: MutableLiveData<GoogleMap> = MutableLiveData()
    var deviceLocation: MutableLiveData<Location> = MutableLiveData()
    var workerPreferredMissionDistance: MutableLiveData<Int> = MutableLiveData()
    var workerPreferredMissionCircle: MutableLiveData<Circle> = MutableLiveData()
    var locationIndicator: MutableLiveData<GroundOverlay> = MutableLiveData()
}