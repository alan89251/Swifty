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
import com.team2.handiwork.viewModel.FragmentRegistrationChooseSubServiceTypeViewModel

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
        val vm = FragmentRegistrationChooseSubServiceTypeViewModel()
        val adapter = SubServiceTypeRecyclerViewAdapter(serviceTypeList)
        binding.vm = vm
        binding.lifecycleOwner = this
        val activity = requireActivity() as UserProfileActivity
        activity.binding.vm!!.currentStep.value = 2
        binding.rvList.adapter = adapter
        binding.rvList.layoutManager = LinearLayoutManager(this.requireContext())

        adapter.selectServiceType.subscribe {
            if (it.selectedSubServiceTypeList.size == 0) {
                vm.selectedServiceTypeMap.remove(it.name)
            } else {
                vm.selectedServiceTypeMap[it.name] = it
            }
        }
        activity.setActionBarTitle("To be more specific:")


        binding.btnNext.setOnClickListener {
            activity.vm.registrationForm.value!!.serviceTypeList =
                vm.selectedServiceTypeMap.values.map { serviceType ->
                    serviceType.subServiceTypeList.clear()
                    serviceType.selectedSubServiceTypeList.removeIf { !it.selected }
                    serviceType
                }
            // todo route to map

            val trans = activity
                .supportFragmentManager
                .beginTransaction()

            trans.replace(
                R.id.fm_registration,
                RegistrationWorkerProfileFragment()
            )
            trans.addToBackStack("RegistrationWorkerProfileFragment")
            trans.commit()
        }
        binding.btnSkip.setOnClickListener {
            val trans = activity
                .supportFragmentManager
                .beginTransaction()

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