package com.team2.handiwork.firebase.firestore

import com.team2.handiwork.firebase.firestore.repository.*

// todo temp interface
class Firestore {
    var userCollection = UserCollection()
    var missionCollection = MissionCollection()
    var transactionCollection = TransactionCollection()
    var enrollmentCollection = EnrollmentCollection()
    var commentCollection = CommentCollection()
}
