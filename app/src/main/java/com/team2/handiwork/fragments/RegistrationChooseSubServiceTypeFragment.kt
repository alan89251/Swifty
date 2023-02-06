package com.team2.handiwork.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.team2.handiwork.activity.RegistrationPersonalInformationActivity
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
        binding.vm = FragmentRegistrationChooseSubServiceTypeViewModel()
        binding.lifecycleOwner = this
        val activity = requireActivity() as RegistrationPersonalInformationActivity
        activity.setCurrentStep(2)
        binding.rvList.adapter = SubServiceTypeRecyclerViewAdapter(serviceTypeList)
        binding.rvList.layoutManager = LinearLayoutManager(this.requireContext())
//        binding.rvList.adapter

        binding.btnNext.setOnClickListener {
            // todo route to map
//            activity
//                .supportFragmentManager
//                .beginTransaction()
//                .replace(R.id.fm_registration, RegistrationChooseServiceTypeFragment())
//                .commit()
        }
        binding.btnSkip.setOnClickListener {
            // todo route to map
//            activity
//                .supportFragmentManager
//                .beginTransaction()
//                .replace(R.id.fm_registration, RegistrationChooseServiceTypeFragment())
//                .commit()
        }


        return binding.root
    }


}