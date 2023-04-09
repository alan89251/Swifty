package com.team2.handiwork.fragments.profile

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.team2.handiwork.AppConst
import com.team2.handiwork.R
import com.team2.handiwork.adapter.CertificateRecyclerViewAdapter
import com.team2.handiwork.adapter.HomeMissionRecyclerViewAdapter
import com.team2.handiwork.databinding.CustomServiceTypeDialogBinding
import com.team2.handiwork.databinding.FragmentMyProfileBinding
import com.team2.handiwork.databinding.LayoutUploadCertificationDialogBinding
import com.team2.handiwork.databinding.LayoutViewCertificateDialogBinding
import com.team2.handiwork.models.Certification
import com.team2.handiwork.models.Comment
import com.team2.handiwork.models.CommentList
import com.team2.handiwork.singleton.UserData
import com.team2.handiwork.utilities.SpacingItemDecorator
import com.team2.handiwork.viewModel.profile.FragmentMyProfileViewModel

class MyProfileFragment : BaseProfileFragment<FragmentMyProfileViewModel>() {
    override var vm = FragmentMyProfileViewModel()

    private lateinit var binding: FragmentMyProfileBinding
    private lateinit var dialogImageView: ShapeableImageView
    private lateinit var certificateRVAdapter: CertificateRecyclerViewAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        email = UserData.currentUserData.email
        super.onCreateView(inflater, container, savedInstanceState)

        binding = FragmentMyProfileBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.vm = vm

        val pref = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val currentTheme = pref.getInt(AppConst.CURRENT_THEME, 0)
        // currentTheme 1 = employer
        val isAgent = currentTheme == 0

        vm.comments.observe(viewLifecycleOwner) { comments ->
            binding.layoutComment.rvComment.adapter = commentAdapter
            commentAdapter.comments = comments
            binding.layoutComment.btnSelect.setOnClickListener { navToViewOtherCommentFragment(comments) }

            if (comments.isEmpty()) {
                binding.layoutComment.root.visibility = View.GONE
            } else {
                binding.layoutComment.root.visibility = View.VISIBLE
            }
        }

        initCertificateRecyclerView()
        vm.certifications.observe(viewLifecycleOwner) { certs ->
            if (certs.isEmpty()) {
                vm.showAddCertification.value = View.VISIBLE
                vm.showCertification.value = View.GONE
            } else {
                vm.showAddCertification.value = View.GONE
                vm.showCertification.value = View.VISIBLE
                certificateRVAdapter.setList(certs)
            }
        }

        binding.layoutCertification.updateCertBtn.setOnClickListener {
            showUploadCertDialog()
        }
        binding.layoutCertification.addCertBtn.setOnClickListener {
            showUploadCertDialog()
        }


        vm.typeList.observe(viewLifecycleOwner) {
            if (!isAgent) return@observe
            val distance = UserData.currentUserData.distance

            binding.subscription.visibility = View.VISIBLE
            if (it.isEmpty() && distance != 0) {
                binding.layoutAgentSubscriptions.root.visibility = View.VISIBLE
                binding.layoutAgentSubscriptions.cancelServiceButton.visibility = View.GONE
                binding.layoutAgentSubscriptionsEmpty.root.visibility = View.GONE
            } else if (it.isEmpty()) {
                binding.layoutAgentSubscriptionsEmpty.root.visibility = View.VISIBLE
                binding.layoutAgentSubscriptions.root.visibility = View.GONE
                binding.layoutAgentSubscriptions.cancelServiceButton.visibility = View.GONE
            } else {
                binding.layoutAgentSubscriptions.root.visibility = View.VISIBLE
                binding.layoutAgentSubscriptions.tvSubsServiceType.visibility = View.VISIBLE
                binding.layoutAgentSubscriptionsEmpty.root.visibility = View.GONE
                binding.layoutAgentSubscriptions.cancelServiceButton.visibility = View.VISIBLE

                val count = it.size
                val desc = if (count >= 3) {
                    "${it[0]}, ${it[1]}, <u>and ${count - 2} more</u>"
                } else if (count == 2) {
                    "${it[0]}, ${it[1]}"
                } else if (count == 1) {
                    it[0]
                } else {
                    ""
                }
                binding.layoutAgentSubscriptions.tvSubsServiceType.text = Html.fromHtml(desc)

                if (count >= 3) {
                    binding.layoutAgentSubscriptions.tvSubsServiceType.setOnClickListener { view ->
                        showServiceTypeDialog(it)
                    }
                }
            }
        }

        vm.user.observe(viewLifecycleOwner) {
            if (it.distance == 0) {
                binding.layoutAgentSubscriptions.tvSubsDistancePlace.text = "No Distance Filter"
                binding.layoutAgentSubscriptions.cancelDistanceButton.visibility = View.GONE
            } else {
                val place = "Within ${it.distance} km"
                binding.layoutAgentSubscriptions.cancelDistanceButton.visibility = View.VISIBLE
                binding.layoutAgentSubscriptions.tvSubsDistancePlace.text = place
            }
            loadIcon()
        }


        binding.btnViewMission.setOnClickListener {
            findNavController().navigate(
                R.id.action_myProfileFragment_to_myMissionsFragment,
            )
        }

        binding.layoutAgentSubscriptionsEmpty.btnUpdateMissionSub.setOnClickListener {
            findNavController()
                .navigate(
                    MyProfileFragmentDirections
                        .actionMyProfileFragmentToAgentUpdateSubscriptionServiceTypeFragment()
                )
        }

