package com.team2.handiwork.fragments

import android.app.Activity.RESULT_OK
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.team2.handiwork.R
import com.team2.handiwork.adapter.MissionPhotosRecyclerViewAdapter
import com.team2.handiwork.databinding.FragmentCreateMissionDetailsBinding
import com.team2.handiwork.models.Mission
import com.team2.handiwork.uiComponents.CreateMissionStepper
import com.team2.handiwork.viewModel.FragmentCreateMissionDetailsViewModel
import com.team2.handiwork.viewModel.LayoutCreateMissionStepperViewModel
import java.util.*
import kotlin.collections.ArrayList

private const val ARG_MISSION = "mission"

class CreateMissionDetailsFragment : Fragment() {
    private lateinit var binding: FragmentCreateMissionDetailsBinding
    private lateinit var vm: FragmentCreateMissionDetailsViewModel
    private lateinit var createMissionStepper: CreateMissionStepper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = FragmentCreateMissionDetailsViewModel()

        arguments?.let {
            vm.mission = it.getSerializable(ARG_MISSION) as Mission
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ):  View? {
        binding = FragmentCreateMissionDetailsBinding.inflate(inflater, container, false)
        binding.vm = vm
        binding.lifecycleOwner = this

        binding.stepper.lifecycleOwner = this
        createMissionStepper = CreateMissionStepper(
            binding.stepper,
            LayoutCreateMissionStepperViewModel()
        )
        createMissionStepper.setCurrentStep(2)

        binding.tvCategoryContent.text = vm.mission.serviceType + " - " + vm.mission.subServiceType
        binding.btnStartTime.setOnClickListener(btnStartTimeOnClickListener)
        binding.btnEndTime.setOnClickListener(btnEndTimeOnClickListener)
        binding.btnCamera.setOnClickListener(btnCameraOnClickListener)
        binding.rvPhotos.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL,
            false)
        binding.btnNext.setOnClickListener(btnNextOnClickListener)

        vm.startDateTimeStr.observe(requireActivity()) {
            binding.btnStartTime.text = it
        }

        vm.endDateTimeStr.observe(requireActivity()) {
            binding.btnEndTime.text = it
        }

        vm.imageUriList.observe(requireActivity(), ::onUpdateMissionPhotosUI)

        // Inflate the layout for this fragment
        return binding.root
    }

    private val btnStartTimeOnClickListener = View.OnClickListener {
        // Get current date
        val calendar = Calendar.getInstance()

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            DatePickerDialog.OnDateSetListener { _, year, month, day ->
                vm.setStartDate(year, month, day)
                showStartTimePickerDialog()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private val btnEndTimeOnClickListener = View.OnClickListener {
        // Get current date
        val calendar = Calendar.getInstance()

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            DatePickerDialog.OnDateSetListener { _, year, month, day ->
                vm.setEndDate(year, month, day)
                showEndTimePickerDialog()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun showStartTimePickerDialog() {
        // Get current time
        val calendar = Calendar.getInstance()
        val timePickerDialog = TimePickerDialog(
            requireContext(),
            TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                vm.setStartTime(hour, minute)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false
        )
        timePickerDialog.show()
    }

    private fun showEndTimePickerDialog() {
        // Get current time
        val calendar = Calendar.getInstance()
        val timePickerDialog = TimePickerDialog(
            requireContext(),
            TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                vm.setEndTime(hour, minute)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false
        )
        timePickerDialog.show()
    }

    private val btnCameraOnClickListener = View.OnClickListener {
        val intent = Intent()
        intent.setType("image/*")
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(
            Intent.createChooser(
                intent,
                "Select Picture"
            ),
            PICK_IMAGE_MULTIPLE
        )
    }

    // The result of the user selected images
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode != PICK_IMAGE_MULTIPLE
            || resultCode != RESULT_OK
            || data == null) {
            return
        }

        val imageUriList = ArrayList<Uri>()
        if (data.clipData != null) { // Selected multiple images
            val clipData = data.clipData!!
            val itemCount = clipData.itemCount
            for (i in 0 until itemCount) {
                val imageUri = clipData.getItemAt(i).uri
                imageUriList.add(imageUri)
            }
        }
        else { // selected single image
            val imageUri = data.data!!
            imageUriList.add(imageUri)
        }

        vm.imageUriList.value = imageUriList
    }

    private fun onUpdateMissionPhotosUI(imageUriList: ArrayList<Uri>) {
        val adapter = MissionPhotosRecyclerViewAdapter(
            imageUriList,
            ::onRemoveMissionPhoto
        )
        binding.rvPhotos.swapAdapter(adapter, false)
    }

    private fun onRemoveMissionPhoto(position: Int) {
        vm.imageUriList.value!!.removeAt(position)
        binding.rvPhotos.adapter!!.notifyItemRemoved(position)
        binding.rvPhotos.adapter!!.notifyDataSetChanged()
    }

    private val btnNextOnClickListener = View.OnClickListener {
        // save user input to model
        vm.mission.startTime = vm.startDateTime.value!!.time.time
        vm.mission.endTime = vm.endDateTime.value!!.time.time
        vm.mission.location = binding.etLocation.text.toString()
        vm.setMissionPhotos(loadPhotos(vm.imageUriList.value!!))
        vm.mission.description = binding.textAreaInformation.text.toString()

        val action =
            CreateMissionDetailsFragmentDirections
                .actionCreateMissionDetailsFragmentToCreateMissionPriceFragment(
                    vm.mission
                )
        findNavController().navigate(action)
    }

    private fun loadPhotos(imageUriList: ArrayList<Uri>): ArrayList<Bitmap> {
        val list = ArrayList<Bitmap>()
        for (imageUri in imageUriList) {
            val bitmap = MediaStore.Images.Media.getBitmap(
                requireActivity().contentResolver,
                imageUri
            )
            list.add(bitmap)
        }
        return list
    }

    companion object {
        private const val PICK_IMAGE_MULTIPLE = 1

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param mission Parameter 1.
         * @return A new instance of fragment CreateMissionDetailsFragment
         */
        @JvmStatic
        fun newInstance(mission: Mission) =
            CreateMissionDetailsFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_MISSION, mission)
                }
            }
    }
}