package com.team2.handiwork.fragments

import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.team2.handiwork.R

open class BaseWalletFragment : Fragment() {

    // todo it should implement on layout_wallet viewmodel
    fun alertDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        val view: View = layoutInflater.inflate(R.layout.dialog_warning, null)
        builder.setView(view)
        builder.setTitle(getString(R.string.credit_on_hold))
        builder.setMessage(getString(R.string.credit_on_hold_content))
        val backButton: Button = view.findViewById<Button>(R.id.btn_back)
        val dialog = builder.create()
        backButton.setOnClickListener {
            dialog.dismiss()
//            supportFragmentManager.popBackStack()
        }
        dialog.show()
    }
}