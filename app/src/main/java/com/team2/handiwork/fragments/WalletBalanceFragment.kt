package com.team2.handiwork.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.team2.handiwork.AppConst
import com.team2.handiwork.R
import com.team2.handiwork.adapter.TransactionRecyclerViewAdapter
import com.team2.handiwork.databinding.FragmentWalletBalanceBinding
import com.team2.handiwork.viewModel.FragmentWalletBalanceViewModel

class WalletBalanceFragment : BaseWalletFragment() {
    private var selectedCredit = 50
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentWalletBalanceBinding.inflate(
            inflater, container, false
        )
        val vm = FragmentWalletBalanceViewModel()
        binding.vm = vm
        binding.lifecycleOwner = this

        val sp = PreferenceManager.getDefaultSharedPreferences(this.requireContext())
        val email = sp.getString(AppConst.EMAIL, "")

        vm.getUser(email!!).subscribe {
            binding.layoutBalance.user = it
        }


        vm.getUserTransaction(email).subscribe {
            val adapter = TransactionRecyclerViewAdapter(requireContext(), it)
            binding.rvTransaction.layoutManager = LinearLayoutManager(this.requireContext())
            binding.rvTransaction.adapter = adapter
        }

        binding.layoutBalance.ibtnNote.setOnClickListener {
            this.alertDialog()
        }


        binding.btn50Credit.setOnClickListener {
            selectedCredit = 50
            it.setOnClickListener(navigationToTopUpOnClickListener)
        }
        binding.btn100Credit.setOnClickListener {
            selectedCredit = 100
            it.setOnClickListener(navigationToTopUpOnClickListener)
        }
        binding.btn500Credit.setOnClickListener {
            selectedCredit = 500
            it.setOnClickListener(navigationToTopUpOnClickListener)
        }

        return binding.root
    }


    private val navigationToTopUpOnClickListener = View.OnClickListener {
        val trans = requireActivity()
            .supportFragmentManager
            .beginTransaction()

        // todo replace with drawer activity
        trans.replace(R.id.fm_registration, WalletTopUpFragment(selectedCredit))
        trans.addToBackStack("WalletTopUpFragment")
        trans.commit()

    }
}