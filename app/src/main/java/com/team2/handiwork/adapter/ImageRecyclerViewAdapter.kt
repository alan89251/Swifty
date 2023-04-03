package com.team2.handiwork.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.team2.handiwork.R
import com.team2.handiwork.databinding.RecycleViewMissionPhotoViewItemBinding
import com.team2.handiwork.firebase.Storage

class ImageRecyclerViewAdapter(var list: List<String>)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    class ImageViewHolder(
        itemBinding: RecycleViewMissionPhotoViewItemBinding,
        private val context: Context
    ): RecyclerView.ViewHolder(itemBinding.root) {
        val binding: RecycleViewMissionPhotoViewItemBinding = itemBinding

        fun loadImage(path: String) {
            Storage().getImgUrl(path, ::onImgUrlLoaded, ::onImgUrlLoadedFail)
        }

        private fun onImgUrlLoaded(imgUrl: String) {
            Glide.with(context)
                .load(imgUrl)
                .into(binding.missionPhoto)
        }

        private fun onImgUrlLoadedFail() {}
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: RecycleViewMissionPhotoViewItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.recycle_view_mission_photo_view_item,
            parent,
            false
        )
        return ImageViewHolder(binding, parent.context)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]
        (holder as ImageViewHolder).loadImage(item)
    }

    override fun getItemCount(): Int = list.size
}