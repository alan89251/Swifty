package com.team2.handiwork.fragments

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.team2.handiwork.R
import com.team2.handiwork.adapter.HomeMissionRecyclerViewAdapter
import com.team2.handiwork.adapter.MyMissionsRecyclerViewAdapter
import com.team2.handiwork.databinding.FragmentAgentHomeBinding
import com.team2.handiwork.databinding.FragmentHomeBinding
import com.team2.handiwork.models.Mission
import com.team2.handiwork.utilities.SpacingItemDecorator
import com.team2.handiwork.utilities.Utility
import com.team2.handiwork.viewModel.ActivityHomeViewModel
import com.team2.handiwork.viewModel.FragmentAgentHomeViewModel
import com.team2.handiwork.viewModel.FragmentHomeViewModel
import kotlin.math.log


class AgentHomeFragment : Fragment(), OnItemSelectedListener {

    lateinit var binding: FragmentAgentHomeBinding
    lateinit var viewModel: FragmentAgentHomeViewModel
    private val homeActivityVm: ActivityHomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAgentHomeBinding.inflate(layoutInflater, container, false)
        viewModel = FragmentAgentHomeViewModel()
        viewModel.observeMissionList(homeActivityVm)
        initSpinner(resources.getStringArray(R.array.agent_mission_history_filter))

        homeActivityVm.currentUser.observe(viewLifecycleOwner) { user ->
            viewModel.setEmail(user)
            viewModel.getMissionFromMissionPool()
        }

        viewModel.poolMissions.observe(viewLifecycleOwner) { missions ->
            initMissionPoolRecyclerView(missions)
            Log.d("hehehehe", "${missions.size}")
        }

        viewModel.filteredMissions.observe(viewLifecycleOwner) { missions ->
//            initOwnMissionRecyclerView(missions)
//            if (missions.isEmpty()) {
//                displayNoMissionInstruction()
//                if (viewModel.filterText.value == "All") {
//                    binding.instructionText.text = "No mission at all"
//                } else {
//                    binding.instructionText.text = "No mission is ${viewModel.filterText.value}"
//                }
//            } else {
//                disableNoMissionInstruction()
//            }
        }

        return binding.root
    }

    private fun displayNoMissionInstruction() {
        binding.noMissionInstruction.visibility = View.VISIBLE
    }

    private fun disableNoMissionInstruction() {
        binding.noMissionInstruction.visibility = View.GONE
    }

    private fun initOwnMissionRecyclerView(missions: List<Mission>) {
        binding.ownMissionRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val itemDecorator = SpacingItemDecorator(10)
        binding.ownMissionRecyclerView.addItemDecoration(itemDecorator)
        val adapter = HomeMissionRecyclerViewAdapter(changeDrawableColor, onMissionClick)
        adapter.setList(missions)
        binding.ownMissionRecyclerView.adapter = adapter
    }

    private fun initMissionPoolRecyclerView(missions: List<Mission>) {
        binding.missionPoolRecyclerView.layoutManager =
            GridLayoutManager(context, viewModel.serviceTypeListColumnNum)
        val adapter = MyMissionsRecyclerViewAdapter(changeDrawableColor, onMissionClick)
        adapter.setList(missions)
        binding.missionPoolRecyclerView.adapter = adapter
    }


    private val changeDrawableColor: (textView: TextView, mission: Mission) -> Unit = { textView, mission ->
        val backgroundDrawable = GradientDrawable()
        backgroundDrawable.shape = GradientDrawable.RECTANGLE
        val cornerRadius = 20.0f
        backgroundDrawable.cornerRadius = cornerRadius
        backgroundDrawable.setColor(
            ContextCompat.getColor(
                requireContext(),
                Utility.convertStatusColor(mission.status)
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
        Toast.makeText(requireContext(), it.employer, Toast.LENGTH_SHORT).show()
        // Todo navigate to mission detail page
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        viewModel.updateFilter(parent!!.getItemAtPosition(position) as String)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

}