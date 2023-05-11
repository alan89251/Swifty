package com.team2.handiwork.fragments.wallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.team2.handiwork.R
import com.team2.handiwork.databinding.FragmentWalletTopUpBinding
import com.team2.handiwork.enums.TransactionEnum
import com.team2.handiwork.firebase.firestore.Firestore
import com.team2.handiwork.models.Transaction
import com.team2.handiwork.singleton.UserData
import com.team2.handiwork.viewModel.wallet.FragmentWalletTopUpViewModel

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

        val user = UserData.currentUserData
        binding.layoutBalance.user = user

        vm.topUpAmount.value = topUpAmount
        binding.vm = vm
        binding.lifecycleOwner = this

        val transaction = Transaction()
        transaction.amount = topUpAmount
        transaction.title = "Top Up"
        transaction.firstName = UserData.currentUserData.firstName
        transaction.lastName = UserData.currentUserData.lastName
        transaction.type = TransactionEnum.TOP_UP

        // todo if update not success
        binding.btnTopUp.setOnClickListener {
            // todo move to viewmodel
            UserData.currentUserData.balance += topUpAmount
            Firestore().userCollection.updateUserBalance(
                user.email,
                UserData.currentUserData.balance,
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