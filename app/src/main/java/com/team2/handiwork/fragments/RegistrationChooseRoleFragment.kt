package com.team2.handiwork.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.team2.handiwork.R
import com.team2.handiwork.activity.RegistrationPersonalInformationActivity
import com.team2.handiwork.databinding.FragmentRegistrationChooseRoleBinding
import com.team2.handiwork.enum.EditorKey
import com.team2.handiwork.enum.SharePreferenceKey
import com.team2.handiwork.models.UserRegistrationForm
import com.team2.handiwork.viewModel.FragmentRegistrationChooseRoleViewModel

class RegistrationChooseRoleFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        val binding = FragmentRegistrationChooseRoleBinding.inflate(
            inflater, container, false
        )
        val vm = FragmentRegistrationChooseRoleViewModel()
        val activity = requireActivity() as RegistrationPersonalInformationActivity
        activity.setCurrentStep(2)
        binding.vm = vm
        binding.lifecycleOwner = this


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

        // todo jump to next fragment
        binding.btnCard1.setOnClickListener {
            val button = it as Button
            vm.isAgent.value = button.currentTextColor == ContextCompat.getColor(
                this.requireContext(),
                R.color.buttonColor
            )
            form.isAgent = vm.isAgent.value!!
            val formJson: String = Gson().toJson(form)
            editor.putString(EditorKey.USER_FORM.toString(), formJson)
            editor.apply()

            activity
                .supportFragmentManager
                .beginTransaction()
                .replace(R.id.fm_registration, RegistrationChooseServiceTypeFragment())
                .commit()
        }
        binding.btnCard2.setOnClickListener {
            val button = it as Button
            vm.isEmployer.value = button.currentTextColor == ContextCompat.getColor(
                this.requireContext(),
                R.color.buttonColor
            )

            form.isEmployer = vm.isEmployer.value!!

            val formJson: String = Gson().toJson(form)
            editor.putString(EditorKey.USER_FORM.toString(), formJson)
            editor.apply()

            activity
                .supportFragmentManager
                .beginTransaction()
                .replace(R.id.fm_registration, RegistrationChooseServiceTypeFragment())
                .commit()
        }

        binding.btnDoBoth.setOnClickListener {
            form.isAgent = true
            form.isEmployer = true


            val formJson: String = Gson().toJson(form)
            editor.putString(EditorKey.USER_FORM.toString(), formJson)
            editor.apply()

            activity
                .supportFragmentManager
                .beginTransaction()
                .replace(R.id.fm_registration, RegistrationChooseServiceTypeFragment())
                .commit()
        }
        return binding.root
    }
}