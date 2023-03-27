package com.team2.handiwork.viewModel.profile

import com.team2.handiwork.firebase.firestore.Firestore
import com.team2.handiwork.firebase.firestore.repository.UserCollection
import com.team2.handiwork.models.ServiceType
import com.team2.handiwork.singleton.UserData

class FragmentMyProfileViewModel : FragmentBaseProfileViewModel() {
    fun cancelDistanceSubscription() {
        UserData.currentUserData.distance = 0

        // update DB
        Firestore()
            .userCollection
            .updateUser(UserData.currentUserData, {})
    }

    fun cancelServiceTypeSubscription() {
        UserData.currentUserData.serviceTypeList = ArrayList()

        // update DB
        Firestore()
            .userCollection
            .updateUser(UserData.currentUserData, {})
    }
}