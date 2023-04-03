package com.team2.handiwork.fragments.profile

import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.team2.handiwork.AppConst
import com.team2.handiwork.R
import com.team2.handiwork.adapter.CertificateRecyclerViewAdapter
import com.team2.handiwork.adapter.MyMissionsRecyclerViewAdapter
import com.team2.handiwork.databinding.FragmentViewProfileBinding
import com.team2.handiwork.databinding.LayoutViewCertificateDialogBinding
import com.team2.handiwork.firebase.Storage
import com.team2.handiwork.models.Certification
import com.team2.handiwork.models.Comment
import com.team2.handiwork.models.CommentList
import com.team2.handiwork.models.Mission
import com.team2.handiwork.singleton.UserData
import com.team2.handiwork.utilities.Ext.Companion.disposedBy
import com.team2.handiwork.utilities.SpacingItemDecorator
import com.team2.handiwork.viewModel.profile.FragmentViewProfileViewModel

class ViewProfileFragment : BaseProfileFragment<FragmentViewProfileViewModel>() {
    override var vm = FragmentViewProfileViewModel()
    private lateinit var binding: FragmentViewProfileBinding
    private lateinit var certificateRVAdapter: CertificateRecyclerViewAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentViewProfileBinding.inflate(inflater, container, false)
        email = requireArguments().getString("targetEmail") as String
        super.onCreateView(inflater, container, savedInstanceState)

        binding.lifecycleOwner = this
        binding.vm = vm

        loadIcon(binding)

        binding.layoutBasicInfo.btnEdit.visibility = View.GONE

        vm.getMissions(email).subscribe {
            vm.missions.value = it
        }.disposedBy(disposeBag)

        vm.missions.observe(viewLifecycleOwner) {
            val missionAdapter = MyMissionsRecyclerViewAdapter(changeDrawableColor, onMissionClick)
            binding.rvMission.layoutManager = GridLayoutManager(requireContext(), 2)
            missionAdapter.setList(it)
            binding.rvMission.adapter = missionAdapter
        }

        // set view invisibility
        binding.layoutBasicInfo.lblPhone.text = "Phone number verified"
        binding.layoutBasicInfo.lblEmail.visibility = View.GONE
        binding.layoutBasicInfo.tvEmail.visibility = View.GONE
        binding.layoutBasicInfo.tvPhone.visibility = View.GONE

        vm.comments.observe(viewLifecycleOwner) { comments ->
            binding.layoutComment.rvComment.adapter = commentAdapter
            commentAdapter.comments = comments
            binding.layoutComment.btnSelect.setOnClickListener { navToViewOtherCommentFragment(comments) }

            if (comments.isEmpty()) {
                binding.layoutComment.root.visibility = View.GONE
                binding.layoutRating.root.visibility = View.GONE
                binding.layoutNoRating.root.visibility = View.VISIBLE
            }
        }

        vm.user.observe(viewLifecycleOwner) {
            (activity as AppCompatActivity?)!!
                .supportActionBar!!.title = "${it.firstName}'s Profile"

            binding.tvMissionTitle.text = "${it.firstName}'s Mission"
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

        dialogBinding.ibtnDeleteCert.visibility = View.GONE

        dialogBinding.dialogViewBack.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun loadIcon(binding: FragmentViewProfileBinding) {
        val imgUrl = requireArguments().getString("targetIconURL")
        if (imgUrl != "" && imgUrl != null) {
            Log.d("hehehe", "$imgUrl")
            Glide.with(this)
                .load(imgUrl)
                .into(binding.layoutBasicInfo.ivUser)
        } else {
            loadAgentIcon(email)
        }
    }

    private val onIconLoaded: (mission: String) -> Unit = { imgUrl ->
        Glide.with(this)
            .load(imgUrl)
            .into(binding.layoutBasicInfo.ivUser)
    }

    private val onIconLoadFailed: () -> Unit = {
    }

    private fun loadAgentIcon(agentEmail: String) {
        Storage().getImgUrl("User/$agentEmail", onIconLoaded, onIconLoadFailed)
    }

    private val changeDrawableColor: (textView: TextView, mission: Mission) -> Unit =
        { textView, mission ->
            val backgroundDrawable = GradientDrawable()
            backgroundDrawable.shape = GradientDrawable.RECTANGLE
            val cornerRadius = 20.0f
            backgroundDrawable.cornerRadius = cornerRadius
            backgroundDrawable.setColor(
                ContextCompat.getColor(
                    requireContext(),
                    vm.convertStatusColor(mission.status)
                )
            )
            textView.background = backgroundDrawable
        }

    private val onMissionClick: (mission: Mission) -> Unit = {
        val bundle: Bundle = Bundle()
        bundle.putSerializable("mission", it)
        findNavController().navigate(
            R.id.action_viewProfileFragment_to_agentMissionDetailFragment,
            bundle
        )
    }

    private fun navToViewOtherCommentFragment(comments: List<Comment>) {
        val action = ViewProfileFragmentDirections
            .actionViewProfileFragmentToViewOtherCommentFragment(
                UserData.currentUserData,
                CommentList(comments)
            )
        findNavController().navigate(action)
    }
}