package com.team2.handiwork.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.team2.handiwork.R
import com.team2.handiwork.activity.UserProfileActivity
import com.team2.handiwork.databinding.FragmentRegistrationPersonalInformationBinding
import com.team2.handiwork.firebase.Storage
import com.team2.handiwork.viewModel.FragmentRegistrationPersonalInformationViewModel

class RegistrationPersonalInformationFragment : Fragment() {
    lateinit var binding: FragmentRegistrationPersonalInformationBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val binding = FragmentRegistrationPersonalInformationBinding.inflate(
            inflater, container, false
        )
        val vm = FragmentRegistrationPersonalInformationViewModel()
        binding.vm = vm
        binding.lifecycleOwner = this
        this.binding = binding
        val activity = requireActivity() as UserProfileActivity
        activity.binding.vm!!.currentStep.value = 1

        binding.btnSendMsg.setOnClickListener {
            vm.verifyMsg.value = "sent to ${vm.phoneNumber.value}"
            activity.vm.registrationForm.value!!.phoneVerify = true
            Toast.makeText(context, "Verification message is sent", Toast.LENGTH_LONG).show()
        }

        binding.btnNext.setOnClickListener {
            activity.vm.registrationForm.value!!.imageURi =
                "User/${activity.vm.registrationForm.value!!.email}"
            activity.vm.registrationForm.value!!.firstName = vm.firstName.value!!
            activity.vm.registrationForm.value!!.lastName = vm.lastName.value!!
            activity.vm.registrationForm.value!!.phoneNumber = vm.phoneNumber.value!!

            val trans = activity
                .supportFragmentManager
                .beginTransaction()

            trans.replace(R.id.fm_registration, RegistrationChooseRoleFragment())
            trans.addToBackStack("RegistrationChooseRoleFragment")
            trans.commit()
        }

        activity.setActionBarTitle("Personal Information")

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
            Storage().uploadImg("User", "userId", selectedImageUri).subscribe {
                if (it) {
                    Toast.makeText(context, "Upload Success!", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Upload Failed!", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}