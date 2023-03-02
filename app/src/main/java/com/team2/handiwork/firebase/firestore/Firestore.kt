package com.team2.handiwork.firebase.firestore

import com.team2.handiwork.firebase.firestore.repository.EnrollmentCollection
import com.team2.handiwork.firebase.firestore.repository.MissionCollection
import com.team2.handiwork.firebase.firestore.repository.TransactionCollection
import com.team2.handiwork.firebase.firestore.repository.UserCollection

// todo temp interface
class Firestore {
    var userCollection = UserCollection()
    var missionCollection = MissionCollection()
    var transactionCollection = TransactionCollection()
    var enrollmentCollection = EnrollmentCollection()
}
