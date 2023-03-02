package com.team2.handiwork.firebase.firestore.service

import android.util.Log
import com.team2.handiwork.firebase.firestore.Firestore
import com.team2.handiwork.models.Enrollment
import com.team2.handiwork.models.Mission
import com.team2.handiwork.models.Transaction
import io.reactivex.rxjava3.core.Observable

class MissionService {
    var fs = Firestore()

    fun submitEnrollmentToMission(enrollment: Enrollment, mission: Mission): Observable<Boolean> {
        return Observable.create { observer ->
            fs.instance.runTransaction {
                // add enrollment
                fs.enrollmentCollection.addEnrollment(enrollment)

                // update Mission enrollment list
                fs.missionCollection.updateMission(mission).subscribe()
            }.addOnSuccessListener {
                observer.onNext(true)
                Log.d("submitPurposeToMission", "submit enrollment successfully")
            }.addOnFailureListener { e ->
                observer.onNext(false)
                Log.w("submitPurposeToMission", "submit enrollment failure", e)
            }
        }
    }

    fun withdrawMission(
        enrollment: Enrollment,
        mission: Mission,
        balance: Int,
        transaction: Transaction
    ): Observable<Boolean> {
        return Observable.create { observer ->
            fs.instance.runTransaction {
                // withdraw enrollment
                fs.enrollmentCollection.updateEnrollment(enrollment)

                // update Mission enrollment list
                fs.missionCollection.updateMission(mission).subscribe()

                // update balance
                fs.userCollection.updateUserBalance(enrollment.agent, balance, transaction)
            }.addOnSuccessListener {
                observer.onNext(true)
                Log.d("submitPurposeToMission", "withdraw mission successfully")
            }.addOnFailureListener { e ->
                observer.onNext(false)
                Log.w("submitPurposeToMission", "withdraw mission failure", e)
            }
        }
    }

    fun finishedMission(mission: Mission): Observable<Boolean> {
        return Observable.create { observer ->
            fs.instance.runTransaction {
                // update Mission enrollment list
                fs.missionCollection.updateMission(mission).subscribe()
            }.addOnSuccessListener {
                observer.onNext(true)
                Log.d("submitPurposeToMission", "finished mission successfully")
            }.addOnFailureListener { e ->
                observer.onNext(false)
                Log.w("submitPurposeToMission", "finished mission failure", e)
            }
        }
    }
}