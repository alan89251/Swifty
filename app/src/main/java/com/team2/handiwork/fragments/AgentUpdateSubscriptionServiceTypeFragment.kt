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
import com.team2.handiwork.databinding.FragmentRegistrationChooseServiceTypeBinding
import com.team2.handiwork.models.ServiceType
import com.team2.handiwork.models.SubServiceType
import com.team2.handiwork.singleton.UserData
import com.team2.handiwork.viewModel.FragmentRegistrationChooseServiceTypeViewModel

class AgentUpdateSubscriptionServiceTypeFragment : Fragment() {
    private var columnCount = 2
    private lateinit var vm: FragmentRegistrationChooseServiceTypeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(RegistrationChooseServiceTypeFragment.ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val binding = FragmentRegistrationChooseServiceTypeBinding.inflate(
            inflater, container, false
        )

        vm = FragmentRegistrationChooseServiceTypeViewModel()
        binding.vm = vm
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

        binding.lifecycleOwner = this
        // mark the service type that the agent has already selected
        markCurrentSelectedServiceTypes()
        binding.rvGrid.layoutManager = GridLayoutManager(context, columnCount)
        val adapter = ServiceTypeRecyclerViewAdapter(vm.serviceTypeMap.values.toList())
        binding.rvGrid.adapter = adapter
        adapter.selectServiceType.subscribe {
            vm.serviceTypeMap[it.name]!!.selected = it.selected
        }

        binding.btnNext.setOnClickListener {
            val selectedList = vm.serviceTypeMap.values.toList().filter { it.selected }
            if (selectedList.isEmpty()) {
                return@setOnClickListener
            }

            // Update UserData in memory only, not yet save to DB
            updateAgentSubscribedServiceTypes()
            navigateToAgentUpdateSubscriptionSubServiceTypeFragment()
        }

        binding.btnSkip.setOnClickListener {
            navigateToAgentUpdateSubscriptionSubServiceTypeFragment()
        }

        return binding.root
    }

    private fun updateAgentSubscribedServiceTypes() {
        UserData.currentUserData.serviceTypeList = vm.serviceTypeMap.values.toList().filter { it.selected }
    }

    private fun markCurrentSelectedServiceTypes() {
        for (serviceType in UserData.currentUserData.serviceTypeList) {
            vm.serviceTypeMap[serviceType.name]!!.selected = serviceType.selected
        }
    }

    fun navigateToAgentUpdateSubscriptionSubServiceTypeFragment() {
        val action = AgentUpdateSubscriptionServiceTypeFragmentDirections
            .actionAgentUpdateSubscriptionServiceTypeFragmentToAgentUpdateSubscriptionSubServiceTypeFragment()
        findNavController().navigate(action)
    }

    companion object {
        const val ARG_COLUMN_COUNT = "column-count"

        @JvmStatic
        fun newInstance(columnCount: Int) = AgentUpdateSubscriptionServiceTypeFragment().apply {
            arguments = Bundle().apply {
                putInt(RegistrationChooseServiceTypeFragment.ARG_COLUMN_COUNT, columnCount)
            }
        }
    }
}