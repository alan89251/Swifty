package com.team2.handiwork.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.team2.handiwork.R
import com.team2.handiwork.activity.UserProfileActivity
import com.team2.handiwork.databinding.FragmentRegistrationWorkerTNCBinding
import com.team2.handiwork.enum.EditorKey
import com.team2.handiwork.viewModel.FragmentRegistrationWorkerTNCViewModel
import java.io.BufferedReader
import java.io.InputStreamReader

class RegistrationWorkerTNCFragment : Fragment() {
    private lateinit var binding: FragmentRegistrationWorkerTNCBinding
    private lateinit var vm: FragmentRegistrationWorkerTNCViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegistrationWorkerTNCBinding.inflate(inflater, container, false)
        vm = FragmentRegistrationWorkerTNCViewModel()
        binding.vm = vm
        binding.lifecycleOwner = this

        // config UIs
        val activity = requireActivity() as UserProfileActivity
        activity.binding.vm!!.currentStep.value = 3
        activity.setActionBarTitle("Terms and Conditions")

        binding.nextBtn.setOnClickListener(nextBtnOnClickListener)

        // load the terms and conditions from resource
        val reader = BufferedReader(
            InputStreamReader(
                resources.openRawResource(R.raw.terms_and_conditions)
            )
        )
        val termsAndConditions = reader.readText()
        reader.close()

        binding.termsAndConditions.text = termsAndConditions
        binding.termsAndConditions.movementMethod = ScrollingMovementMethod()
        binding.userAgreementSwitch.setOnCheckedChangeListener { _, b ->
            vm.isEnableNextBtn.value = b
        }

        return binding.root
    }

    @SuppressLint("CheckResult")
    private val nextBtnOnClickListener = View.OnClickListener {
        val p = PreferenceManager.getDefaultSharedPreferences(this.requireContext())
        val editor = p.edit()
        val activity = requireActivity() as UserProfileActivity
        vm.register(activity.vm.registrationForm.value!!).subscribe {
            Log.d("registration status: ", it.toString())
            if (it) { // update database successfully
                editor.putBoolean(EditorKey.IS_UPDATE_PROFILE_SUCCESS.toString(), true)
                editor.commit()
            } else { // fail to update db
                // Keep the registration form in user preference
                editor.putBoolean(EditorKey.IS_UPDATE_PROFILE_SUCCESS.toString(), false)
                editor.commit()
            }
            navigateToSignUpCompletionScreen()
        }
    }

    private fun navigateToSignUpCompletionScreen() {
        val transaction = requireActivity()
            .supportFragmentManager
            .beginTransaction()
        transaction.replace(
            R.id.fm_registration,
            SignUpCompletionFragment()
        )
        transaction.addToBackStack("SignUpCompletionFragment")
        transaction.commit()
    }
}