package com.team2.handiwork.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.team2.handiwork.databinding.FragmentWalletTopUpBinding
import com.team2.handiwork.viewModel.FragmentWalletTopUpViewModel

class WalletTopUpFragment() : BaseWalletFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentWalletTopUpBinding.inflate(
            inflater, container, false
        )
        val vm = FragmentWalletTopUpViewModel()
        val topUpAmount = requireArguments().getInt("selectedCredit", 50)
        vm.topUpAmount.value = topUpAmount
        binding.vm = vm
        binding.lifecycleOwner = this

//        binding.layoutBalance.ivCashOut.visibility = View.GONE

        binding.btnTopUp.setOnClickListener {
            // todo navigation to finished
        }


        binding.layoutBalance.ibtnNote.setOnClickListener {
            this.alertDialog()
        }

        return binding.root
    }
}