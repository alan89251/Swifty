package com.team2.handiwork.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.team2.handiwork.activity.RegistrationPersonalInformationActivity
import com.team2.handiwork.adapter.SubServiceTypeRecyclerViewAdapter
import com.team2.handiwork.databinding.FragmentRegistrationChooseSubServiceTypeBinding
import com.team2.handiwork.enum.EditorKey
import com.team2.handiwork.enum.SharePreferenceKey
import com.team2.handiwork.models.ServiceType
import com.team2.handiwork.models.UserRegistrationForm
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

        binding.btnNext.setOnClickListener {

            val sp = requireActivity().getSharedPreferences(
                SharePreferenceKey.USER_FORM.toString(),
                Context.MODE_PRIVATE,
            )
            val json = sp.getString(EditorKey.USER_FORM.toString(), "")
            if (json == "") {
                Log.e("Error on sharedpreference ", "user registration form does not exist")
            }
            val form = Gson().fromJson(json, UserRegistrationForm::class.java)
            val editor = sp.edit()
            form.serviceTypeList = serviceTypeList

            val formJson: String = Gson().toJson(form)
            editor.putString(EditorKey.USER_FORM.toString(), formJson)
            editor.apply()

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