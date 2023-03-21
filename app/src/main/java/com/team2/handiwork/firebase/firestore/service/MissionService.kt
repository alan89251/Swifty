package com.team2.handiwork.firebase.firestore.service

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.team2.handiwork.enums.FirebaseCollectionKey
import com.team2.handiwork.enums.MissionStatusEnum
import com.team2.handiwork.enums.TransactionEnum
import com.team2.handiwork.firebase.firestore.repository.MissionCollection
import com.team2.handiwork.firebase.firestore.repository.TransactionCollection
import com.team2.handiwork.firebase.firestore.repository.UserCollection
import com.team2.handiwork.models.Mission
import com.team2.handiwork.models.MissionUser
import com.team2.handiwork.models.Transaction
import com.team2.handiwork.models.User
import com.team2.handiwork.singleton.UserData
import io.reactivex.rxjava3.core.Observable

class MissionService(
    private val userRepo: UserCollection,
    private val missionRepo: MissionCollection,
    private val tranRepo: TransactionCollection,
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
    fun selectAgent(
        mission: Mission,
        email: String,
        onSuccess: ((Mission) -> Unit)? = null,
        onError: ((Exception) -> Unit)? = null
    ) {
        mission.status = MissionStatusEnum.CONFIRMED
        mission.selectedAgent = email
        missionRepo.updateMission(mission, onSuccess, onError)
    }

    /**
     * For Employer dispute mission -> change status from PENDING_ACCEPTANCE to DISPUTED
     * 1. change mission status from PENDING_ACCEPTANCE to DISPUTED
     * ** no transaction record
     * */
    fun disputeMission(
        mission: Mission,
        onSuccess: ((Mission) -> Unit)? = null,
        onError: ((Exception) -> Unit)? = null
    ) {
        mission.status = MissionStatusEnum.DISPUTED
        missionRepo.updateMission(mission, onSuccess, onError)
    }

    fun rejectMission(
        mission: Mission,
        onSuccess: ((Mission) -> Unit)? = null,
        onError: ((Exception) -> Unit)? = null
    ) {
        mission.status = MissionStatusEnum.DISPUTED
        missionRepo.updateMission(mission, onSuccess, onError)
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
        mission: Mission, employer: User, onSuccess: ((MissionUser) -> Unit)? = null
    ) {
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
        onSuccess?.invoke(MissionUser(mission, employer))
        // todo add transaction record
    }

    /**
     * For Employer cancel mission before 48 hours -> change status from CONFIRMED to CANCEL
     * 1. change mission status from CONFIRMED to CANCEL
     * 2. release Employer onHold ( todo charge penalty )
     * 3. increase Employer balance  ( todo confirm transaction record )
     * */
    fun cancelMissionBefore48HoursByEmployer(
        mission: Mission, employer: User, onSuccess: ((MissionUser) -> Unit)? = null
    ) {
        val batch = fs.batch()

        mission.status = MissionStatusEnum.CANCELLED
        batch.set(missionRepo.collection.document(mission.missionId), mission.serialize())
        employer.confirmedCancellationCount += 1

        batch.update(
            userRepo.collection.document(employer.email), hashMapOf<String, Int>(
                "confirmedCancellationCount" to employer.confirmedCancellationCount,
            ) as Map<String, Any>
        )
        batch.commit()
        onSuccess?.invoke(MissionUser(mission, employer))
        // todo add transaction record
    }

    /**
     * For Employer cancel mission with 48 hours -> change status from CONFIRMED to CANCEL
     * 1. change mission status from CONFIRMED to CANCEL
     * 2. deducted 100% of mission price on agent onHold
     * 3. add transaction record
     * */
    fun cancelMissionWithin48HoursByEmployer(
        mission: Mission, employer: User, onSuccess: ((MissionUser) -> Unit)? = null
    ) {
        val batch = fs.batch()
        val id = System.currentTimeMillis().toString()

        mission.status = MissionStatusEnum.CANCELLED
        batch.set(missionRepo.collection.document(mission.missionId), mission.serialize())
        employer.confirmedCancellationCount += 1

        employer.onHold = (employer.onHold - mission.price).toInt()
        batch.update(
            userRepo.collection.document(employer.email), hashMapOf<String, Int>(
                "onHold" to employer.onHold,
                "confirmedCancellationCount" to employer.confirmedCancellationCount,
            ) as Map<String, Any>
        )

        val transaction = Transaction()

        transaction.amount = mission.price.toInt()
        transaction.title = "${mission.serviceType} Withdrawal"
        transaction.firstName = employer.firstName
        transaction.lastName = employer.lastName
        transaction.type = TransactionEnum.WITHDRAW

        val transDoc = userRepo
            .collection
            .document(employer.email)
            .collection(FirebaseCollectionKey.TRANSACTIONS.displayName)
            .document(id)

        batch.set(transDoc, transaction.serialize())

        batch.commit()
        onSuccess?.invoke(MissionUser(mission, employer))
    }

    /**
     * For Agent cancel mission before 48 hours -> change status from CONFIRMED to CANCEL
     * 1. change mission status from CONFIRMED to CANCEL
     * 2. no amount deduct on Agent balance
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

            batch.set(missionRepo.collection.document(mission.missionId), mission.serialize())

            batch.update(
                userRepo.collection.document(agent.email), hashMapOf<String, Int>(
                    "confirmedCancellationCount" to agent.confirmedCancellationCount,
                ) as Map<String, Any>
            )

            observer.onNext(MissionUser(mission, agent))
        }
    }

    /**
     * For Agent cancel mission within 48 hours -> change status from CONFIRMED to CANCEL
     * 1. change mission status from CONFIRMED to CANCEL
     * 2. deducted Agent balance by 50% of mission price
     * 3. add transaction record
     * */
    fun cancelMissionWithin48HoursByAgent(
        mission: Mission,
        agent: User,
    ): Observable<MissionUser> {
        return Observable.create { observer ->
            val batch = fs.batch()
            val id = System.currentTimeMillis().toString()

            mission.status = MissionStatusEnum.OPEN
            mission.selectedAgent = ""
            agent.confirmedCancellationCount += 1

            val penalty = mission.price / 2
            agent.balance = (agent.balance - penalty).toInt()

            mission.enrollments.remove(agent.email)
            batch.set(missionRepo.collection.document(mission.missionId), mission.serialize())

            batch.update(
                userRepo.collection.document(agent.email), hashMapOf<String, Int>(
                    "balance" to agent.balance,
                    "confirmedCancellationCount" to agent.confirmedCancellationCount,
                ) as Map<String, Any>
            )

            val transaction = Transaction()

            transaction.amount = penalty.toInt()
            transaction.title = "${mission.serviceType} Withdrawal"
            transaction.firstName = agent.firstName
            transaction.lastName = agent.lastName
            transaction.type = TransactionEnum.WITHDRAW

            val transDoc = userRepo
                .collection
                .document(agent.email)
                .collection(FirebaseCollectionKey.TRANSACTIONS.displayName)
                .document(id)

            batch.set(transDoc, transaction.serialize())

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
        mission: Mission,
        onSuccess: ((Mission) -> Unit)? = null,
        onError: ((Exception) -> Unit)? = null
    ) {
        mission.status = MissionStatusEnum.COMPLETED
        missionRepo.updateMission(mission, onSuccess, onError)
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