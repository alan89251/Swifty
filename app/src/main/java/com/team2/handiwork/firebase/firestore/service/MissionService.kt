package com.team2.handiwork.firebase.firestore.service

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.team2.handiwork.firebase.firestore.repository.EnrollmentCollection
import com.team2.handiwork.firebase.firestore.repository.MissionCollection
import com.team2.handiwork.firebase.firestore.repository.TransactionCollection
import com.team2.handiwork.firebase.firestore.repository.UserCollection
import com.team2.handiwork.models.Enrollment
import com.team2.handiwork.models.Mission
import com.team2.handiwork.models.Transaction
import io.reactivex.rxjava3.core.Observable

class MissionService(
    private val userRepo: UserCollection,
    private val missionRepo: MissionCollection,
    private val enrollmentRepo: EnrollmentCollection,
    private val transactionRepo: TransactionCollection
) {
    var fs = Firebase.firestore

    fun submitEnrollmentToMission(enrollment: Enrollment, mission: Mission): Observable<Boolean> {
        return Observable.create { observer ->
            fs.runTransaction {
                // add enrollment
                enrollmentRepo.addEnrollment(enrollment)

                // update Mission enrollment list
                missionRepo.updateMission(mission).subscribe()

//                transactionRepo

            }.addOnSuccessListener {
                observer.onNext(true)
                Log.d("submitPurposeToMission", "submit enrollment successfully")
            }.addOnFailureListener { e ->
                observer.onNext(false)
                Log.w("submitPurposeToMission", "submit enrollment failure", e)
            }
        }
    }

    fun revokeMission(mission: Mission, email: String) {
        val missionId = mission.missionId
        fs.runTransaction {
            // revoke mission
            enrollmentRepo.deleteEnrollment(missionId, email)

            // update revoke enrollment list
            missionRepo.updateMission(mission).subscribe()
        }.addOnSuccessListener {
            Log.d("revokeMission", "revoke mission successfully")
        }.addOnFailureListener { e ->
            Log.w("revokeMission", "revoke mission failure", e)
        }
    }

    fun withdrawMission(
        enrollment: Enrollment,
        mission: Mission,
        balance: Int,
        transaction: Transaction
    ): Observable<Boolean> {
        return Observable.create { observer ->
            fs.runTransaction {
                // withdraw enrollment
                enrollmentRepo.deleteEnrollment(enrollment.missionId, enrollment.agent)

                // update Mission enrollment list
                missionRepo.updateMission(mission).subscribe()

                // update balance
                userRepo.updateUserBalance(enrollment.agent, balance, transaction)
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
            fs.runTransaction {
                // update Mission enrollment list
                missionRepo.updateMission(mission).subscribe()
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