        binding.layoutAgentSubscriptions.btnEdit.setOnClickListener {
            findNavController()
                .navigate(
                    MyProfileFragmentDirections
                        .actionMyProfileFragmentToAgentUpdateSubscriptionServiceTypeFragment()
                )
        }

        binding.layoutBasicInfo.btnEdit.setOnClickListener {
            val action =
                MyProfileFragmentDirections.actionMyProfileFragmentToUpdateProfileFragment(UserData.currentUserData)
            findNavController().navigate(action)
        }

        binding.layoutAgentSubscriptions.cancelDistanceButton.setOnClickListener {
            vm.cancelDistanceSubscription()
        }

        binding.layoutAgentSubscriptions.cancelServiceButton.setOnClickListener {
            vm.cancelServiceTypeSubscription()
        }

        return binding.root
    }

    private fun initCertificateRecyclerView() {
        binding.layoutCertification.certRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val itemDecorator = SpacingItemDecorator(10)
        binding.layoutCertification.certRecyclerView.addItemDecoration(itemDecorator)
        certificateRVAdapter = CertificateRecyclerViewAdapter(requireContext(), certOnClickListener)
        binding.layoutCertification.certRecyclerView.adapter = certificateRVAdapter
    }

    private val certOnClickListener: (Certification) -> Unit = {
        showViewCertDialog(it)
    }

    private fun loadIcon() {
//        val pref = PreferenceManager.getDefaultSharedPreferences(requireContext())
//        val imgUrl = pref.getString(AppConst.PREF_USER_ICON_URL, "")
//        if (imgUrl != "") {
            Glide.with(this)
                .load(UserData.currentUserData.imageURi)
                .into(binding.layoutBasicInfo.ivUser)
//        }
    }

    private fun showServiceTypeDialog(serviceType: List<String>) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)

        val dialogBinding: CustomServiceTypeDialogBinding = DataBindingUtil.inflate(
            LayoutInflater.from(requireContext()),
            R.layout.custom_service_type_dialog,
            null,
            false
        )

        dialog.setContentView(dialogBinding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))

        val adapter = ArrayAdapter<String>(requireContext(), R.layout.service_type_dialog_list_item, serviceType)
        dialogBinding.dialogList.adapter = adapter

        dialogBinding.dialogBack.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showViewCertDialog(certification: Certification) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)

        val dialogBinding: LayoutViewCertificateDialogBinding = DataBindingUtil.inflate(
            LayoutInflater.from(requireContext()),
            R.layout.layout_view_certificate_dialog,
            null,
            false
        )
        dialogBinding.cert = certification
        dialog.setContentView(dialogBinding.root)
        //dialog.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))

        Glide.with(requireContext())
            .load(certification.imgUrl)
            .into(dialogBinding.ivCertImg)

        dialogBinding.ivCertImg.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(Uri.parse(certification.imgUrl), "image/*")
            startActivity(intent)
        }

        dialogBinding.ibtnDeleteCert.setOnClickListener {

            val finishDeleteCallback: () -> Unit = {
                Toast.makeText(requireContext(), "Finished Delete Certification", Toast.LENGTH_SHORT).show()
                vm.getCertifications(email)
                dialog.dismiss()
            }
            vm.deleteCertificate(email, certification, finishDeleteCallback)
        }

        dialogBinding.dialogViewBack.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showUploadCertDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)

        val dialogBinding: LayoutUploadCertificationDialogBinding = DataBindingUtil.inflate(
            LayoutInflater.from(requireContext()),
            R.layout.layout_upload_certification_dialog,
            null,
            false
        )
        dialogBinding.vm = vm
        dialog.setContentView(dialogBinding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))

        dialogBinding.ibtnSelectCert.setOnClickListener {
            val photoIntent = Intent(Intent.ACTION_PICK)
            photoIntent.type = "image/*"
            startActivityForResult(photoIntent, 500)
        }
        dialogImageView = dialogBinding.ivCertImg


        dialogBinding.dialogUpload.setOnClickListener {
            if (vm.newCertName.value!!.isEmpty()) {
                dialogBinding.etCertName.error = "Please enter cert name"
                dialogBinding.etCertName.requestFocus()
                return@setOnClickListener
            }
            if (vm.newImageUrl.value != null) {

                val finishUploadCallback: () -> Unit = {
                    Toast.makeText(requireContext(), "Finished Upload Certification", Toast.LENGTH_SHORT).show()
                    vm.getCertifications(email)
                    dialog.dismiss()
                }

                vm.uploadCertificate(email, finishUploadCallback)

            } else {
                Toast.makeText(requireContext(), "Please select cert image", Toast.LENGTH_SHORT).show()
            }
        }

        dialogBinding.dialogUploadBack.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 500) {
            val selectedImageUri = data?.data
            vm.newImageUrl.value = selectedImageUri
            val selectedImageStream =
                requireActivity().contentResolver.openInputStream(selectedImageUri!!)
            val selectedImageBitmap = BitmapFactory.decodeStream(selectedImageStream)
            dialogImageView.setImageBitmap(selectedImageBitmap)
        }
    }

    private fun navToViewOtherCommentFragment(comments: List<Comment>) {
        val action = MyProfileFragmentDirections
            .actionMyProfileFragmentToViewOtherCommentFragment(
                UserData.currentUserData,
                CommentList(comments)
            )
        findNavController().navigate(action)
    }
}