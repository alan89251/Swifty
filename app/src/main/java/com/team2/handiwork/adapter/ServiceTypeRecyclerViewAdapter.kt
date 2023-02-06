package com.team2.handiwork.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.team2.handiwork.R
import com.team2.handiwork.databinding.RecycleViewServiceTypeItemBinding
import com.team2.handiwork.models.ServiceType

class ServiceTypeRecyclerViewAdapter(var list: List<ServiceType>) :
    RecyclerView.Adapter<ServiceTypeRecyclerViewAdapter.ViewHolder>() {

    class ViewHolder(itemBinding: RecycleViewServiceTypeItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        val binding: RecycleViewServiceTypeItemBinding = itemBinding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RecycleViewServiceTypeItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.recycle_view_service_type_item,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.binding.serviceType = item
        if (item.visibility == 0) {
            item.visibility = 1
        } else {
            item.visibility = 0
        }
    }

    override fun getItemCount(): Int = list.size
}