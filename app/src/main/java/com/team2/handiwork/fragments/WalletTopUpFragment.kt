package com.team2.handiwork.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.team2.handiwork.R
import com.team2.handiwork.databinding.FragmentWalletTopUpBinding
import com.team2.handiwork.models.User
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
        val user = requireArguments().getSerializable("user")

        binding.layoutBalance.user = user as User

        vm.topUpAmount.value = topUpAmount
        binding.vm = vm
        binding.lifecycleOwner = this

//        binding.layoutBalance.ivCashOut.visibility = View.GONE

        binding.btnTopUp.setOnClickListener {
            findNavController().navigate(
                R.id.action_walletTopUpFragment_to_walletTopUpSuccessFragment,
            )
        }


        binding.layoutBalance.ibtnNote.setOnClickListener {
            this.alertDialog()
        }

        return binding.root
    }
}