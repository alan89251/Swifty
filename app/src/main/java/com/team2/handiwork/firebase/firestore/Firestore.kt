package com.team2.handiwork.firebase.firestore

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.team2.handiwork.firebase.firestore.collection.Enrollment
import com.team2.handiwork.firebase.firestore.collection.Mission
import com.team2.handiwork.firebase.firestore.collection.Transaction
import com.team2.handiwork.firebase.firestore.collection.User

// todo temp interface
class Firestore {
    var instance = Firebase.firestore
    var userCollection = User()
    var missionCollection = Mission()
    var transactionCollection = Transaction()
    var enrollmentCollection = Enrollment()

}
