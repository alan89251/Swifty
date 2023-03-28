package com.team2.handiwork.fragments

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.GridLayoutManager
import com.team2.handiwork.AppConst
import com.team2.handiwork.R
import com.team2.handiwork.adapter.MyMissionsRecyclerViewAdapter
import com.team2.handiwork.base.fragment.DisposeFragment
import com.team2.handiwork.databinding.FragmentMyMissionsBinding
import com.team2.handiwork.models.Mission
import com.team2.handiwork.viewModel.ActivityHomeViewModel
import com.team2.handiwork.viewModel.mission.FragmentMyMissionsViewModel


class MyMissionsFragment : DisposeFragment(), AdapterView.OnItemSelectedListener {

    lateinit var binding: FragmentMyMissionsBinding
    lateinit var viewModel: FragmentMyMissionsViewModel
    private val homeActivityVm: ActivityHomeViewModel by activityViewModels()
    private lateinit var adapter: MyMissionsRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentMyMissionsBinding.inflate(inflater, container, false)
        viewModel = FragmentMyMissionsViewModel()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.progressBar.visibility = View.VISIBLE
        viewModel.observeMissionList(homeActivityVm)
        val pref = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val currentTheme = pref.getInt(AppConst.CURRENT_THEME, 0)

        if (currentTheme == 1) {
            binding.floatingActionButton.visibility = View.VISIBLE
            initSpinner(resources.getStringArray(R.array.employer_mission_history_filter))
        } else {
            binding.floatingActionButton.visibility = View.GONE
            initSpinner(resources.getStringArray(R.array.agent_mission_history_filter))
        }

        homeActivityVm.missions.observe(viewLifecycleOwner) { missions ->
            missions.let {
                if (missions.isEmpty()) {
                    setupNoMissionUI()
                } else {
                    setupHasMissionUI()
                }
            }
        }

        initMissionHistoryRecyclerView()
        viewModel.filteredMissions.observe(viewLifecycleOwner) { missions ->
            missions?.let {
                adapter.setList(missions)
            }
        }


        binding.floatingActionButton.setOnClickListener {
            if (viewModel.checkEnoughBalance()) {
                findNavController().navigate(MyMissionsFragmentDirections.actionMyMissionsFragmentToCreateMissionSelectCategoryFragment())
            } else {
                findNavController().navigate(MyMissionsFragmentDirections.actionMyMissionsFragmentToFailCreateMissionFragment())
            }
        }
    }

    private fun initSpinner(entries: Array<String>) {
        val adapter = ArrayAdapter(requireContext(), R.layout.mission_filter_spinner_item, entries)
        adapter.setDropDownViewResource(R.layout.mission_filter_spinner_dropdown_item)
        binding.missionFilterSpinner.adapter = adapter
        binding.missionFilterSpinner.onItemSelectedListener = this
    }


    private fun setupHasMissionUI() {
        binding.progressBar.visibility = View.GONE
        binding.noMissionsLayout.visibility = View.GONE
        binding.hasMissionsLayout.visibility = View.VISIBLE
    }

    private fun setupNoMissionUI() {
        binding.progressBar.visibility = View.GONE
        binding.hasMissionsLayout.visibility = View.GONE
        binding.noMissionsLayout.visibility = View.VISIBLE
    }

    private fun initMissionHistoryRecyclerView() {
        binding.homeMissionCategoryRecyclerView.layoutManager =
            GridLayoutManager(context, viewModel.serviceTypeListColumnNum)
        adapter = MyMissionsRecyclerViewAdapter(changeDrawableColor, onMissionClick)
        binding.homeMissionCategoryRecyclerView.adapter = adapter
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

    private val onMissionClick: (mission: Mission) -> Unit = { mission ->
        val pref = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val currentTheme = pref.getInt(AppConst.CURRENT_THEME, 0)

        if (currentTheme == 1) {
            val action =
                MyMissionsFragmentDirections.actionMyMissionsFragmentToEmployerMissionDetailsFragment(
                    mission
                )
            findNavController().navigate(action)
        } else {
            val bundle = Bundle()
            bundle.putSerializable("mission", mission)
            findNavController().navigate(
                R.id.action_myMissionsFragment_to_agentMissionDetailFragment,
                bundle
            )
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        viewModel.updateFilter(parent!!.getItemAtPosition(position) as String)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

}