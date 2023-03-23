package com.team2.handiwork.fragments

import android.Manifest
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.drawable.GradientDrawable
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.ScrollView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.team2.handiwork.AppConst
import com.team2.handiwork.R
import com.team2.handiwork.ScreenMsg
import com.team2.handiwork.adapter.HomeMissionRecyclerViewAdapter
import com.team2.handiwork.adapter.MyMissionsRecyclerViewAdapter
import com.team2.handiwork.databinding.FragmentAgentHomeBinding
import com.team2.handiwork.models.Mission
import com.team2.handiwork.services.MissionNotificationHelper
import com.team2.handiwork.utilities.GridSpacingItemDecorator
import com.team2.handiwork.utilities.MissionSuggestionWorker
import com.team2.handiwork.utilities.SpacingItemDecorator
import com.team2.handiwork.viewModel.ActivityHomeViewModel
import com.team2.handiwork.viewModel.FragmentAgentHomeViewModel
import java.util.UUID
import java.util.concurrent.TimeUnit


class AgentHomeFragment : Fragment(), OnItemSelectedListener {

    lateinit var binding: FragmentAgentHomeBinding
    lateinit var viewModel: FragmentAgentHomeViewModel
    private val homeActivityVm: ActivityHomeViewModel by activityViewModels()
    private lateinit var ownMissionAdapter: HomeMissionRecyclerViewAdapter
    private lateinit var poolMissionAdapter: MyMissionsRecyclerViewAdapter
    private lateinit var suggestedMissionAdapter: MyMissionsRecyclerViewAdapter
    private val MY_PERMISSIONS_REQUEST_LOCATION = 100

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAgentHomeBinding.inflate(layoutInflater, container, false)
        viewModel = FragmentAgentHomeViewModel()
        val sp = PreferenceManager.getDefaultSharedPreferences(this.requireContext())
        val email = sp.getString(AppConst.EMAIL, "")
        viewModel.observeMissionList(homeActivityVm)
        initSpinner(resources.getStringArray(R.array.agent_mission_history_filter))

        homeActivityVm.currentUser.observe(viewLifecycleOwner) { user ->
            viewModel.getMissionFromMissionPool(user.email)
        }
        homeActivityVm.missions.observe(viewLifecycleOwner) { AllMissions ->
            AllMissions?.let {
                if (AllMissions.isEmpty()) {
                    binding.agentOwnMissionLayout.visibility = View.GONE
                }
            }
        }

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                MY_PERMISSIONS_REQUEST_LOCATION
            )
        } else {
            viewModel.getUserLocation(
                requireActivity()
                    .getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
            )
        }

        viewModel.suggestedMissionCount.observe(viewLifecycleOwner) { newCount ->
            val oldCount = sp.getInt(AppConst.PREF_SUGGESTED_MISSION_COUNT, 0)
            if (newCount > oldCount) {
                MissionNotificationHelper(requireContext()).sendMissionNotification(newCount - oldCount)
            }
            val editor: SharedPreferences.Editor = sp.edit()
            editor.putInt(AppConst.PREF_SUGGESTED_MISSION_COUNT, newCount)
            editor.apply()
        }

        initSuggestedMissionRecyclerView()
        viewModel.suggestedMissions.observe(viewLifecycleOwner) { missions ->
            missions?.let {
                suggestedMissionAdapter.setList(missions)
                if (missions.isEmpty()) {
                    binding.missionSuggestionLayout.visibility = View.GONE
                } else {
                    binding.missionSuggestionLayout.visibility = View.VISIBLE
                }
            }
        }

        initMissionPoolRecyclerView()
        viewModel.poolMissions.observe(viewLifecycleOwner) { missions ->
            missions?.let {
                poolMissionAdapter.setList(missions)
            }
        }


        initOwnMissionRecyclerView()
        viewModel.filteredMissions.observe(viewLifecycleOwner) { missions ->
            missions?.let {
                ownMissionAdapter.setList(missions)
                if (missions.isEmpty()) {
                    displayNoMissionInstruction()
                    if (viewModel.filterText.value == "All") {
                        binding.instructionText.text = "No mission at all"
                    } else {
                        binding.instructionText.text = "No mission is ${viewModel.filterText.value}"
                    }
                } else {
                    disableNoMissionInstruction()
                }
            }
        }

        binding.focusUpButton.setOnClickListener {
            binding.scrollViewLayout.fullScroll(ScrollView.FOCUS_UP)
        }

        binding.suggestRefreshBtn.setOnClickListener {
            viewModel.getMissionFromMissionPool(email!!)
        }

        binding.poolMissionRefreshBtn.setOnClickListener {
            viewModel.getMissionFromMissionPool(email!!)
        }

        return binding.root
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    viewModel.getUserLocation(
                        requireActivity()
                            .getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
                    )
                }
            }
        }
        return
    }

    private fun displayNoMissionInstruction() {
        binding.noMissionInstruction.visibility = View.VISIBLE
    }

    private fun disableNoMissionInstruction() {
        binding.noMissionInstruction.visibility = View.GONE
    }

    private fun initSuggestedMissionRecyclerView() {
        binding.suggestedMissionRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val itemDecorator = SpacingItemDecorator(10)
        suggestedMissionAdapter = MyMissionsRecyclerViewAdapter(changeDrawableColor, onMissionClick)
        binding.suggestedMissionRecyclerView.addItemDecoration(itemDecorator)
        binding.suggestedMissionRecyclerView.adapter = suggestedMissionAdapter
    }

    private fun initOwnMissionRecyclerView() {
        binding.ownMissionRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val itemDecorator = SpacingItemDecorator(10)
        binding.ownMissionRecyclerView.addItemDecoration(itemDecorator)
        ownMissionAdapter = HomeMissionRecyclerViewAdapter(changeDrawableColor, onMissionClick)
        binding.ownMissionRecyclerView.adapter = ownMissionAdapter
    }

    private fun initMissionPoolRecyclerView() {
        binding.missionPoolRecyclerView.layoutManager =
            GridLayoutManager(context, viewModel.serviceTypeListColumnNum)
        poolMissionAdapter = MyMissionsRecyclerViewAdapter(changeDrawableColor, onMissionClick)
        binding.missionPoolRecyclerView.addItemDecoration(GridSpacingItemDecorator(2,60,true))
        binding.missionPoolRecyclerView.adapter = poolMissionAdapter
    }


    private val changeDrawableColor: (textView: TextView, mission: Mission) -> Unit =
        { textView, mission ->
            val backgroundDrawable = GradientDrawable()
            backgroundDrawable.shape = GradientDrawable.RECTANGLE
            val cornerRadius = 20.0f
            backgroundDrawable.cornerRadius = cornerRadius
            backgroundDrawable.setColor(
                ContextCompat.getColor(
                    requireContext(),
                    viewModel.convertStatusColor(mission.status)
                )
            )
            textView.background = backgroundDrawable
        }

    private fun initSpinner(entries: Array<String>) {
        val adapter = ArrayAdapter(requireContext(), R.layout.mission_filter_spinner_item, entries)
        adapter.setDropDownViewResource(R.layout.mission_filter_spinner_dropdown_item)
        binding.missionFilterSpinner.adapter = adapter
        binding.missionFilterSpinner.onItemSelectedListener = this
    }

    private val onMissionClick: (mission: Mission) -> Unit = {
        val bundle: Bundle = Bundle()
        bundle.putSerializable("mission", it)
        findNavController().navigate(
            R.id.action_agentHomeFragment_to_agentMissionDetailFragment,
            bundle
        )
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        viewModel.updateFilter(parent!!.getItemAtPosition(position) as String)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

}