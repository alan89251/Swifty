package com.team2.handiwork.fragments

import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.team2.handiwork.R
import com.team2.handiwork.adapter.CreateMissionServiceTypeRecyclerViewAdapter
import com.team2.handiwork.adapter.HomeMissionRecyclerViewAdapter
import com.team2.handiwork.databinding.FragmentHomeBinding
import com.team2.handiwork.models.Mission
import com.team2.handiwork.models.ServiceType
import com.team2.handiwork.models.SubServiceType
import com.team2.handiwork.singleton.UserData
import com.team2.handiwork.utilities.Utility
import com.team2.handiwork.viewModel.ActivityHomeViewModel
import com.team2.handiwork.viewModel.FragmentHomeViewModel


class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding
    lateinit var viewModel: FragmentHomeViewModel
    private val homeActivityVm: ActivityHomeViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        viewModel = FragmentHomeViewModel()
        binding.progressBar.visibility = View.VISIBLE


        homeActivityVm.currentUser.observe(viewLifecycleOwner) { user ->
            binding.userCredit.text = user.balance.toString()
            viewModel.getMissionsByEmail(user.email)
        }


        viewModel.missions.observe(viewLifecycleOwner) { missions ->
            if (missions.isEmpty()) {
                setupNoMissionUI()
            } else {
                setupHasMissionUI()
                initHasMissionRecyclerView(missions)
            }
        }

        binding.addMissionButton.setOnClickListener {
            if (viewModel.checkEnoughBalance()) {
                navigateToFailScreen()
            } else {
                navigateToSelectCategoryScreen()
            }
        }
        return binding.root
    }

    private fun setupHasMissionUI() {
        binding.progressBar.visibility = View.GONE
        binding.noMissionLayoutGroup.visibility = View.GONE
        binding.hasMissionLayoutGroup.visibility = View.VISIBLE
    }

    private fun setupNoMissionUI() {
        binding.progressBar.visibility = View.GONE
        binding.hasMissionLayoutGroup.visibility = View.GONE
        binding.noMissionLayoutGroup.visibility = View.VISIBLE
        initNoMissionRecyclerView()
    }

    private fun initNoMissionRecyclerView() {
        binding.homeMissionCategoryRecyclerView.layoutManager =
            GridLayoutManager(context, viewModel.serviceTypeListColumnNum)
        val adapter = CreateMissionServiceTypeRecyclerViewAdapter(getServiceTypes())
        binding.homeMissionCategoryRecyclerView.adapter = adapter
        adapter.selectServiceType.subscribe {
            viewModel.mission.serviceType = it.name

            if (viewModel.checkEnoughBalance()) {
                val action =
                    HomeFragmentDirections.actionHomeFragmentToCreateMissionSelectSubServiceTypeFragment(viewModel.mission)
                findNavController().navigate(action)
            } else {
                navigateToFailScreen()
            }
        }
    }

    private fun initHasMissionRecyclerView(missions : List<Mission>) {
        binding.homeMissionRecyclerView. layoutManager = LinearLayoutManager(requireContext())
        val adapter = HomeMissionRecyclerViewAdapter()
        adapter.setList(missions)
        binding.homeMissionRecyclerView.adapter = adapter
    }

    private fun getServiceTypes(): ArrayList<ServiceType> {
        return viewModel.serviceTypes.map {
            val serviceType = ServiceType()
            serviceType.name = it
            serviceType.subServiceTypeList = resources.getStringArray(
                viewModel.getSubServiceTypesResId(it)
            ).map {
                val subServiceType = SubServiceType()
                subServiceType.name = it
                subServiceType
            }.toList() as ArrayList<SubServiceType>
            serviceType
        }.toList() as ArrayList<ServiceType>
    }

    private fun navigateToFailScreen() {
        findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToFailCreateMissionFragment())
    }

    private fun navigateToSelectCategoryScreen() {
        findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToCreateMissionSelectCategoryFragment())
    }

}