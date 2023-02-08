package com.team2.handiwork.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.team2.handiwork.R
import com.team2.handiwork.activity.UserProfileActivity
import com.team2.handiwork.adapter.ServiceTypeRecyclerViewAdapter
import com.team2.handiwork.databinding.FragmentRegistrationChooseServiceTypeBinding
import com.team2.handiwork.viewModel.FragmentRegistrationChooseServiceTypeViewModel

class RegistrationChooseServiceTypeFragment : Fragment() {

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

        val vm = FragmentRegistrationChooseServiceTypeViewModel()
        binding.vm = vm
        val view = binding.root
        val activity = requireActivity() as UserProfileActivity
        activity.setCurrentStep(activity.binding.stepper,2)

        binding.lifecycleOwner = this
        binding.rvGrid.layoutManager = GridLayoutManager(context, columnCount)
        val adapter = ServiceTypeRecyclerViewAdapter(vm.serviceTypeList)

        activity.setActionBarTitle("My skills are...")
        binding.rvGrid.adapter = adapter

        adapter.selectServiceType.subscribe {
            if (it.selected) {
                vm.selectedServiceTypeList.add(it)
            } else {
                vm.selectedServiceTypeList.remove(it)
            }
        }

        binding.btnNext.setOnClickListener {
            if (vm.selectedServiceTypeList.size == 0) {
                return@setOnClickListener
            }

            val form = activity.getUserRegistrationForm()
            form.serviceTypeList = vm.selectedServiceTypeList
            activity.updateUserRegistrationForm(form)


            // todo pass arg or not

            val trans = activity
                .supportFragmentManager
                .beginTransaction()

            trans.replace(
                R.id.fm_registration,
                RegistrationChooseSubServiceTypeFragment(vm.selectedServiceTypeList)
            )
            trans.addToBackStack("RegistrationChooseSubServiceTypeFragment")
            trans.commit()

        }

        binding.btnSkip.setOnClickListener {
            requireActivity()
            val trans = activity
                .supportFragmentManager
                .beginTransaction()

            trans.replace(
                R.id.fm_registration,
                RegistrationWorkerProfileFragment()
            )
            trans.addToBackStack("RegistrationWorkerProfileFragment")
            trans.commit()
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