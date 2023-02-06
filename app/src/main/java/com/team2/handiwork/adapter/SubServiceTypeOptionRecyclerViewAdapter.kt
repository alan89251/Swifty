package com.team2.handiwork.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.team2.handiwork.R
import com.team2.handiwork.databinding.RecycleViewSubServiceTypeOptionItemBinding
import com.team2.handiwork.models.SubServiceType

class SubServiceTypeOptionRecyclerViewAdapter(var list: List<SubServiceType>) :
    RecyclerView.Adapter<SubServiceTypeOptionRecyclerViewAdapter.ViewHolder>() {

    inner class ViewHolder(itemBinding: RecycleViewSubServiceTypeOptionItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        val binding: RecycleViewSubServiceTypeOptionItemBinding = itemBinding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RecycleViewSubServiceTypeOptionItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.recycle_view_sub_service_type_option_item,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.binding.subServiceType = item
//        if (item.visibility == 0) {
//            item.visibility = 1
//        } else {
//            item.visibility = 0
//        }
    }

    override fun getItemCount(): Int = list.size
}