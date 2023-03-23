package com.team2.handiwork.fragments.registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.team2.handiwork.R
import com.team2.handiwork.activity.UserProfileActivity
import com.team2.handiwork.adapter.SubServiceTypeRecyclerViewAdapter
import com.team2.handiwork.base.fragment.BaseFragmentActivity
import com.team2.handiwork.databinding.FragmentRegistrationChooseSubServiceTypeBinding
import com.team2.handiwork.models.ServiceType

class RegistrationChooseSubServiceTypeFragment(var serviceTypeList: List<ServiceType>) :
    BaseFragmentActivity() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentRegistrationChooseSubServiceTypeBinding.inflate(
            inflater,
            container,
            false
        )
        val fragmentActivity = requireActivity() as UserProfileActivity
        val vm = fragmentActivity.vm
        binding.vm = vm

        val adapter = SubServiceTypeRecyclerViewAdapter(serviceTypeList)

        binding.lifecycleOwner = this
        fragmentActivity.binding.vm!!.currentStep.value = 2
        binding.rvList.adapter = adapter
        binding.rvList.layoutManager = LinearLayoutManager(this.requireContext())

        adapter.selectServiceType.subscribe {
            if (it.subServiceTypeList.size == 0) {
                vm.selectedServiceTypeMap.remove(it.name)
            } else {
                vm.selectedServiceTypeMap[it.name] = it
            }
        }
        fragmentActivity.setActionBarTitle("To be more specific:")

        binding.btnNext.setOnClickListener {
            fragmentActivity.vm.registrationForm.value!!.serviceTypeList =
                vm.selectedServiceTypeMap.values.toList()
            this.navigate(
                R.id.fm_registration,
                RegistrationWorkerProfileFragment(),
                "RegistrationWorkerProfileFragment"
            )
        }
        binding.btnSkip.setOnClickListener {
            this.navigate(
                R.id.fm_registration,
                RegistrationWorkerProfileFragment(),
                "RegistrationWorkerProfileFragment"
            )
        }


        return binding.root
    }


}