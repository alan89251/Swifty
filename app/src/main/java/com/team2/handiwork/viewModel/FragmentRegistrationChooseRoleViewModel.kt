package com.team2.handiwork.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FragmentRegistrationChooseRoleViewModel : ViewModel() {
    var isAgent = MutableLiveData<Boolean>(false)
    var isEmployer = MutableLiveData<Boolean>(false)
}