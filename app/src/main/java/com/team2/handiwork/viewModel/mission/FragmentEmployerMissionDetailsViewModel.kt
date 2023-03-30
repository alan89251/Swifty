package com.team2.handiwork.viewModel.mission

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.util.Log
import android.view.View
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.team2.handiwork.R
import com.team2.handiwork.adapter.Agent1RecyclerViewAdapter
import com.team2.handiwork.firebase.firestore.Firestore
import com.team2.handiwork.firebase.firestore.repository.CommentCollection
import com.team2.handiwork.firebase.firestore.service.MissionService
import com.team2.handiwork.models.Comment
import com.team2.handiwork.models.Mission
import com.team2.handiwork.models.MissionUser
import com.team2.handiwork.models.User
import com.team2.handiwork.singleton.UserData
import io.reactivex.rxjava3.core.Observable
import java.text.SimpleDateFormat
import java.util.*

class FragmentEmployerMissionDetailsViewModel : ViewModel() {
    val mission: MutableLiveData<Mission> = MutableLiveData()
    val missionCredit = MediatorLiveData<String>()
    var missionDuration: String = ""
        get() {
            val dateFormatter = SimpleDateFormat("MM/dd/yyyy HH:mm")
            return dateFormatter.format(Date(mission.value!!.startTime)) +
                    " - " +
                    dateFormatter.format(Date(mission.value!!.endTime))
        }
    var selectedAgent: MutableLiveData<User> = MutableLiveData()
    var isAgentReviewed: MutableLiveData<Boolean> = MutableLiveData()
    var iconImageUrl = MutableLiveData<String>()
    var fs = Firestore()
    var missionService = MissionService(
        fs.userCollection,
        fs.missionCollection,
        fs.transactionCollection,
    )
    val commentCollection = CommentCollection()
    lateinit var alertBuilder: AlertDialog.Builder
    val agentListAdapter = MutableLiveData<Agent1RecyclerViewAdapter>()
    val btnCancelConfirmedVisibility = MediatorLiveData<Int>()
    val btnAcceptVisibility = MediatorLiveData<Int>()
    val btnDisputeVisibility = MediatorLiveData<Int>()
    val btnLeaveReviewVisibility = MediatorLiveData<Int>()
    val selectedAgentLayoutVisibility = MediatorLiveData<Int>()
    val comments = MutableLiveData<List<Comment>>()
    val rating = MediatorLiveData<Float>()
    var onOpenChat: ((User) -> Unit)? = null // arg: selected agent
    val onChatBtnOfSelectedAgentClicked = View.OnClickListener { onOpenChat?.invoke(selectedAgent.value!!) }
    var onViewProfile: ((User) -> Unit)? = null // arg: selected agent
    val onViewProfileBtnOfSelectedAgentClicked = View.OnClickListener { onViewProfile?.invoke(selectedAgent.value!!) }
    var onAcceptedMission: ((Boolean) -> Unit)? = null // arg: save db result
    var doRefreshScreen: (() -> Unit)? = null

    init {
        missionCredit.addSource(mission) {
            missionCredit.value = it.price.toString()
        }
        btnCancelConfirmedVisibility.addSource(mission) {
            btnCancelConfirmedVisibility.value = if (isCurrentDateAfterMissionStartDate()) View.INVISIBLE else View.VISIBLE
        }
        btnAcceptVisibility.addSource(mission) {
            btnAcceptVisibility.value = if (isCurrentDateAfterMissionStartDate()) View.VISIBLE else View.INVISIBLE
        }
        btnDisputeVisibility.addSource(mission) {
            btnDisputeVisibility.value = if (isCurrentDateAfterMissionEndDate()) View.VISIBLE else View.INVISIBLE
        }
        selectedAgentLayoutVisibility.addSource(selectedAgent) {
            selectedAgentLayoutVisibility.value = View.VISIBLE
        }
        btnLeaveReviewVisibility.addSource(isAgentReviewed) {
            btnLeaveReviewVisibility.value = checkBtnLeaveReviewVisibility()
        }
        btnLeaveReviewVisibility.addSource(selectedAgent) {
            btnLeaveReviewVisibility.value = checkBtnLeaveReviewVisibility()
        }
        rating.addSource(comments) {
            rating.value = calculateRating()
        }
    }

