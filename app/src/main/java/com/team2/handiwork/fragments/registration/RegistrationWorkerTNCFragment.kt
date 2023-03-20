package com.team2.handiwork.fragments.registration

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.preference.PreferenceManager
import com.team2.handiwork.AppConst
import com.team2.handiwork.R
import com.team2.handiwork.activity.UserProfileActivity
import com.team2.handiwork.base.fragment.BaseFragmentActivity
import com.team2.handiwork.databinding.FragmentRegistrationWorkerTNCBinding
import com.team2.handiwork.enums.EditorKey
import com.team2.handiwork.utilities.Ext.Companion.disposedBy
import com.team2.handiwork.utilities.Utility
import java.io.BufferedReader
import java.io.InputStreamReader

class RegistrationWorkerTNCFragment : BaseFragmentActivity() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentRegistrationWorkerTNCBinding.inflate(inflater, container, false)
        val fragmentActivity = requireActivity() as UserProfileActivity

        val vm = fragmentActivity.vm
        binding.vm = vm
        binding.lifecycleOwner = this

        // config UIs
        fragmentActivity.binding.vm!!.currentStep.value = 3
        fragmentActivity.setActionBarTitle("Terms and Conditions")

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
        val fragmentActivity = requireActivity() as UserProfileActivity
        val p = PreferenceManager.getDefaultSharedPreferences(this.requireContext())
        val editor = p.edit()
        fragmentActivity.vm.register(fragmentActivity.vm.registrationForm.value!!).subscribe {
            Log.d("registration status: ", it.toString())
            if (it) { // update database successfully
                editor.putBoolean(EditorKey.IS_UPDATE_PROFILE_SUCCESS.toString(), true)
                editor.commit()
            } else { // fail to update db
                // Keep the registration form in user preference
                editor.putBoolean(EditorKey.IS_UPDATE_PROFILE_SUCCESS.toString(), false)
                editor.commit()
            }

            if (fragmentActivity.vm.registrationForm.value!!.isEmployer) {
                Utility.setThemeToChange(Utility.THEME_EMPLOYER)
                editor.putInt(AppConst.CURRENT_THEME, 1)
            } else {
                Utility.setThemeToChange(Utility.THEME_AGENT)
                editor.putInt(AppConst.CURRENT_THEME, 0)
            }
            editor.commit()
            this.navigate(
                R.id.fm_registration,
                SignUpCompletionFragment(),
                "SignUpCompletionFragment"
            )
        }.disposedBy(disposeBag)
    }

}