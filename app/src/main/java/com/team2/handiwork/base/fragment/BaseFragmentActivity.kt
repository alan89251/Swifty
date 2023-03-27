package com.team2.handiwork.base.fragment

import androidx.fragment.app.Fragment

abstract class BaseFragmentActivity : DisposeFragment() {

    fun navigate(fragmentId: Int, fragment: Fragment, addBackStackName: String) {
        val trans = requireActivity().supportFragmentManager.beginTransaction()
        trans.replace(fragmentId, fragment)
        if (addBackStackName.isNotEmpty()) {
            trans.addToBackStack(addBackStackName)
        }
        trans.commit()
    }
}