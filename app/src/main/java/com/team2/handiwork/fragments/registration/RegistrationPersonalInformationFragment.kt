package com.team2.handiwork.fragments.registration

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.preference.PreferenceManager
import com.team2.handiwork.AppConst
import com.team2.handiwork.R
import com.team2.handiwork.activity.UserProfileActivity
import com.team2.handiwork.base.BaseFragmentActivity
import com.team2.handiwork.databinding.FragmentRegistrationPersonalInformationBinding
import com.team2.handiwork.firebase.Storage

class RegistrationPersonalInformationFragment : BaseFragmentActivity() {
    lateinit var binding: FragmentRegistrationPersonalInformationBinding
    var email = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val binding = FragmentRegistrationPersonalInformationBinding.inflate(
            inflater, container, false
        )
        val fragmentActivity = requireActivity() as UserProfileActivity

        val pref = PreferenceManager.getDefaultSharedPreferences(this.requireContext())
        val vm = fragmentActivity.vm

        email = pref.getString(AppConst.EMAIL, "")!!
        binding.vm = fragmentActivity.vm
        binding.lifecycleOwner = this
        this.binding = binding
        fragmentActivity.binding.vm!!.currentStep.value = 1

        fragmentActivity.vm.registrationForm.observe(this.viewLifecycleOwner) {
            vm.form.value = it
        }

        binding.btnSendMsg.setOnClickListener {
            vm.verifyMsg.value = "sent to ${vm.phoneNumber.value}"
            fragmentActivity.vm.registrationForm.value!!.phoneVerify = true
            Toast.makeText(context, "Verification message is sent", Toast.LENGTH_LONG).show()
        }

        binding.btnNext.setOnClickListener {
            fragmentActivity.vm.registrationForm.value!!.imageURi =
                "User/${fragmentActivity.vm.registrationForm.value!!.email}"
            fragmentActivity.vm.registrationForm.value!!.firstName = vm.firstName.value!!
            fragmentActivity.vm.registrationForm.value!!.lastName = vm.lastName.value!!
            fragmentActivity.vm.registrationForm.value!!.phoneNumber = vm.phoneNumber.value!!

            this.navigate(
                R.id.fm_registration,
                RegistrationChooseRoleFragment(),
                "RegistrationChooseRoleFragment"
            )
        }

        fragmentActivity.setActionBarTitle("Personal Information")

        binding.ibtnPersonalInfoCamera.setOnClickListener {
            val photoIntent = Intent(Intent.ACTION_PICK)
            photoIntent.type = "image/*"
            startActivityForResult(photoIntent, 500)
        }
        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 500) {
            val selectedImageUri = data?.data
            val selectedImageStream =
                requireActivity().contentResolver.openInputStream(selectedImageUri!!)
            val selectedImageBitmap = BitmapFactory.decodeStream(selectedImageStream)
            binding.ivPersonInfoIcon.setImageBitmap(selectedImageBitmap)
            binding.ivPersonInfoIcon.visibility = View.VISIBLE
            // todo set userID from sharepreference
            Storage().uploadImg("User", email, selectedImageUri).subscribe {
                if (it) {
                    Toast.makeText(context, "Upload Success!", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Upload Failed!", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}