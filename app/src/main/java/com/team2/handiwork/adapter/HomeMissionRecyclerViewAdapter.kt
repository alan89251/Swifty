package com.team2.handiwork.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.team2.handiwork.R
import com.team2.handiwork.databinding.MissionRecyclerViewItemBinding
import com.team2.handiwork.databinding.RecycleViewCreateMissionSubServiceTypeItemBinding
import com.team2.handiwork.enum.MissionStatusEnum
import com.team2.handiwork.models.Mission
import com.team2.handiwork.utilities.Utility

class HomeMissionRecyclerViewAdapter() :
    RecyclerView.Adapter<HomeMissionRecyclerViewAdapter.ViewHolder>() {

    private val list = ArrayList<Mission>()

    inner class ViewHolder(itemBinding: MissionRecyclerViewItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(mission: Mission) {
            // Todo mission thumbnail
            binding.missionName.text = mission.subServiceType
            binding.missionTimeDate.text = Utility.convertLongToDate(mission.endTime)
            binding.missionTimeHour.text = Utility.convertLongToHour(mission.endTime)
            binding.missionAddress.text = mission.location
            binding.missionPrice.text = mission.price.toString()
            binding.missionStatus.text = MissionStatusEnum.values()[mission.status].toString()
            // Todo need a confirmed user
            binding.confirmedUserRow.visibility = View.GONE
        }

        val binding: MissionRecyclerViewItemBinding = itemBinding
    }

    fun setList(missions: List<Mission>) {
        list.clear()
        list.addAll(missions)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: MissionRecyclerViewItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.mission_recycler_view_item,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = list.size

}