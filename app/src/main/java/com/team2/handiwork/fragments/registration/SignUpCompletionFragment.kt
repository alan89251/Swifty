package com.team2.handiwork.fragments.registration

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.team2.handiwork.HomeActivity
import com.team2.handiwork.R
import com.team2.handiwork.activity.UserProfileActivity
import com.team2.handiwork.databinding.FragmentSignUpCompletionBinding
import com.team2.handiwork.enums.EditorKey
import com.team2.handiwork.viewModel.FragmentSignUpCompletionViewModel

class SignUpCompletionFragment : Fragment() {
    private lateinit var binding: FragmentSignUpCompletionBinding
    private lateinit var vm: FragmentSignUpCompletionViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpCompletionBinding.inflate(inflater, container, false)
        vm = FragmentSignUpCompletionViewModel()
        binding.vm = vm
        binding.lifecycleOwner = this

        // remove back button in navigation bar
        (requireActivity() as AppCompatActivity)
            .supportActionBar!!
            .setDisplayHomeAsUpEnabled(false)

        // change title
        val activity = requireActivity() as UserProfileActivity
        activity.setActionBarTitle("Registration Completed")

        // config UIs
        activity.binding.vm!!.currentStep.value = 4
        binding.navBtn.setOnClickListener(navBtnOnClickListener)
        val sp = PreferenceManager.getDefaultSharedPreferences(requireContext())
        vm.isMissionSuccess.value =
            sp.getBoolean(EditorKey.IS_UPDATE_PROFILE_SUCCESS.toString(), false)

        return binding.root
    }

    private val navBtnOnClickListener = View.OnClickListener {
        if (vm.isMissionSuccess.value == null) {
            return@OnClickListener
        }
        if (vm.isMissionSuccess.value!!) {
            navigateToHomeScreen()
        } else {
            backToRegistrationWorkerTNCScreen()
        }
    }

    private fun navigateToHomeScreen() {
        // display back button in navigation bar
        (requireActivity() as AppCompatActivity)
            .supportActionBar!!
            .setDisplayHomeAsUpEnabled(true)

        val intent = Intent(requireContext(), HomeActivity::class.java)
        startActivity(intent)
    }

    private fun backToRegistrationWorkerTNCScreen() {
        // display back button in navigation bar
        (requireActivity() as AppCompatActivity)
            .supportActionBar!!
            .setDisplayHomeAsUpEnabled(true)

        requireActivity()
            .supportFragmentManager
            .beginTransaction()
            .replace(R.id.fm_registration, RegistrationWorkerTNCFragment())
            .commit()
    }
}