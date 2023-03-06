package com.team2.handiwork.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.team2.handiwork.adapter.SubServiceTypeRecyclerViewAdapter
import com.team2.handiwork.databinding.FragmentAgentUpdateSubscriptionSubServiceTypeBinding
import com.team2.handiwork.singleton.UserData
import com.team2.handiwork.viewModel.ActivityRegistrationViewModel

class AgentUpdateSubscriptionSubServiceTypeFragment: Fragment() {
    private lateinit var vm: ActivityRegistrationViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentAgentUpdateSubscriptionSubServiceTypeBinding.inflate(
            inflater,
            container,
            false
        )
        vm = ActivityRegistrationViewModel()
        markCurrentSelectedSubServiceTypes()
        binding.lifecycleOwner = this
        binding.form.vm = vm
        binding.form.lifecycleOwner = this

        vm.primaryTextColor.value = "#FFFFFF" // white
        vm.primaryButtonColor.value = "#1845A0"

        val adapter = SubServiceTypeRecyclerViewAdapter(UserData.currentUserData.serviceTypeList)
        binding.form.rvList.adapter = adapter
        binding.form.rvList.layoutManager = LinearLayoutManager(this.requireContext())
        adapter.selectServiceType.subscribe {
            if (it.subServiceTypeList.size == 0) {
                vm.selectedServiceTypeMap.remove(it.name)
            } else {
                vm.selectedServiceTypeMap[it.name] = it
            }
        }

        binding.form.btnNext.setOnClickListener {
            // Update UserData in memory only, not yet save to DB
            updateAgentSubscribedSubServiceTypes()

            navigateToAgentUpdateSubscriptionLocationFragment()
        }
        binding.form.btnSkip.setOnClickListener {
            navigateToAgentUpdateSubscriptionLocationFragment()
        }

        return binding.root
    }

    private fun markCurrentSelectedSubServiceTypes() {
        for (serviceType in UserData.currentUserData.serviceTypeList) {
            vm.selectedServiceTypeMap[serviceType.name] = serviceType
        }
    }

    private fun updateAgentSubscribedSubServiceTypes() {
        UserData.currentUserData.serviceTypeList =
            vm.selectedServiceTypeMap.values.map { serviceType ->
                serviceType.subServiceTypeList.removeIf { !it.selected }
                serviceType
            }
    }

    private fun navigateToAgentUpdateSubscriptionLocationFragment() {
        val action = AgentUpdateSubscriptionSubServiceTypeFragmentDirections
            .actionAgentUpdateSubscriptionSubServiceTypeFragmentToAgentUpdateSubscriptionLocationFragment()
        findNavController().navigate(action)
    }
}