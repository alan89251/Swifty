package com.team2.handiwork.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.team2.handiwork.R
import com.team2.handiwork.databinding.FragmentFailCreateMissionBinding
import com.team2.handiwork.singleton.UserData


class FailCreateMissionFragment : Fragment() {

    lateinit var binding: FragmentFailCreateMissionBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentFailCreateMissionBinding.inflate(inflater, container, false)

        binding.userCredit.text = UserData.currentUserData.balance.toString()
        // Inflate the layout for this fragment
        return binding.root

//        binding.viewWalletBtn.setOnClickListener {
//
//        }
    }
}