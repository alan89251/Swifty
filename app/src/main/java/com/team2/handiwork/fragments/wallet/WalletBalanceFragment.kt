package com.team2.handiwork.fragments.wallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.team2.handiwork.AppConst
import com.team2.handiwork.R
import com.team2.handiwork.adapter.TransactionRecyclerViewAdapter
import com.team2.handiwork.databinding.FragmentWalletBalanceBinding
import com.team2.handiwork.singleton.UserData
import com.team2.handiwork.utilities.Ext.Companion.disposedBy
import com.team2.handiwork.viewModel.wallet.FragmentWalletBalanceViewModel

class WalletBalanceFragment : BaseWalletFragment() {
    //    private var selectedCredit = 50
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
        val bundle: Bundle = Bundle()

        binding.layoutBalance.user = UserData.currentUserData

        vm.getUserTransaction(email!!).subscribe {
            context?.let { ctx ->
                val adapter = TransactionRecyclerViewAdapter(ctx, it)
                binding.rvTransaction.layoutManager = LinearLayoutManager(ctx)
                binding.rvTransaction.adapter = adapter
            }
        }.disposedBy(disposeBag)

        binding.layoutBalance.ibtnNote.setOnClickListener {
            this.alertDialog()
        }


        binding.btn50Credit.setOnClickListener {
            bundle.putInt("selectedCredit", 50)
            findNavController().navigate(
                R.id.action_walletBalanceFragment_to_walletTopUpFragment,
                bundle
            )
        }
        binding.btn100Credit.setOnClickListener {
            bundle.putInt("selectedCredit", 100)
            findNavController().navigate(
                R.id.action_walletBalanceFragment_to_walletTopUpFragment,
                bundle
            )
        }
        binding.btn500Credit.setOnClickListener {
            bundle.putInt("selectedCredit", 500)
            findNavController().navigate(
                R.id.action_walletBalanceFragment_to_walletTopUpFragment,
                bundle
            )
        }

        return binding.root
    }
}