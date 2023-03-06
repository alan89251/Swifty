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
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.team2.handiwork.R
import com.team2.handiwork.databinding.FragmentRegistrationWorkerProfileBinding
import com.team2.handiwork.firebase.Firestore
import com.team2.handiwork.singleton.UserData
import com.team2.handiwork.viewModel.FragmentRegistrationWorkerProfileViewModel

class AgentUpdateSubscriptionLocationFragment : Fragment() {
    private lateinit var binding: FragmentRegistrationWorkerProfileBinding
    private lateinit var vm: FragmentRegistrationWorkerProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegistrationWorkerProfileBinding.inflate(inflater, container, false)
        vm = FragmentRegistrationWorkerProfileViewModel()
        binding.vm = vm
        binding.lifecycleOwner = this

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

        binding.workerPreferredMissionDistanceSpinner.onItemSelectedListener =
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
                    vm.workerPreferredMissionDistance.value = mapDistance(selectedView.text.toString())
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
        if (vm.deviceLocation.value == null) {
            Toast.makeText(requireContext(), "Your hasn't set your location!", Toast.LENGTH_SHORT)
            return@OnClickListener
        }
        if (vm.workerPreferredMissionDistance.value == null) {
            Toast.makeText(requireContext(), "Your hasn't set your distance!", Toast.LENGTH_SHORT)
            return@OnClickListener
        }

        UserData.currentUserData.locationLat = vm.deviceLocation.value!!.latitude
        UserData.currentUserData.locationLng = vm.deviceLocation.value!!.longitude
        UserData.currentUserData.distance = vm.workerPreferredMissionDistance.value!!

        // save to DB
        Firestore()
            .updateUser(UserData.currentUserData)
            .subscribe {
                if (it) {
                    navigateToAgentProfileFragment()
                }
            }
    }

    @SuppressLint("CheckResult")
    private val skipBtnOnClickListener = View.OnClickListener {
        // save to DB
        Firestore()
            .updateUser(UserData.currentUserData)
            .subscribe {
                if (it) {
                    navigateToAgentProfileFragment()
                }
            }
    }

    private fun navigateToAgentProfileFragment() {
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}