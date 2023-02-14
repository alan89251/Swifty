package com.team2.handiwork.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.team2.handiwork.R
import com.team2.handiwork.databinding.FragmentWalletTopSuccessBinding

class WalletTopUpSuccessFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentWalletTopSuccessBinding.inflate(
            inflater, container, false
        )
        binding.lifecycleOwner = this

        binding.btnViewWallet.setOnClickListener {
            findNavController().navigate(
                R.id.action_walletTopUpSuccessFragment_to_walletBalanceFragment,
            )
        }

        binding.btnNavToHome.setOnClickListener {
            findNavController().navigate(
                R.id.action_walletTopUpSuccessFragment_to_homeFragment,
            )
        }
        return binding.root
    }
}