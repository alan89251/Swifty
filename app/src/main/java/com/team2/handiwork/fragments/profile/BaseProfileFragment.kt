package com.team2.handiwork.fragments.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.team2.handiwork.adapter.CommentRecyclerViewAdapter
import com.team2.handiwork.base.fragment.DisposeFragment
import com.team2.handiwork.utilities.Ext.Companion.disposedBy
import com.team2.handiwork.viewModel.profile.FragmentBaseProfileViewModel

open class BaseProfileFragment<VM : FragmentBaseProfileViewModel> : DisposeFragment() {
    open lateinit var vm: VM
    lateinit var email: String
    var commentAdapter = CommentRecyclerViewAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        vm.getComments(email).subscribe {
            vm.comments.value = it
            vm.rating.value = vm.calculateRating(it)
            vm.ratingNumber.value = it.count()
        }.disposedBy(disposeBag)

        vm.getUser(email).subscribe {
            vm.user.value = it
            vm.cancelRate.value = vm.calculateCancellationRate(it)

            val subServiceTypeList = it.serviceTypeList.flatMap { st ->
                st.subServiceTypeList
            }.map { sst ->
                sst.name
            }

            val serviceTypeList = it.serviceTypeList.map { st -> st.name }


            val list = subServiceTypeList.ifEmpty {
                serviceTypeList
            }

            vm.typeList.value = list

            vm.categories.value = list.joinToString(separator = "\n") { name ->
                name
            }
        }.disposedBy(disposeBag)

        vm.getCertifications(email)

        return super.onCreateView(inflater, container, savedInstanceState)
    }
}