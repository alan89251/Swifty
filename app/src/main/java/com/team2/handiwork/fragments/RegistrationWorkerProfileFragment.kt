package com.team2.handiwork.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.team2.handiwork.utilities.GetDeviceLocationLogic
import com.team2.handiwork.R
import com.team2.handiwork.databinding.FragmentRegistrationWorkerProfileBinding
import com.team2.handiwork.viewModel.FragmentRegistrationWorkerProfileViewModel

class RegistrationWorkerProfileFragment : Fragment() {
    private lateinit var binding: FragmentRegistrationWorkerProfileBinding
    private lateinit var vm: FragmentRegistrationWorkerProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegistrationWorkerProfileBinding.inflate(inflater, container, false)
        vm = FragmentRegistrationWorkerProfileViewModel()
        binding.vm = vm
        binding.lifecycleOwner = this

        // configure UIs
        configStepper()

        vm.deviceLocation.observe(requireActivity(), ::onReceiveDeviceLocation)
        vm.workerLocationMap.observe(requireActivity(), ::requireDeviceLocation)
        vm.workerPreferredMissionDistance.observe(requireActivity(), ::updateMapContent)

        binding.workerPreferredMissionDistanceSpinner.onItemSelectedListener = workerPreferredMissionDistanceSpinnerListener

        // require location permission if not grant
        if (!checkForLocationPermission()) {
            requireLocationPermission()
            return binding.root
        }

        loadWorkerLocationMap()

        return binding.root
    }

    private val workerPreferredMissionDistanceSpinnerListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
            val selectedView = p1 as TextView
            vm.workerPreferredMissionDistance.value = mapDistance(selectedView.text.toString())
        }

        override fun onNothingSelected(p0: AdapterView<*>?) {
            return
        }

    }

    // map distance string to distance
    private fun mapDistance(distanceStr: String): Double {
        return distanceStr.removeSuffix("km").toDouble()
    }

    // get google map scale of the distance
    private fun getMapScaleByDistance(distance: Double): Float {
        return when (distance) {
            5.0 -> 12f
            10.0 -> 11f
            else -> 10f
        }
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
        requestPermissions(arrayOf(
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
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    loadWorkerLocationMap()
                }
                else {
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
        val workerLocationMapFragment: SupportMapFragment = getChildFragmentManager().findFragmentById(
            R.id.worker_location_map
        ) as SupportMapFragment
        workerLocationMapFragment.getMapAsync(OnMapReadyCallback {
            vm.workerLocationMap.value = it
        })
    }

    private fun requireDeviceLocation(workerLocationMap: GoogleMap) {
        GetDeviceLocationLogic(
            requireActivity().
                getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager,
            requireContext()
        )
            .requestLocation {
                vm.deviceLocation.value = it
            }
    }

    private fun onReceiveDeviceLocation(location: Location) {
        configMapContentByDeviceLocation(location)
    }

    private fun configMapContentByDeviceLocation(location: Location) {
        val deviceLatLng = LatLng(location.latitude, location.longitude)
        if (vm.workerPreferredMissionDistance.value == null) {
            return
        }
        updateMapContent(vm.workerPreferredMissionDistance.value!!)
    }

    private fun updateLocationIndicator(selectedDistance: Double) {
        if (vm.workerLocationMap.value == null) {
            return
        }
        if (vm.deviceLocation.value == null) {
            return
        }
        vm.locationIndicator.value?.remove()
        val imageDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.location_indicator)
        vm.locationIndicator.value = vm.workerLocationMap.value!!.addGroundOverlay(
            GroundOverlayOptions()
                .image(imageDescriptor)
                .position(
                    LatLng(vm.deviceLocation.value!!.latitude, vm.deviceLocation.value!!.longitude),
                    selectedDistance.toFloat() * 200F,
                    selectedDistance.toFloat() * 200F
                )
        )
    }

    private fun updateMapContent(selectedDistance: Double) {
        updateMapZoomingScale(getMapScaleByDistance(selectedDistance))
        updateCircleOfUserPreferredDistance(selectedDistance)
        updateLocationIndicator(selectedDistance)
    }

    private fun updateMapZoomingScale(scale: Float) {
        if (vm.workerLocationMap.value == null) {
            return
        }
        if (vm.deviceLocation.value == null) {
            return
        }
        vm.workerLocationMap.value!!.moveCamera(CameraUpdateFactory.newLatLngZoom(
            LatLng(vm.deviceLocation.value!!.latitude, vm.deviceLocation.value!!.longitude),
            scale))
    }

    private fun updateCircleOfUserPreferredDistance(selectedDistance: Double) {
        if (vm.workerLocationMap.value == null) {
            return
        }
        if (vm.deviceLocation.value == null) {
            return
        }
        // clear previous circle
        vm.workerPreferredMissionCircle.value?.remove()
        // add new circle
        val deviceLatLng = LatLng(vm.deviceLocation.value!!.latitude, vm.deviceLocation.value!!.longitude)
        vm.workerPreferredMissionCircle.value = vm.workerLocationMap.value!!.addCircle(
            CircleOptions()
                .center(deviceLatLng)
                .radius(selectedDistance * 1000.0) // change km to meter
                .fillColor(Color.parseColor("#80E5B769"))
                .strokeColor(Color.parseColor("#80E5B769"))
        )
    }

    private fun configStepper() {
        val drawable: Drawable =
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_baseline_check_24,
                null
            )!!
        drawable.setTint(ContextCompat.getColor(requireContext(), R.color.white))
        binding.registrationStepper.ivStep1.background.setTint(
            ContextCompat.getColor(
                requireContext(),
                R.color.checked_color
            )
        )
        binding.registrationStepper.ivStep1.setImageDrawable(drawable)
        binding.registrationStepper.ivStep2.setImageResource(R.drawable.stepper__active_2)
        binding.registrationStepper.ivStep3.setImageResource(R.drawable.stepper__next_2)
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}