    fun calculateRating(): Float {
        if (comments.value!!.isEmpty()) {
            return 0F
        }
        var ratingSum = 0.0
        comments.value!!.forEach {
            ratingSum += it.rating
        }
        return (ratingSum / comments.value!!.size).toFloat()
    }

    fun selectAgent(mission: Mission, selectedAgent: String, onSuccess: (Mission) -> Unit) {
        missionService.selectAgent(mission, selectedAgent, onSuccess)
    }

    fun completeMission(mission: Mission, onSuccess: (Mission) -> Unit) {
        missionService.completeMission(mission, onSuccess)
    }

    fun updateUser(user: User, onSuccess: (User) -> Unit) {
        fs.userCollection.updateUser(user, onSuccess)
    }

    fun isMissionStartIn48Hours(): Boolean {
        var startTime = Calendar.getInstance()
        startTime.timeInMillis = mission.value!!.startTime
        var date48HoursBefore = Calendar.getInstance()
        date48HoursBefore.timeInMillis = startTime.timeInMillis - 172800000L // 48 hours before
        var curDate = Calendar.getInstance()

        return curDate.after(date48HoursBefore)
    }

    fun isCurrentDateAfterMissionStartDate(): Boolean {
        var curDate = Calendar.getInstance()
        var startTime = Calendar.getInstance()
        startTime.timeInMillis = mission.value!!.startTime
        return curDate.after(startTime)
    }

    fun isCurrentDateAfterMissionEndDate(): Boolean {
        var curDate = Calendar.getInstance()
        var endTime = Calendar.getInstance()
        endTime.timeInMillis = mission.value!!.endTime
        return curDate.after(endTime)
    }

    fun getAgentsByEmails(emails: List<String>): Observable<List<User>> {
        return fs.userCollection.getUsers(emails)
    }

    @SuppressLint("CheckResult")
    fun getSelectedAgentFromDB() {
        fs.userCollection
            .getUserSingleTime(
                mission.value!!.selectedAgent,
                {
                    selectedAgent.value = it
                },
                {
                    Log.d("Employer mission detail",
                        "Fail to get selected agent from DB: $it")
                }
            )
    }

    fun cancelOpenMissionByEmployer(onSuccess: (MissionUser) -> Unit) {
        missionService.cancelOpenMissionByEmployer(
            mission.value!!,
            UserData.currentUserData,
            onSuccess
        )
    }

    fun cancelMissionBefore48HoursByEmployer(onSuccess: (MissionUser) -> Unit) {
        missionService.cancelMissionBefore48HoursByEmployer(
            mission.value!!,
            UserData.currentUserData,
            onSuccess
        )
    }

    fun cancelMissionWithin48HoursByEmployer(onSuccess: (MissionUser) -> Unit) {
        missionService.cancelMissionWithin48HoursByEmployer(
            mission.value!!,
            UserData.currentUserData,
            onSuccess
        )
    }

    fun disputeMission(onSuccess: (Mission) -> Unit) {
        missionService.disputeMission(mission.value!!, onSuccess)
    }

    fun rejectMission(onSuccess: (Mission) -> Unit) {
        missionService.rejectMission(mission.value!!, onSuccess)
    }

    fun getCommentsFromDB(user: User, onResult: ((List<Comment>) -> Unit)) {
        commentCollection.getCommentsSingleTime(
            user.email,
            onResult
        ) {
            Log.d("Employer Mission Detail", "Fail to get comments from DB: $it")
        }
    }

    fun getCommentsForSelectedAgentFromDB(user: User) {
        commentCollection.getCommentsSingleTime(
            user.email,
            {
                comments.value = it
            },
            {
                Log.d("Employer Mission Detail", "Fail to get comments from DB: $it")
            }
        )
    }

    @SuppressLint("CheckResult")
    fun getAgentsFromDB() {
        mission.value?.let {
            if (it.enrollments.isNotEmpty()) {
                getAgentsByEmails(it.enrollments)
                    .subscribe { users ->
                        updateAgentList(users)
                    }
            }
        }
    }

