package com.team2.handiwork.fragments.profile

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.preference.PreferenceManager
import com.bumptech.glide.Glide
import com.team2.handiwork.AppConst
import com.team2.handiwork.R
import com.team2.handiwork.databinding.FragmentUpdateProfileBinding
import com.team2.handiwork.firebase.Storage
import com.team2.handiwork.models.Mission
import com.team2.handiwork.models.User
import com.team2.handiwork.singleton.UserData
import com.team2.handiwork.viewModel.ActivityHomeViewModel
import com.team2.handiwork.viewModel.profile.FragmentUpdateProfileViewModel


class UpdateProfileFragment : Fragment() {

    val args: UpdateProfileFragmentArgs by navArgs()
    private val homeActivityVm: ActivityHomeViewModel by activityViewModels()
    private lateinit var binding: FragmentUpdateProfileBinding
    lateinit var vm: FragmentUpdateProfileViewModel
    var email = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentUpdateProfileBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        val pref = PreferenceManager.getDefaultSharedPreferences(this.requireContext())
        email = pref.getString(AppConst.EMAIL, "")!!
        vm = FragmentUpdateProfileViewModel()
        binding.vm = vm
        vm.setUser(args.user)

        loadIcon()
        binding.btnSave.setOnClickListener {
            // todo upload new img first than update the user
            //vm.updateUser(updateFinishCallback)
            vm.uploadUserNew(updateFinishCallback, onIconLoaded, onIconLoadFailed)
        }

        binding.ibtnPersonalInfoCamera.setOnClickListener {
            val photoIntent = Intent(Intent.ACTION_PICK)
            photoIntent.type = "image/*"
            startActivityForResult(photoIntent, 500)
        }

        return binding.root
    }

    private fun loadIcon() {
        Glide.with(this)
            .load(UserData.currentUserData.imageURi)
            .into(binding.ivPersonInfoIcon)
    }

    private val updateFinishCallback: (user: User) -> Unit = {
        vm.newImageUrl.value = null
        vm.fsImgUrl.value = ""
        findNavController().navigate(R.id.action_updateProfileFragment_to_myProfileFragment)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 500) {
            val selectedImageUri = data?.data
            vm.newImageUrl.value = selectedImageUri
            Glide.with(this)
                .load(selectedImageUri)
                .into(binding.ivPersonInfoIcon)
            binding.ivPersonInfoIcon.visibility = View.VISIBLE
        }
    }

    private val onIconLoaded: (mission: String) -> Unit = { imgUrl ->
        vm.fsImgUrl.value = imgUrl
        vm.updateUser(updateFinishCallback)
    }

    private val onIconLoadFailed: () -> Unit = {
//        val pref = PreferenceManager.getDefaultSharedPreferences(requireContext())
//        val editor: SharedPreferences.Editor = pref.edit()
//        editor.putString(AppConst.PREF_USER_ICON_URL, "")
//        editor.apply()
    }
}