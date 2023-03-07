package com.team2.handiwork.firebase.firestore.service

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.team2.handiwork.enums.MissionStatusEnum
import com.team2.handiwork.firebase.firestore.repository.MissionCollection
import com.team2.handiwork.firebase.firestore.repository.UserCollection
import com.team2.handiwork.models.Mission
import com.team2.handiwork.models.User
import io.reactivex.rxjava3.core.Observable

class MissionService(
    private val userRepo: UserCollection,
    private val missionRepo: MissionCollection,
) {
    var fs = Firebase.firestore

    /**
     * For Employer create an new mission
     * 1. create mission
     * 2. deducted employer wallet balance amount by mission amount
     * 3. increase employer wallet onHold amount by mission amount
     * ** no transaction record
     * */
    fun createMission(mission: Mission, employer: User): Observable<Mission> {
        val batch = fs.batch()
        val id = System.currentTimeMillis().toString()

        mission.status = MissionStatusEnum.OPEN.value
        batch.set(missionRepo.collection.document(id), mission)
        employer.balance = (employer.balance - mission.price).toInt()
        employer.onHold = (employer.onHold + mission.price).toInt()

        batch.update(
            userRepo.collection.document(employer.email),
            hashMapOf<String, Int>(
                "balance" to employer.balance,
                "onHold" to employer.onHold
            ) as Map<String, Any>
        )
        return Observable.create<Mission> { observer ->
            batch.commit()
                .addOnSuccessListener {
                    mission.missionId = id
                    observer.onNext(mission)
                    Log.d("Open Mission: ", "Success")
                }.addOnFailureListener {
                    observer.onError(it)
                    Log.d("Open Mission: ", "Fail $it")
                }
        }
    }

    /**
     * For Agent complete mission -> change status from CONFIRMED to PENDING_ACCEPTANCE
     * 1. change mission status from CONFIRMED to PENDING_ACCEPTANCE
     * ** no transaction record
     * */
    fun finishedMission(mission: Mission) {
        mission.status = MissionStatusEnum.PENDING_ACCEPTANCE.value
        missionRepo.updateMission(mission)
    }

    /**
     * For Employer select mission -> change status from OPEN to CONFIRMED
     * 1. change mission status from OPEN to CONFIRMED
     * ** no transaction record
     * */
    fun selectAgent(mission: Mission, email: String) {
        mission.status = MissionStatusEnum.CONFIRMED.value
        mission.selectedAgent = email
        missionRepo.updateMission(mission)
    }

    /**
     * For Employer dispute mission -> change status from PENDING_ACCEPTANCE to DISPUTED
     * 1. change mission status from PENDING_ACCEPTANCE to DISPUTED
     * ** no transaction record
     * */
    fun disputeMission(mission: Mission) {
        mission.status = MissionStatusEnum.DISPUTED.value
        missionRepo.updateMission(mission)
    }

    /**
     * For Agent revoke mission -> Agent can enroll mission or revoke mission before CONFIRM status
     * 1. remove agent email from mission enrollments
     * ** no transaction record
     * */
    fun revokeMission(mission: Mission, agentEmail: String) {
        mission.enrollments.remove(agentEmail)
        missionRepo.updateMission(mission)
    }

    /**
     * For Employer cancel mission before 48 hours -> change status from CONFIRMED to CANCEL
     * 1. change mission status from CONFIRMED to CANCEL
     * 2. release Employer onHold ( todo charge penalty )
     * 3. increase Employer balance  ( todo confirm transaction record )
     * */
    fun cancelMissionBefore48HoursByEmployer(
        mission: Mission,
        employer: User,
    ) {
        val batch = fs.batch()

        mission.status = MissionStatusEnum.CANCELLED.value
        batch.set(missionRepo.collection.document(mission.missionId), mission)

        employer.balance = (employer.balance + mission.price).toInt()
        employer.onHold = (employer.onHold - mission.price).toInt()
        employer.confirmedCancellationCount += 1

        batch.update(
            userRepo.collection.document(employer.email),
            hashMapOf<String, Int>(
                "balance" to employer.balance,
                "onHold" to employer.onHold,
                "confirmedCancellationCount" to employer.confirmedCancellationCount,
            ) as Map<String, Any>
        )

        batch.commit()

        // todo add transaction record
    }

    /**
     * For Employer cancel mission with 48 hours -> change status from CONFIRMED to CANCEL
     * 1. change mission status from CONFIRMED to CANCEL
     * 2. release Employer onHold ( todo charge penalty )
     * 3. increase Employer balance  ( todo confirm transaction record )
     * */
    fun cancelMissionWithin48HoursByEmployer(
        mission: Mission,
        employer: User,
    ) {
        val batch = fs.batch()

        mission.status = MissionStatusEnum.CANCELLED.value
        batch.set(missionRepo.collection.document(mission.missionId), mission)
        employer.confirmedCancellationCount += 1

        employer.balance = (employer.balance + mission.price).toInt()
        employer.onHold = (employer.onHold - mission.price).toInt()

        batch.update(
            userRepo.collection.document(employer.email),
            hashMapOf<String, Int>(
                "balance" to employer.balance,
                "onHold" to employer.onHold,
                "confirmedCancellationCount" to employer.confirmedCancellationCount,
            ) as Map<String, Any>
        )
        batch.commit()

        // todo add transaction record
    }

    /**
     * For Agent cancel mission before 48 hours -> change status from CONFIRMED to CANCEL
     * 1. change mission status from CONFIRMED to CANCEL
     * 2. deducted Agent balance  ( todo confirm transaction record )
     * */
    fun cancelMissionBefore48HoursByAgent(
        mission: Mission,
        agent: User,
    ) {
        val batch = fs.batch()

        mission.status = MissionStatusEnum.OPEN.value
        agent.confirmedCancellationCount += 1
        agent.balance = (agent.balance - mission.price).toInt()

        batch.set(missionRepo.collection.document(mission.missionId), mission)

        batch.update(
            userRepo.collection.document(agent.email),
            hashMapOf<String, Int>(
                "balance" to agent.balance,
                "confirmedCancellationCount" to agent.confirmedCancellationCount,
            ) as Map<String, Any>
        )

        // todo add transaction record
    }

    /**
     * For Agent cancel mission within 48 hours -> change status from CONFIRMED to CANCEL
     * 1. change mission status from CONFIRMED to CANCEL
     * 2. deducted Agent balance  ( todo confirm transaction record )
     * */
    fun cancelMissionWithin48HoursByAgent(
        mission: Mission,
        agent: User,
    ) {
        val batch = fs.batch()

        mission.status = MissionStatusEnum.CANCELLED.value
        agent.confirmedCancellationCount += 1
        agent.balance = (agent.balance - mission.price).toInt()

        batch.set(missionRepo.collection.document(mission.missionId), mission)

        batch.update(
            userRepo.collection.document(agent.email),
            hashMapOf<String, Int>(
                "balance" to agent.balance,
                "confirmedCancellationCount" to agent.confirmedCancellationCount,
            ) as Map<String, Any>
        )
        batch.commit()

        // todo add transaction record
    }

    /**
     * For Employer complete mission -> change status from PENDING_ACCEPTANCE to COMPLETE
     * 1. change mission status from PENDING_ACCEPTANCE to COMPLETE
     * ** user balance and transaction handled by backend scheduler
     * */
    fun completeMission(
        mission: Mission
    ) {
        mission.status = MissionStatusEnum.COMPLETED.value
        missionRepo.updateMission(mission)
    }

    /**
     * For Agent enrolled mission -> XX going change status
     * 1. add agent email to Mission enrollment list
     * ** no transaction
     * */
    fun enrolledMission(
        mission: Mission,
        agentEmail: String,
    ) {
        mission.enrollments.add(agentEmail)
        missionRepo.updateMission(mission)
    }
}