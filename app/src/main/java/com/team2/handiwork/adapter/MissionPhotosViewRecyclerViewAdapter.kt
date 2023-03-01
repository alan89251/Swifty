package com.team2.handiwork.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.team2.handiwork.R
import com.team2.handiwork.databinding.RecycleViewMissionPhotoViewItemBinding

class MissionPhotosViewRecyclerViewAdapter(var list: List<Uri>):
    RecyclerView.Adapter<MissionPhotosViewRecyclerViewAdapter.ViewHolder>() {
    class ViewHolder(itemBinding: RecycleViewMissionPhotoViewItemBinding):
        RecyclerView.ViewHolder(itemBinding.root) {
            val binding: RecycleViewMissionPhotoViewItemBinding = itemBinding
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RecycleViewMissionPhotoViewItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.recycle_view_mission_photo_view_item,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.binding.missionPhoto.setImageURI(item)
    }

    override fun getItemCount(): Int = list.size
}