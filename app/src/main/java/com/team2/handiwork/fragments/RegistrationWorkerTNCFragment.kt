package com.team2.handiwork.fragments

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        binding.nextBtn.setOnClickListener(nextBtnOnClickListener)
        binding.backBtn.setOnClickListener(backBtnOnClickListener)

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
        binding.userAgreementSwitch.setOnCheckedChangeListener { compoundButton, b ->
            vm.isEnableNextBtn.value = b
        }

        return binding.root
    }

    private val nextBtnOnClickListener = View.OnClickListener {
        val p = PreferenceManager.getDefaultSharedPreferences(this.requireContext())
        val editor = p.edit()
        val activity = requireActivity() as UserProfileActivity
        vm.register(activity.getUserRegistrationForm()).subscribe {
            Log.d("registration status: ", it.toString())
            if (it) { // update database successfully
                editor.remove(EditorKey.USER_FORM.toString())
                editor.putBoolean(EditorKey.IS_UPDATE_PROFILE_SUCCESS.toString(), true)
                editor.commit()
            }
            else { // fail to update db
                // Keep the registration form in user preference
                editor.putBoolean(EditorKey.IS_UPDATE_PROFILE_SUCCESS.toString(), false)
                editor.commit()
            }
            navigateToSignUpCompletionScreen()
        }
    }

    private val backBtnOnClickListener = View.OnClickListener {
        requireActivity()
            .supportFragmentManager
            .beginTransaction()
            .replace(R.id.fm_registration, RegistrationWorkerProfileFragment())
            .commit()
    }

    private fun navigateToSignUpCompletionScreen() {
        requireActivity()
            .supportFragmentManager
            .beginTransaction()
            .replace(R.id.fm_registration, SignUpCompletionFragment())
            .commit()
    }
}