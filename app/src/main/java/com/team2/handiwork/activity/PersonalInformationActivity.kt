package com.team2.handiwork.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.team2.handiwork.R
import com.team2.handiwork.databinding.ActivityPersonalInformationBinding
import com.team2.handiwork.viewModel.ActivityPersonalInformationViewModel

class PersonalInformationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityPersonalInformationBinding>(
            this,
            R.layout.activity_personal_information
        )
        binding.vm = ActivityPersonalInformationViewModel()
    }
}