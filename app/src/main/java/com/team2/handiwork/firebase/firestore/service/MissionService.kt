package com.team2.handiwork.firebase.firestore.service

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.team2.handiwork.enums.MissionStatusEnum
import com.team2.handiwork.firebase.firestore.repository.MissionCollection
import com.team2.handiwork.firebase.firestore.repository.UserCollection
import com.team2.handiwork.models.Mission
import com.team2.handiwork.models.MissionUser
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
    fun createMission(mission: Mission, employer: User): Observable<MissionUser> {
        val batch = fs.batch()
        val id = System.currentTimeMillis().toString()

        mission.status = MissionStatusEnum.OPEN
        batch.set(missionRepo.collection.document(id), mission.serialize())
        employer.balance = (employer.balance - mission.price).toInt()
        employer.onHold = (employer.onHold + mission.price).toInt()

        batch.update(
            userRepo.collection.document(employer.email), hashMapOf<String, Int>(
                "balance" to employer.balance, "onHold" to employer.onHold
            ) as Map<String, Any>
        )
        return Observable.create<MissionUser> { observer ->
            batch.commit().addOnSuccessListener {
                mission.missionId = id
                observer.onNext(MissionUser(mission, employer))
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
    fun finishedMission(mission: Mission): Observable<Mission> {
        return Observable.create<Mission> { observer ->
            mission.status = MissionStatusEnum.PENDING_ACCEPTANCE
            missionRepo.updateMission(mission)
            observer.onNext(mission)
        }
    }

    /**
     * For Employer select mission -> change status from OPEN to CONFIRMED
     * 1. change mission status from OPEN to CONFIRMED
     * ** no transaction record
     * */
    fun selectAgent(mission: Mission, email: String): Observable<Mission> {
        return Observable.create<Mission> { observer ->
            mission.status = MissionStatusEnum.CONFIRMED
            mission.selectedAgent = email
            missionRepo.updateMission(mission)
            observer.onNext(mission)
        }
    }

    /**
     * For Employer dispute mission -> change status from PENDING_ACCEPTANCE to DISPUTED
     * 1. change mission status from PENDING_ACCEPTANCE to DISPUTED
     * ** no transaction record
     * */
    fun disputeMission(mission: Mission): Observable<Mission> {
        return Observable.create { observer ->
            mission.status = MissionStatusEnum.DISPUTED
            missionRepo.updateMission(mission)
            observer.onNext(mission)

        }
    }

    fun rejectMission(mission: Mission): Observable<Mission> {
        return Observable.create { observer ->
            mission.status = MissionStatusEnum.DISPUTED
            missionRepo.updateMission(mission)
            observer.onNext(mission)

        }
    }

    /**
     * For Agent revoke mission -> Agent can enroll mission or revoke mission before CONFIRM status
     * 1. remove agent email from mission enrollments
     * ** no transaction record
     * */
    fun revokeMission(mission: Mission, agentEmail: String): Observable<Mission> {
        return Observable.create { observer ->
            mission.enrollments.remove(agentEmail)
            missionRepo.updateMission(mission)
            observer.onNext(mission)
        }
    }

    fun cancelOpenMissionByEmployer(
        mission: Mission,
        employer: User,
    ): Observable<MissionUser> {
        return Observable.create { observer ->
            val batch = fs.batch()

            mission.status = MissionStatusEnum.CANCELLED
            batch.set(missionRepo.collection.document(mission.missionId), mission.serialize())

            employer.balance = (employer.balance + mission.price).toInt()
            employer.onHold = (employer.onHold - mission.price).toInt()

            batch.update(
                userRepo.collection.document(employer.email), hashMapOf<String, Int>(
                    "balance" to employer.balance,
                    "onHold" to employer.onHold,
                ) as Map<String, Any>
            )
            batch.commit()
            observer.onNext(MissionUser(mission, employer))
        }
        // todo add transaction record
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
    ): Observable<MissionUser> {
        return Observable.create { observer ->
            val batch = fs.batch()

            mission.status = MissionStatusEnum.CANCELLED
            batch.set(missionRepo.collection.document(mission.missionId), mission.serialize())

            employer.balance = (employer.balance + mission.price).toInt()
            employer.onHold = (employer.onHold - mission.price).toInt()
            employer.confirmedCancellationCount += 1

            batch.update(
                userRepo.collection.document(employer.email), hashMapOf<String, Int>(
                    "balance" to employer.balance,
                    "onHold" to employer.onHold,
                    "confirmedCancellationCount" to employer.confirmedCancellationCount,
                ) as Map<String, Any>
            )
            batch.commit()
            observer.onNext(MissionUser(mission, employer))

        }
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
    ): Observable<MissionUser> {
        return Observable.create { observer ->
            val batch = fs.batch()

            mission.status = MissionStatusEnum.CANCELLED
            batch.set(missionRepo.collection.document(mission.missionId), mission.serialize())
            employer.confirmedCancellationCount += 1

            employer.balance = (employer.balance + mission.price).toInt()
            employer.onHold = (employer.onHold - mission.price).toInt()

            batch.update(
                userRepo.collection.document(employer.email), hashMapOf<String, Int>(
                    "balance" to employer.balance,
                    "onHold" to employer.onHold,
                    "confirmedCancellationCount" to employer.confirmedCancellationCount,
                ) as Map<String, Any>
            )
            batch.commit()
            observer.onNext(MissionUser(mission, employer))
            // todo add transaction record
        }
    }

    /**
     * For Agent cancel mission before 48 hours -> change status from CONFIRMED to CANCEL
     * 1. change mission status from CONFIRMED to CANCEL
     * 2. deducted Agent balance  ( todo confirm transaction record )
     * */
    fun cancelMissionBefore48HoursByAgent(
        mission: Mission,
        agent: User,
    ): Observable<MissionUser> {
        return Observable.create { observer ->

            val batch = fs.batch()

            mission.status = MissionStatusEnum.OPEN
            mission.enrollments.remove(agent.email)
            mission.selectedAgent = ""
            agent.confirmedCancellationCount += 1
            agent.balance = (agent.balance - mission.price).toInt()

            batch.set(missionRepo.collection.document(mission.missionId), mission.serialize())

            batch.update(
                userRepo.collection.document(agent.email), hashMapOf<String, Int>(
                    "balance" to agent.balance,
                    "confirmedCancellationCount" to agent.confirmedCancellationCount,
                ) as Map<String, Any>
            )

            // todo add transaction record
            observer.onNext(MissionUser(mission, agent))
        }
    }

    /**
     * For Agent cancel mission within 48 hours -> change status from CONFIRMED to CANCEL
     * 1. change mission status from CONFIRMED to CANCEL
     * 2. deducted Agent balance  ( todo confirm transaction record )
     * */
    fun cancelMissionWithin48HoursByAgent(
        mission: Mission,
        agent: User,
    ): Observable<MissionUser> {
        return Observable.create { observer ->
            val batch = fs.batch()

            mission.status = MissionStatusEnum.OPEN
            mission.selectedAgent = ""
            agent.confirmedCancellationCount += 1
            agent.balance = (agent.balance - mission.price).toInt()
            mission.enrollments.remove(agent.email)
            batch.set(missionRepo.collection.document(mission.missionId), mission.serialize())

            batch.update(
                userRepo.collection.document(agent.email), hashMapOf<String, Int>(
                    "balance" to agent.balance,
                    "confirmedCancellationCount" to agent.confirmedCancellationCount,
                ) as Map<String, Any>
            )
            // todo add transaction record

            batch.commit()
            observer.onNext(MissionUser(mission, agent))
        }
    }

    /**
     * For Employer complete mission -> change status from PENDING_ACCEPTANCE to COMPLETE
     * 1. change mission status from PENDING_ACCEPTANCE to COMPLETE
     * ** user balance and transaction handled by backend scheduler
     * */
    fun completeMission(
        mission: Mission
    ): Observable<Mission> {
        return Observable.create { observer ->
            mission.status = MissionStatusEnum.COMPLETED
            missionRepo.updateMission(mission)
            observer.onNext(mission)
        }
    }

    /**
     * For Agent enrolled mission -> XX going change status
     * 1. add agent email to Mission enrollment list
     * ** no transaction
     * */
    fun enrolledMission(
        mission: Mission,
        agentEmail: String,
    ): Observable<Mission> {
        return Observable.create<Mission> { observer ->
            mission.enrollments.add(agentEmail)
            missionRepo.updateMission(mission)
            observer.onNext(mission)
        }
    }
}