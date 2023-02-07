package com.team2.handiwork.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
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
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.team2.handiwork.R
import com.team2.handiwork.UserProfileActivity
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
        binding.nextBtn.setOnClickListener(nextBtnOnClickListener)
        binding.skipBtn.setOnClickListener(skipBtnOnClickListener)
        configStepper()

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

    private val nextBtnOnClickListener = View.OnClickListener {
        // update UserRegistrationForm
        val activity = requireActivity() as UserProfileActivity
        val form = activity.getUserRegistrationForm()
        form.locationLat = vm.deviceLocation.value!!.latitude
        form.locationLng = vm.deviceLocation.value!!.longitude
        form.distance = vm.workerPreferredMissionDistance.value!!
        activity.updateUserRegistrationForm(form)

        // navigate to RegistrationWorkerTNCFragment
        requireActivity()
            .supportFragmentManager
            .beginTransaction()
            .replace(R.id.user_profile_fragment, RegistrationWorkerTNCFragment())
            .commit()
    }

    private val skipBtnOnClickListener = View.OnClickListener {
        requireActivity()
            .supportFragmentManager
            .beginTransaction()
            .replace(R.id.user_profile_fragment, RegistrationWorkerTNCFragment())
            .commit()
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}