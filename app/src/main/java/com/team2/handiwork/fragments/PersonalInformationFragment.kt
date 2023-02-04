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
import com.team2.handiwork.databinding.FragmentPersonalInformationBinding
import com.team2.handiwork.firebase.Storage
import com.team2.handiwork.viewModel.FragmentPersonalInformationViewModel

class PersonalInformationFragment : Fragment() {
    lateinit var binding: FragmentPersonalInformationBinding;
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentPersonalInformationBinding.inflate(
            inflater,
            container,
            false
        )
        this.binding = binding
        binding.vm = FragmentPersonalInformationViewModel()
        binding.lifecycleOwner = this

        binding.btnSendMsg.setOnClickListener {
            // do nth
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
            Storage()
                .uploadImg("User", "userId", selectedImageUri)
                .subscribe {
                    if (it) {
                        Toast.makeText(context, "Upload Success!\n", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(context, "Upload Failed!\n", Toast.LENGTH_LONG).show();
                    }
                }
        }
    }
}