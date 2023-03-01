package com.team2.handiwork.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.team2.handiwork.R
import com.team2.handiwork.databinding.RecycleViewMissionPhotoItemBinding

class MissionPhotosRecyclerViewAdapter(
    var list: List<Uri>,
    private val onRemoveMissionPhoto: (Int) -> Unit // arg: position
):
    RecyclerView.Adapter<MissionPhotosRecyclerViewAdapter.ViewHolder>() {
    class ViewHolder(itemBinding: RecycleViewMissionPhotoItemBinding):
        RecyclerView.ViewHolder(itemBinding.root) {
            val binding: RecycleViewMissionPhotoItemBinding = itemBinding
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RecycleViewMissionPhotoItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.recycle_view_mission_photo_item,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.binding.missionPhoto.setImageURI(item)
        holder.binding.btnRemoveMissionPhoto.setOnClickListener {
            onRemoveMissionPhoto(position)
        }
    }

    override fun getItemCount(): Int = list.size
}