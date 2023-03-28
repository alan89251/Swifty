package com.team2.handiwork.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.team2.handiwork.R
import com.team2.handiwork.adapter.ServiceTypeRecyclerViewAdapter
import com.team2.handiwork.databinding.FragmentAgentUpdateSubscriptionServiceTypeBinding
import com.team2.handiwork.models.ServiceType
import com.team2.handiwork.models.SubServiceType
import com.team2.handiwork.models.User
import com.team2.handiwork.singleton.UserData
import com.team2.handiwork.viewModel.ActivityRegistrationViewModel

class AgentUpdateSubscriptionServiceTypeFragment : Fragment() {
    private val columnCount = 2
    private lateinit var vm: ActivityRegistrationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = ActivityRegistrationViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val binding = FragmentAgentUpdateSubscriptionServiceTypeBinding.inflate(
            inflater, container, false
        )
        binding.lifecycleOwner = this
        binding.form.vm = vm
        binding.form.lifecycleOwner = this

        vm.primaryButtonColor.value = R.color.dark_blue_100

        // mark the service type that the agent has already selected
        if (vm.isFirstTimeRun) {
            loadServiceTypes()
        }

        binding.form.rvGrid.layoutManager = GridLayoutManager(requireContext(), columnCount)
        val adapter = ServiceTypeRecyclerViewAdapter(vm.serviceTypeMap.values.toList())
        binding.form.rvGrid.adapter = adapter
        adapter.selectServiceType.subscribe {
            vm.serviceTypeMap[it.name]!!.selected = it.selected
        }

        binding.form.btnNext.setOnClickListener {
            val selectedList = vm.serviceTypeMap.values.toList().filter { it.selected }
            if (selectedList.isEmpty()) {
                return@setOnClickListener
            }

            // Update UserData in memory only, not yet save to DB
            updateAgentSubscribedServiceTypes()
            navigateToAgentUpdateSubscriptionSubServiceTypeFragment()
        }

        binding.form.btnSkip.setOnClickListener {
            // reload original service types selection status from User
            loadServiceTypes()
            updateAgentSubscribedServiceTypes()
            navigateToAgentUpdateSubscriptionSubServiceTypeFragment()
        }

        vm.isFirstTimeRun = false
        return binding.root
    }

    private fun updateAgentSubscribedServiceTypes() {
        // copy the sub service type selected status to vm
        for (serviceType in UserData.currentUserData.serviceTypeList) {
            if (vm.serviceTypeMap[serviceType.name]!!.selected) {
                for (subServiceType in serviceType.subServiceTypeList) {
                    vm.serviceTypeMap[serviceType.name]!!
                        .subServiceTypeList
                        .find({ it.name == subServiceType.name })!!
                        .selected = subServiceType.selected
                }
            }
        }
    }

    private fun loadServiceTypes() {
        resources
            .getStringArray(R.array.service_type_list)
            .forEach {
                val serviceType = ServiceType()
                serviceType.name = it
                serviceType.subServiceTypeList = ArrayList<SubServiceType>(
                    resources
                        .getStringArray(vm.getSubServiceTypesResId(it))
                        .map { subServiceTypeName ->
                            val subServiceType = SubServiceType()
                            subServiceType.name = subServiceTypeName
                            subServiceType
                        }
                )
                vm.serviceTypeMap[serviceType.name] = serviceType
            }

        for (serviceType in UserData.currentUserData.serviceTypeList) {
            vm.serviceTypeMap[serviceType.name]!!.selected = serviceType.selected
        }
    }

    fun navigateToAgentUpdateSubscriptionSubServiceTypeFragment() {
        var updateForm = User()
        updateForm.serviceTypeList = vm.serviceTypeMap.values.toList().filter { it.selected }
        val action = AgentUpdateSubscriptionServiceTypeFragmentDirections
            .actionAgentUpdateSubscriptionServiceTypeFragmentToAgentUpdateSubscriptionSubServiceTypeFragment(
                updateForm
            )
        findNavController().navigate(action)
    }
}