package com.team2.handiwork.fragments.registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.team2.handiwork.R
import com.team2.handiwork.activity.UserProfileActivity
import com.team2.handiwork.adapter.ServiceTypeRecyclerViewAdapter
import com.team2.handiwork.databinding.FragmentRegistrationChooseServiceTypeBinding
import com.team2.handiwork.base.fragment.BaseFragmentActivity
import com.team2.handiwork.models.ServiceType
import com.team2.handiwork.models.SubServiceType

class RegistrationChooseServiceTypeFragment : BaseFragmentActivity() {

    private var columnCount = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val binding = FragmentRegistrationChooseServiceTypeBinding.inflate(
            inflater, container, false
        )
        val fragmentActivity = requireActivity() as UserProfileActivity
        val vm = fragmentActivity.vm
        binding.vm = fragmentActivity.vm

        resources
            .getStringArray(R.array.service_type_list)
            .forEach {
                val serviceType = ServiceType()
                serviceType.name = it
                serviceType.subServiceTypeList = ArrayList<SubServiceType>(
                    resources
                        .getStringArray(vm.getSubServiceTypesResId(it))
                        .map { subServiceTypeName ->
                            val subServiceType = SubServiceType()
                            subServiceType.name = subServiceTypeName
                            subServiceType
                        }
                )
                vm.serviceTypeMap[serviceType.name] = serviceType
            }


        val view = binding.root
        fragmentActivity.binding.vm!!.currentStep.value = 2

        binding.lifecycleOwner = this
        binding.rvGrid.layoutManager = GridLayoutManager(context, columnCount)
        val adapter = ServiceTypeRecyclerViewAdapter(vm.serviceTypeMap.values.toList())

        fragmentActivity.setActionBarTitle("My skills are...")
        binding.rvGrid.adapter = adapter

        adapter.selectServiceType.subscribe {
            vm.serviceTypeMap[it.name]!!.selected = it.selected
        }

        binding.btnNext.setOnClickListener {
            val selectedList = vm.serviceTypeMap.values.toList().filter { it.selected }
            if (selectedList.isEmpty()) {
                return@setOnClickListener
            }

            this.navigate(
                R.id.fm_registration,
                RegistrationChooseSubServiceTypeFragment(selectedList),
                "RegistrationChooseSubServiceTypeFragment"
            )
        }

        binding.btnSkip.setOnClickListener {
            this.navigate(
                R.id.fm_registration,
                RegistrationWorkerProfileFragment(),
                "RegistrationWorkerProfileFragment"
            )
        }
        return view
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) = RegistrationChooseServiceTypeFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_COLUMN_COUNT, columnCount)
            }
        }
    }
}