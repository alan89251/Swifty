package com.team2.handiwork.fragments

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.team2.handiwork.AppConst
import com.team2.handiwork.R
import com.team2.handiwork.adapter.CreateMissionServiceTypeRecyclerViewAdapter
import com.team2.handiwork.adapter.HomeMissionRecyclerViewAdapter
import com.team2.handiwork.databinding.FragmentHomeBinding
import com.team2.handiwork.models.Mission
import com.team2.handiwork.models.ServiceType
import com.team2.handiwork.models.SubServiceType
import com.team2.handiwork.utilities.Utility
import com.team2.handiwork.viewModel.ActivityHomeViewModel
import com.team2.handiwork.viewModel.FragmentHomeViewModel


class HomeFragment : Fragment(), AdapterView.OnItemSelectedListener {

    lateinit var binding: FragmentHomeBinding
    lateinit var viewModel: FragmentHomeViewModel
    private val homeActivityVm: ActivityHomeViewModel by activityViewModels()
    private lateinit var homeMissionAdapter: HomeMissionRecyclerViewAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        viewModel = FragmentHomeViewModel()
        binding.progressBar.visibility = View.VISIBLE
        val pref = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val currentTheme = pref.getInt(AppConst.CURRENT_THEME, 0)
        viewModel.observeMissionList(homeActivityVm)
        homeActivityVm.currentUser.observe(viewLifecycleOwner) { user ->
            switchViewButtonContext(user.balance)
        }
        homeMissionAdapter = HomeMissionRecyclerViewAdapter(changeDrawableColor, onMissionClick)

        // setup UI according to theme
        val actionBar = (activity as AppCompatActivity).supportActionBar
        if (currentTheme == 1) {
            actionBar?.title = "Swifty Employer Portal"
            binding.addMissionButton.visibility = View.VISIBLE
            initSpinner(resources.getStringArray(R.array.employer_mission_filter))
        } else {
            actionBar?.title = "Swifty Agent Portal"
            binding.addMissionButton.visibility = View.GONE
            initSpinner(resources.getStringArray(R.array.agent_mission_history_filter))
        }

        homeActivityVm.missions.observe(viewLifecycleOwner) { missions ->
            missions?.let {
                if (missions.isEmpty()) {
                    displayNoMissionUI()
                } else {
                    displayHasMissionUI()
                }
            }
        }

        viewModel.filteredMissions.observe(viewLifecycleOwner) { missions ->
            missions?.let {
                homeMissionAdapter.setList(missions)
            }
        }

        binding.addMissionButton.setOnClickListener {
            if (viewModel.checkEnoughBalance()) {
                navigateToSelectCategoryScreen()
            } else {
                navigateToFailScreen()
            }
        }

        binding.viewWalletBtn.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToWalletBalanceFragment())
        }

        return binding.root
    }


    private fun displayHasMissionUI() {
        binding.progressBar.visibility = View.GONE
        binding.noMissionLayoutGroup.visibility = View.GONE
        binding.hasMissionLayoutGroup.visibility = View.VISIBLE
        initHasMissionRecyclerView()
    }

    private fun displayNoMissionUI() {
        binding.progressBar.visibility = View.GONE
        binding.hasMissionLayoutGroup.visibility = View.GONE
        binding.noMissionLayoutGroup.visibility = View.VISIBLE
        initNoMissionRecyclerView()
    }

    private fun initNoMissionRecyclerView() {
        binding.homeMissionCategoryRecyclerView.layoutManager =
            GridLayoutManager(context, viewModel.serviceTypeListColumnNum)
        val adapter = CreateMissionServiceTypeRecyclerViewAdapter(getServiceTypes())
        binding.homeMissionCategoryRecyclerView.adapter = adapter
        adapter.selectServiceType.subscribe {
            viewModel.mission.serviceType = it.name

            if (viewModel.checkEnoughBalance()) {
                val action =
                    HomeFragmentDirections.actionHomeFragmentToCreateMissionSelectSubServiceTypeFragment(viewModel.mission)
                findNavController().navigate(action)
            } else {
                navigateToFailScreen()
            }
        }
    }

    private fun initSpinner(entries: Array<String>) {
        val adapter = ArrayAdapter(requireContext(), R.layout.mission_filter_spinner_item, entries)
        adapter.setDropDownViewResource(R.layout.mission_filter_spinner_dropdown_item)
        binding.missionFilterSpinner.adapter = adapter
        binding.missionFilterSpinner.onItemSelectedListener = this
    }

    private fun initHasMissionRecyclerView() {
        binding.homeMissionRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.homeMissionRecyclerView.adapter = homeMissionAdapter
    }

    private fun getServiceTypes(): ArrayList<ServiceType> {
        return viewModel.serviceTypes.map {
            val serviceType = ServiceType()
            serviceType.name = it
            serviceType.subServiceTypeList = resources.getStringArray(
                viewModel.getSubServiceTypesResId(it)
            ).map {
                val subServiceType = SubServiceType()
                subServiceType.name = it
                subServiceType
            }.toList() as ArrayList<SubServiceType>
            serviceType
        }.toList() as ArrayList<ServiceType>
    }

    private fun navigateToFailScreen() {
        findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToFailCreateMissionFragment())
    }

    private fun navigateToSelectCategoryScreen() {
        findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToCreateMissionSelectCategoryFragment())
    }

    private fun switchViewButtonContext(balance: Int) {
        if (balance <= 0) {
            binding.viewWalletBtn.setBackgroundColor(resources.getColor(R.color.very_dark_blue_100))
            binding.viewWalletBtn.setTextColor(resources.getColor(R.color.white_100))
            binding.viewWalletBtn.text = "Top up"
        }
        binding.userCredit.text = "${balance} credits"
    }

    private val changeDrawableColor: (textView: TextView, mission: Mission) -> Unit = { textView, mission ->
        val backgroundDrawable = GradientDrawable()
        backgroundDrawable.shape = GradientDrawable.RECTANGLE
        val cornerRadius = 20.0f
        backgroundDrawable.cornerRadius = cornerRadius
        backgroundDrawable.setColor(
            ContextCompat.getColor(
                requireContext(),
                viewModel.convertStatusColor(mission.status)
            )
        )
        textView.background = backgroundDrawable
    }

    private val onMissionClick: (mission: Mission) -> Unit = {
        Toast.makeText(requireContext(), it.employer, Toast.LENGTH_SHORT).show()
        val action =
            HomeFragmentDirections
                .actionHomeFragmentToEmployerMissionDetailsFragment(
                    it
                )
        findNavController().navigate(action)
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        viewModel.updateFilter(parent!!.getItemAtPosition(position) as String)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }
}