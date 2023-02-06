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
import com.team2.handiwork.databinding.FragmentRegistrationPersonalInformationBinding
import com.team2.handiwork.firebase.Storage
import com.team2.handiwork.viewModel.FragmentRegistrationPersonalInformationViewModel

class RegistrationPersonalInformationFragment : Fragment() {
    lateinit var binding: FragmentRegistrationPersonalInformationBinding;
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

        binding.btnSendMsg.setOnClickListener {
            vm.verifyMsg.value = "sent to ${vm.phoneNumber.value}"
            Toast.makeText(context, "Verification message is sent", Toast.LENGTH_LONG).show();
        }

        binding.btnNext.setOnClickListener {
            // todo verify input
        }

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
            // todo set userID from sharepreference
            Storage().uploadImg("User", "userId", selectedImageUri).subscribe {
                    if (it) {
                        Toast.makeText(context, "Upload Success!", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(context, "Upload Failed!", Toast.LENGTH_LONG).show();
                    }
                }
        }
    }
}