    @SuppressLint("CheckResult")
    fun updateAgentList(agents: List<User>) {
        val adapter = Agent1RecyclerViewAdapter(agents) { agent, onResult ->
            getCommentsFromDB(agent, onResult)
        }
        adapter.chatAgent.subscribe {
            onOpenChat?.invoke(it)
        }
        adapter.viewAgent.subscribe {
            onViewProfile?.invoke(it)
        }
        adapter.selectedAgent.subscribe {
            alertBuilder
                .setTitle("Select ${it.firstName} ${it.lastName} as your agent?")
                .setMessage(R.string.select_agent_alert_msg)
                .setPositiveButton("Confirm Mission") { _, _ ->
                    selectAgent(
                        mission.value!!,
                        it.email
                    ) { updateMissionResult ->
                        mission.value = updateMissionResult
                        doRefreshScreen?.invoke()
                    }
                }
                .setNegativeButton("Back", null)
                .show()
        }
        agentListAdapter.value = adapter
    }

    val btnCancelOpenOnClickListener = View.OnClickListener {
        alertBuilder
            .setTitle(R.string.cancel_mission_alert_title)
            .setMessage(R.string.cancel_open_mission_alert_msg)
            .setPositiveButton("Confirm Cancel") { _, _ ->
                cancelOpenMissionByEmployer { missionUser ->
                    mission.value = missionUser.mission
                    UserData.currentUserData = missionUser.user
                    doRefreshScreen?.invoke()
                }
            }
            .setNegativeButton("Back", null)
            .show()
    }

    val btnCancelConfirmedOnClickListener = View.OnClickListener {
        if (isMissionStartIn48Hours()) {
            alertBuilder
                .setTitle(R.string.cancel_mission_alert_title)
                .setMessage(R.string.cancel_confirmed_mission_in_48_hours_alert_msg)
                .setPositiveButton("Confirm Cancel") { _, _ ->
                    cancelMissionWithin48HoursByEmployer { missionUser ->
                        mission.value = missionUser.mission
                        UserData.currentUserData = missionUser.user
                        doRefreshScreen?.invoke()
                    }
                }
                .setNegativeButton("Back", null)
                .show()
        } else {
            alertBuilder
                .setTitle(R.string.cancel_mission_alert_title)
                .setMessage(R.string.cancel_confirmed_mission_before_48_hours_alert_msg)
                .setPositiveButton("Confirm Cancel") { _, _ ->
                    cancelMissionBefore48HoursByEmployer { missionUser ->
                        mission.value = missionUser.mission
                        UserData.currentUserData = missionUser.user
                        doRefreshScreen?.invoke()
                    }
                }
                .setNegativeButton("Back", null)
                .show()
        }
    }

    val btnAcceptOnClickListener = View.OnClickListener {
        alertBuilder
            .setTitle(R.string.accept_mission_result_alert_title)
            .setMessage(R.string.accept_mission_result_alert_msg)
            .setPositiveButton("Confirm Completion") { _, _ ->
                completeMission(mission.value!!) { result ->
                    mission.value = result
                    onAcceptedMission?.invoke(true)
                }
            }
            .setNegativeButton("Back", null)
            .show()
    }

    val btnDisputeOnClickListener = View.OnClickListener {
        alertBuilder
            .setTitle(R.string.dispute_mission_alert_title)
            .setMessage(R.string.dispute_mission_alert_msg)
            .setPositiveButton("Raise Dispute") { _, _ ->
                disputeMission() { result ->
                    mission.value = result
                    doRefreshScreen?.invoke()
                }
            }
            .setNegativeButton("Back", null)
            .show()
    }

    val btnRejectOnClickListener = View.OnClickListener {
        alertBuilder
            .setTitle(R.string.reject_mission_alert_title)
            .setMessage(R.string.reject_mission_alert_msg)
            .setPositiveButton("Confirm Reject") { _, _ ->
                rejectMission { result ->
                    mission.value = result
                    doRefreshScreen?.invoke()
                }
            }
            .setNegativeButton("Back", null)
            .show()
    }

    fun checkBtnLeaveReviewVisibility(): Int {
        return if (mission.value != null
            && !mission.value!!.isReviewed
            && selectedAgent.value != null) View.VISIBLE
        else View.INVISIBLE
    }
}