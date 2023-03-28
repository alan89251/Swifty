package com.team2.handiwork.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.team2.handiwork.R
import com.team2.handiwork.databinding.FragmentAgentUpdateSubscriptionLocationBinding
import com.team2.handiwork.firebase.firestore.Firestore
import com.team2.handiwork.models.User
import com.team2.handiwork.singleton.UserData
import com.team2.handiwork.viewModel.ActivityRegistrationViewModel

private const val ARG_UPDATE_FORM = "updateForm"

class AgentUpdateSubscriptionLocationFragment : Fragment() {
    private lateinit var binding: FragmentAgentUpdateSubscriptionLocationBinding
    private lateinit var vm: ActivityRegistrationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = ActivityRegistrationViewModel()

        arguments?.let {
            vm.registrationForm.value = it.getSerializable(ARG_UPDATE_FORM) as User
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAgentUpdateSubscriptionLocationBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.form.vm = vm
        binding.form.lifecycleOwner = this

        binding.form.nextBtn.setOnClickListener(nextBtnOnClickListener)
        binding.form.skipBtn.setOnClickListener(skipBtnOnClickListener)

        vm.primaryButtonColor.value = R.color.dark_blue_100

        vm.deviceLocation.observe(requireActivity()) {
            vm.configMapContentByDeviceLocation(it)
        }
        vm.workerLocationMap.observe(requireActivity()) {
            vm.requireDeviceLocation(
                requireActivity()
                    .getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
            )
        }
        vm.workerPreferredMissionDistance.observe(requireActivity()) {
            vm.updateMapContent(it)
        }

        val distanceSpinnerAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.worker_preferred_mission_distance_choices,
            R.layout.layout_spinner_item_update_distance
        )
        distanceSpinnerAdapter.setDropDownViewResource(R.layout.layout_spinner_dropdown_item_distance)
        binding.form.workerPreferredMissionDistanceSpinner.adapter = distanceSpinnerAdapter

        binding.form.workerPreferredMissionDistanceSpinner.onItemSelectedListener =
            workerPreferredMissionDistanceSpinnerListener

        // require location permission if not grant
        if (!checkForLocationPermission()) {
            requireLocationPermission()
            return binding.root
        }

        loadWorkerLocationMap()

        return binding.root
    }

    private val workerPreferredMissionDistanceSpinnerListener =
        object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val selectedView = p1 as TextView?
                if (selectedView != null) {
                    vm.workerPreferredMissionDistance.value =
                        mapDistance(selectedView.text.toString())
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                return
            }

        }

    // map distance string to distance
    private fun mapDistance(distanceStr: String): Int {
        return distanceStr.removeSuffix("km").toInt()
    }

    private fun checkForLocationPermission(): Boolean {
        return (ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED)
    }

    private fun requireLocationPermission() {
        requestPermissions(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    // Permission Granted
                    loadWorkerLocationMap()
                } else {
                    // Permission denied
                    Toast.makeText(requireContext(), "User denied permission", Toast.LENGTH_SHORT)
                }
            }

            else -> { // Ignore all other requests
                super.onRequestPermissionsResult(
                    requestCode,
                    permissions,
                    grantResults
                )
                return
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun loadWorkerLocationMap() {
        // get the google map
        val workerLocationMapFragment: SupportMapFragment = childFragmentManager.findFragmentById(
            R.id.worker_location_map
        ) as SupportMapFragment
        workerLocationMapFragment.getMapAsync(OnMapReadyCallback {
            vm.workerLocationMap.value = it
        })
    }

    @SuppressLint("CheckResult")
    private val nextBtnOnClickListener = View.OnClickListener {
        if (!checkForLocationPermission()) {
            Toast.makeText(requireContext(), resources.getString(R.string.ask_for_location_permission), Toast.LENGTH_SHORT).show()
        }
        if (vm.deviceLocation.value == null) {
            Toast.makeText(requireContext(), "Your hasn't set your location!", Toast.LENGTH_SHORT).show()
            return@OnClickListener
        }
        if (vm.workerPreferredMissionDistance.value == null) {
            Toast.makeText(requireContext(), "Your hasn't set your distance!", Toast.LENGTH_SHORT).show()
            return@OnClickListener
        }

        UserData.currentUserData.locationLat = vm.deviceLocation.value!!.latitude
        UserData.currentUserData.locationLng = vm.deviceLocation.value!!.longitude
        UserData.currentUserData.distance = vm.workerPreferredMissionDistance.value!!

        // save to DB
        updateUser()
    }

    @SuppressLint("CheckResult")
    private fun updateUser() {
        // update memory
        UserData.currentUserData.serviceTypeList =
            vm.registrationForm.value!!.serviceTypeList.map { serviceType ->
                serviceType.subServiceTypeList.removeIf { !it.selected }
                serviceType
            }

        // update DB
        Firestore()
            .userCollection
            .updateUser(UserData.currentUserData, {
                navigateToAgentProfileFragment()
            })
    }

    private val skipBtnOnClickListener = View.OnClickListener {
        updateUser()
    }

    private fun navigateToAgentProfileFragment() {
        val action = AgentUpdateSubscriptionLocationFragmentDirections
            .actionAgentUpdateSubscriptionLocationFragmentToMyProfileFragment()
        findNavController().navigate(action)
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param updateForm Parameter 1.
         * @return A new instance of fragment AgentUpdateSubscriptionLocationFragment
         */
        @JvmStatic
        fun newInstance(updateForm: User) =
            AgentUpdateSubscriptionLocationFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_UPDATE_FORM, updateForm)
                }
            }
    }
}