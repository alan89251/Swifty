package com.team2.handiwork.fragments

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
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
        binding.btnAbort.setOnClickListener(btnAbortOnClickListener)

        vm.startDateTimeStr.observe(requireActivity()) {
            binding.btnStartTime.text = it
        }

        vm.endDateTimeStr.observe(requireActivity()) {
            binding.btnEndTime.text = it
        }

        vm.imageUriList.observe(requireActivity(), ::onUpdateMissionPhotosUI)
        binding.textAreaInformation.addTextChangedListener {
            vm.description.value = it?.toString()
        }

        // Initialize the AutocompleteSupportFragment.
        Places.initialize(
            requireActivity().applicationContext,
            resources.getString(R.string.google_map_api_key)
        )
        val autocompleteFragmentLocation = childFragmentManager
            .findFragmentById(R.id.autocomplete_fragment_location) as AutocompleteSupportFragment
        // Specify the types of place data to return.
        autocompleteFragmentLocation.setPlaceFields(
            listOf(
                Place.Field.ADDRESS,
                Place.Field.LAT_LNG
            )
        )
        autocompleteFragmentLocation.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                val autocompleteEditTextLocation = autocompleteFragmentLocation
                    .requireView()
                    .findViewById<EditText>(com.google.android.libraries.places.R.id.places_autocomplete_search_input)
                autocompleteEditTextLocation.isSingleLine = false
                autocompleteEditTextLocation.layoutParams.height =
                    resources.getDimensionPixelSize(R.dimen.auto_complete_location_height)
                autocompleteFragmentLocation.setHint(place.address)
                vm.location.value = place.address
                vm.locationLat.value = place.latLng!!.latitude
                vm.locationLng.value = place.latLng!!.longitude
            }

            override fun onError(status: Status) {
                Log.d("Create Mission", "Error in place autocomplete: $status")
            }
        })

        // Inflate the layout for this fragment
        return binding.root
    }

    private val btnAbortOnClickListener = View.OnClickListener {
        AlertDialog.Builder(requireContext())
            .setTitle(resources.getString(R.string.abort_create_mission_alert_title))
            .setMessage(resources.getString(R.string.abort_create_mission_alert_msg))
            .setPositiveButton("Confirm") { _, _ ->
                navigateToHomeFragment()
            }
            .setNegativeButton("Back", null)
            .show()
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
        saveUserInputToModel()

        val action =
            CreateMissionDetailsFragmentDirections
                .actionCreateMissionDetailsFragmentToCreateMissionPriceFragment(
                    vm.mission
                )
        findNavController().navigate(action)
    }

    private fun saveUserInputToModel() {
        vm.mission.startTime = vm.startDateTime.value!!.timeInMillis
        vm.mission.endTime = vm.endDateTime.value!!.timeInMillis
        vm.mission.location = vm.location.value!!
        vm.mission.latitude = vm.locationLat.value!!
        vm.mission.longitude = vm.locationLng.value!!
        // mission photo is optional
        if (vm.imageUriList.value != null) {
            vm.mission.missionPhotoUris = vm.imageUriList.value!!
        }
        vm.mission.description = vm.description.value!!
    }

    private fun navigateToHomeFragment() {
        val action = CreateMissionDetailsFragmentDirections
            .actionCreateMissionDetailsFragmentToHomeFragment()
        findNavController().navigate(action)
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