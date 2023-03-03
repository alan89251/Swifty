package com.team2.handiwork.base

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

abstract class BaseFragmentActivity<T : FragmentActivity> : Fragment() {
    var fragmentActivity = requireActivity() as T

    fun navigate(fragmentId: Int, fragment: Fragment, addBackStackName: String) {
        val trans = fragmentActivity.supportFragmentManager.beginTransaction()
        trans.replace(fragmentId, fragment)
        if (addBackStackName.isNotEmpty()) {
            trans.addToBackStack(addBackStackName)
        }
        trans.commit()
    }
}