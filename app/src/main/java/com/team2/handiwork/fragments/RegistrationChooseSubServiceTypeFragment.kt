package com.team2.handiwork.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.team2.handiwork.R
import com.team2.handiwork.activity.UserProfileActivity
import com.team2.handiwork.adapter.SubServiceTypeRecyclerViewAdapter
import com.team2.handiwork.databinding.FragmentRegistrationChooseSubServiceTypeBinding
import com.team2.handiwork.models.ServiceType

class RegistrationChooseSubServiceTypeFragment(var serviceTypeList: List<ServiceType>) :
    Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentRegistrationChooseSubServiceTypeBinding.inflate(
            inflater,
            container,
            false
        )
        val activity = requireActivity() as UserProfileActivity
        val vm = activity.vm
        binding.vm = vm

        val adapter = SubServiceTypeRecyclerViewAdapter(serviceTypeList)
        binding.lifecycleOwner = this
        activity.binding.vm!!.currentStep.value = 2
        binding.rvList.adapter = adapter
        binding.rvList.layoutManager = LinearLayoutManager(this.requireContext())

        adapter.selectServiceType.subscribe {
            if (it.subServiceTypeList.size == 0) {
                vm.selectedServiceTypeMap.remove(it.name)
            } else {
                vm.selectedServiceTypeMap[it.name] = it
            }
        }
        activity.setActionBarTitle("To be more specific:")


        val trans = activity
            .supportFragmentManager
            .beginTransaction()


        binding.btnNext.setOnClickListener {
            activity.vm.registrationForm.value!!.serviceTypeList =
                vm.selectedServiceTypeMap.values.map { serviceType ->
                    serviceType.subServiceTypeList.removeIf { !it.selected }
                    serviceType
                }
            // todo route to map
            trans.replace(
                R.id.fm_registration,
                RegistrationWorkerProfileFragment()
            )
            trans.addToBackStack("RegistrationWorkerProfileFragment")
            trans.commit()
        }
        binding.btnSkip.setOnClickListener {
            trans.replace(
                R.id.fm_registration,
                RegistrationWorkerProfileFragment()
            )
            trans.addToBackStack("RegistrationWorkerProfileFragment")
            trans.commit()
        }


        return binding.root
    }


}