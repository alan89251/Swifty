package com.team2.handiwork.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.team2.handiwork.R
import com.team2.handiwork.databinding.FragmentHomeBinding
import com.team2.handiwork.utilities.Utility


class HomeFragment : Fragment() {

    lateinit var binding : FragmentHomeBinding
    var isEmployer = true;
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater,container,false)


        return binding.root
    }





}