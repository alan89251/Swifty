package com.team2.handiwork.fragments

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.rpc.context.AttributeContext.Resource
import com.team2.handiwork.R
import com.team2.handiwork.adapter.CreateMissionServiceTypeRecyclerViewAdapter
import com.team2.handiwork.adapter.MyMissionsRecyclerViewAdapter
import com.team2.handiwork.databinding.FragmentMyMissionsBinding
import com.team2.handiwork.models.Mission
import com.team2.handiwork.singleton.UserData
import com.team2.handiwork.viewModel.FragmentMyMissionsViewModel


class MyMissionsFragment : Fragment() {

    lateinit var binding: FragmentMyMissionsBinding
    lateinit var viewModel: FragmentMyMissionsViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMyMissionsBinding.inflate(inflater, container, false)
        viewModel = FragmentMyMissionsViewModel()
        viewModel.getMissionsByEmail(UserData.currentUserData.email)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.progressBar.visibility = View.VISIBLE

        viewModel.missions.observe(viewLifecycleOwner) { missions ->
            if (missions.isEmpty()) {
                setupNoMissionUI()
            } else {
                setupHasMissionUI()
                initMissionHistoryRecyclerView(missions)
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

    private fun initSpinner() {
        // Todo dynamic entries
        val entries = resources.getStringArray(R.array.agent_mission_history_filter)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, entries)
        binding.missionFilterSpinner.adapter = adapter
    }


    private fun setupHasMissionUI() {
        binding.progressBar.visibility = View.GONE
        binding.noMissionsLayout.visibility = View.GONE
        binding.hasMissionsLayout.visibility = View.VISIBLE
        initSpinner()
    }

    private fun setupNoMissionUI() {
        binding.progressBar.visibility = View.GONE
        binding.hasMissionsLayout.visibility = View.GONE
        binding.noMissionsLayout.visibility = View.VISIBLE
    }

    private fun initMissionHistoryRecyclerView(missions: List<Mission>) {
        binding.homeMissionCategoryRecyclerView.layoutManager =
            GridLayoutManager(context, viewModel.serviceTypeListColumnNum)
        val adapter = MyMissionsRecyclerViewAdapter(changeDrawableColor)
        adapter.setList(missions)
        binding.homeMissionCategoryRecyclerView.adapter = adapter
    }

    private val changeDrawableColor: (textView: TextView) -> Unit = {
        val backgroundDrawable = GradientDrawable()
        backgroundDrawable.shape = GradientDrawable.RECTANGLE
        val cornerRadius = 20.0f
        backgroundDrawable.cornerRadius = cornerRadius
        backgroundDrawable.setColor(ContextCompat.getColor(requireContext(), R.color.blue_500))
        it.background = backgroundDrawable
    }

}