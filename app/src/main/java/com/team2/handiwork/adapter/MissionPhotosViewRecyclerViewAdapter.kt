package com.team2.handiwork.adapter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.team2.handiwork.R
import com.team2.handiwork.databinding.RecycleViewMissionPhotoViewItemBinding
import java.util.Base64

class MissionPhotosViewRecyclerViewAdapter(var list: List<String>):
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
        val byteArray = Base64.getDecoder().decode(item)
        val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        holder.binding.missionPhoto.setImageBitmap(bitmap)
    }

    override fun getItemCount(): Int = list.size
}