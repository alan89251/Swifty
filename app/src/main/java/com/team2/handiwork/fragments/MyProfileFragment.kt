package com.team2.handiwork.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.team2.handiwork.AppConst
import com.team2.handiwork.databinding.FragmentProfileAgentBinding
import com.team2.handiwork.databinding.FragmentProfileEmployerBinding
import com.team2.handiwork.viewModel.FragmentMyProfileViewModel

class MyProfileFragment : Fragment() {
    var vm = FragmentMyProfileViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val agentBinding = FragmentProfileAgentBinding.inflate(inflater, container, false)

        val employerBinding = FragmentProfileEmployerBinding.inflate(inflater, container, false)
        val pref = PreferenceManager.getDefaultSharedPreferences(requireContext())

        val currentTheme = pref.getInt(AppConst.CURRENT_THEME, 0)
        // currentTheme 1 = employer
        val isAgent = currentTheme != 1

        return if (isAgent) agentBinding.root else employerBinding.root
    }
}