package com.team2.handiwork.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.team2.handiwork.R
import com.team2.handiwork.adapter.SubServiceTypeRecyclerViewAdapter
import com.team2.handiwork.databinding.FragmentAgentUpdateSubscriptionSubServiceTypeBinding
import com.team2.handiwork.models.ServiceType
import com.team2.handiwork.models.User
import com.team2.handiwork.viewModel.ActivityRegistrationViewModel

private const val ARG_UPDATE_FORM = "updateForm"

class AgentUpdateSubscriptionSubServiceTypeFragment : Fragment() {
    private lateinit var vm: ActivityRegistrationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = ActivityRegistrationViewModel()

        arguments?.let {
            vm.registrationForm.value = it.getSerializable(ARG_UPDATE_FORM) as User
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentAgentUpdateSubscriptionSubServiceTypeBinding.inflate(
            inflater,
            container,
            false
        )
        if (vm.isFirstTimeRun) {
            vm.markCurrentSelectedSubServiceTypes()
        }

        binding.lifecycleOwner = this
        binding.form.vm = vm
        binding.form.lifecycleOwner = this

        vm.primaryButtonColor.value = R.color.dark_blue_100

        val adapter = SubServiceTypeRecyclerViewAdapter(vm.selectedServiceTypeMap.values.toList())
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
            navigateToAgentUpdateSubscriptionLocationFragment()
        }
        binding.form.btnSkip.setOnClickListener {
            // reload original sub service types selection status from User
            vm.selectedServiceTypeMap = hashMapOf<String, ServiceType>()
            vm.markCurrentSelectedSubServiceTypes()

            navigateToAgentUpdateSubscriptionLocationFragment()
        }

        vm.isFirstTimeRun = false
        return binding.root
    }

    private fun navigateToAgentUpdateSubscriptionLocationFragment() {
        var updateForm = User()
        updateForm.serviceTypeList = vm.selectedServiceTypeMap.values.toList()
        val action = AgentUpdateSubscriptionSubServiceTypeFragmentDirections
            .actionAgentUpdateSubscriptionSubServiceTypeFragmentToAgentUpdateSubscriptionLocationFragment(
                updateForm
            )
        findNavController().navigate(action)
    }

    companion object {

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param updateForm Parameter 1.
         * @return A new instance of fragment AgentUpdateSubscriptionSubServiceTypeFragment
         */
        @JvmStatic
        fun newInstance(updateForm: User) =
            AgentUpdateSubscriptionSubServiceTypeFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_UPDATE_FORM, updateForm)
                }
            }
    }
}