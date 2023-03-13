package com.team2.handiwork.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.team2.handiwork.R
import com.team2.handiwork.databinding.RecyclerViewMissionItemBinding
import com.team2.handiwork.enums.MissionStatusEnum
import com.team2.handiwork.models.Mission
import com.team2.handiwork.utilities.Utility


class MyMissionsRecyclerViewAdapter(
    private val dynamicBackground: (TextView, Mission) -> Unit,
    private val onItemClicked: (Mission) -> Unit
) :
    RecyclerView.Adapter<MyMissionsRecyclerViewAdapter.ViewHolder>() {

    private val list = ArrayList<Mission>()

    inner class ViewHolder(itemBinding: RecyclerViewMissionItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        val binding: RecyclerViewMissionItemBinding = itemBinding
        fun bind(
            mission: Mission,
            changeBackground: (TextView, Mission) -> Unit,
            clickListener: (Mission) -> Unit
        ) {
            // Todo image loading
            binding.backgroundImage.setImageResource(Utility.getDefaultMissionPhoto(mission.subServiceType))
            binding.missionTitleBottomLeft.text = mission.subServiceType
            binding.statusTopRight.text = MissionStatusEnum.values()[mission.status.value].toString()
            binding.missionTimeDate.text = Utility.convertLongToDate(mission.endTime)
            binding.missionTimeHour.text = Utility.convertLongToHour(mission.endTime)
            binding.missionAddress.text = mission.location
            binding.missionCredit.text = mission.price.toString()
            changeBackground(binding.statusTopRight, mission)
            binding.listItemLayout.setOnClickListener {
                clickListener(mission)
            }
            // Todo need a confirmed user
            binding.confirmedUserRow.visibility = View.GONE
        }
    }

    fun setList(missions: List<Mission>) {
        list.clear()
        list.addAll(missions)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RecyclerViewMissionItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.recycler_view_mission_item,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item, dynamicBackground, onItemClicked)
    }

    override fun getItemCount(): Int {
        return list.size
    }

}