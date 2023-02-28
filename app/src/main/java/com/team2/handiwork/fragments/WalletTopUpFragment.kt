package com.team2.handiwork.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.team2.handiwork.R
import com.team2.handiwork.databinding.FragmentWalletTopUpBinding
import com.team2.handiwork.enum.TransactionEnum
import com.team2.handiwork.firebase.Firestore
import com.team2.handiwork.models.Transaction
import com.team2.handiwork.models.User
import com.team2.handiwork.singleton.UserData
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

        val transaction = Transaction()
        transaction.amount = topUpAmount
        transaction.title = "Top Up"
        transaction.firstName = UserData.currentUserData.firstName
        transaction.lastName = UserData.currentUserData.lastName
        transaction.transType = TransactionEnum.TOP_UP

        binding.btnTopUp.setOnClickListener {
            Firestore().updateUserBalance(
                user.email,
                user.balance + topUpAmount,
                transaction,
            )
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