package com.team2.handiwork.fragments.registration

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
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.team2.handiwork.R
import com.team2.handiwork.activity.UserProfileActivity
import com.team2.handiwork.base.fragment.BaseFragmentActivity
import com.team2.handiwork.databinding.FragmentRegistrationWorkerProfileBinding
import com.team2.handiwork.viewModel.ActivityRegistrationViewModel

class RegistrationWorkerProfileFragment : BaseFragmentActivity() {
    private lateinit var vm: ActivityRegistrationViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentRegistrationWorkerProfileBinding.inflate(inflater, container, false)
        val view = binding.root
        val fragmentActivity = requireActivity() as UserProfileActivity

        vm = fragmentActivity.vm
        binding.vm = vm
        binding.lifecycleOwner = this

        // configure UIs
        fragmentActivity.binding.vm!!.currentStep.value = 2
        fragmentActivity.setActionBarTitle("My preferred location:")

        binding.nextBtn.setOnClickListener(nextBtnOnClickListener)
        binding.skipBtn.setOnClickListener(skipBtnOnClickListener)

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

        val distanceSpinnerAdapter = ArrayAdapter.createFromResource(requireContext(),
            R.array.worker_preferred_mission_distance_choices,
            R.layout.layout_spinner_item_distance)
        distanceSpinnerAdapter.setDropDownViewResource(R.layout.layout_spinner_dropdown_item_distance)
        binding.workerPreferredMissionDistanceSpinner.adapter = distanceSpinnerAdapter
        binding.workerPreferredMissionDistanceSpinner.onItemSelectedListener =
            workerPreferredMissionDistanceSpinnerListener

        // require location permission if not grant
        if (!checkForLocationPermission()) {
            requireLocationPermission()
            return view
        }

        loadWorkerLocationMap()

        return view
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

    private val nextBtnOnClickListener = View.OnClickListener {
        val fragmentActivity = requireActivity() as UserProfileActivity
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
        fragmentActivity.vm.registrationForm.value!!.locationLat =
            vm.deviceLocation.value!!.latitude
        fragmentActivity.vm.registrationForm.value!!.locationLng =
            vm.deviceLocation.value!!.longitude
        fragmentActivity.vm.registrationForm.value!!.distance =
            vm.workerPreferredMissionDistance.value!!

        navigateToRegistrationWorkerTNCScreen()
    }

    private val skipBtnOnClickListener = View.OnClickListener {
        navigateToRegistrationWorkerTNCScreen()
    }

    private fun navigateToRegistrationWorkerTNCScreen() {
        // clear map
        if (vm.workerLocationMap.value != null) vm.workerLocationMap.value!!.clear()
        this.navigate(
            R.id.fm_registration,
            RegistrationWorkerTNCFragment(),
            "RegistrationWorkerTNCFragment",
        )
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}