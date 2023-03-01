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
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.team2.handiwork.AppConst
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
    private lateinit var ownMissionAdapter: HomeMissionRecyclerViewAdapter
    private lateinit var poolMissionAdapter: MyMissionsRecyclerViewAdapter
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

//        viewModel.getUserEnrollments(email!!).subscribe() { enrollments ->
//            viewModel.getMissionByEnrollments(enrollments)
//        }


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

                // Todo filter logic for agent side
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

        return binding.root
    }

    private fun displayNoMissionInstruction() {
        binding.noMissionInstruction.visibility = View.VISIBLE
    }

    private fun disableNoMissionInstruction() {
        binding.noMissionInstruction.visibility = View.GONE
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
        binding.missionPoolRecyclerView.adapter = poolMissionAdapter
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