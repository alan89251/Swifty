package com.team2.handiwork.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import com.team2.handiwork.R
import com.team2.handiwork.activity.RegistrationPersonalInformationActivity
import com.team2.handiwork.adapter.ServiceTypeRecyclerViewAdapter
import com.team2.handiwork.databinding.FragmentRegistrationChooseServiceTypeBinding
import com.team2.handiwork.enum.EditorKey
import com.team2.handiwork.enum.SharePreferenceKey
import com.team2.handiwork.models.UserRegistrationForm
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
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentRegistrationChooseServiceTypeBinding.inflate(
            inflater,
            container,
            false
        )

        val vm = FragmentRegistrationChooseServiceTypeViewModel()
        binding.vm = vm
        val view = binding.root
        val activity = requireActivity() as RegistrationPersonalInformationActivity

        binding.lifecycleOwner = this
        binding.rvGrid.layoutManager = GridLayoutManager(context, columnCount)
        val adapter = ServiceTypeRecyclerViewAdapter(vm.serviceTypeList)

        binding.rvGrid.adapter = adapter
        val serviceTypeList = adapter.list.filter { it.selected }


        binding.btnNext.setOnClickListener {

            val form = activity.getUserRegistrationForm()
            form.serviceTypeList = serviceTypeList
            activity.updateUserRegistrationForm(form)

            requireActivity()
                .supportFragmentManager
                .beginTransaction()
                .replace(
                    R.id.fm_registration,
                    RegistrationChooseSubServiceTypeFragment(serviceTypeList),
                )
                .commit()
        }

        binding.btnSkip.setOnClickListener {
            requireActivity()
            // todo goto location page

//                .supportFragmentManager
//                .beginTransaction()
//                .replace(R.id.fm_registration, RegistrationChooseSubServiceTypeFragment())
//                .commit()
        }


        return view
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            RegistrationChooseServiceTypeFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}