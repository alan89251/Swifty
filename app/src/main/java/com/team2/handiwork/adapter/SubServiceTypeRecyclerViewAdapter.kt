package com.team2.handiwork.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.team2.handiwork.R
import com.team2.handiwork.databinding.RecycleViewSubServiceTypeItemBinding
import com.team2.handiwork.models.ServiceType

class SubServiceTypeRecyclerViewAdapter(var list: List<ServiceType>) :
    RecyclerView.Adapter<SubServiceTypeRecyclerViewAdapter.ViewHolder>() {

    class ViewHolder(itemBinding: RecycleViewSubServiceTypeItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        val binding: RecycleViewSubServiceTypeItemBinding = itemBinding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RecycleViewSubServiceTypeItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.recycle_view_sub_service_type_item,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.binding.serviceType = item
        holder.binding.rvSubServiceType.layoutManager =
            LinearLayoutManager(holder.binding.root.context)
        holder.binding.rvSubServiceType.adapter =
            SubServiceTypeOptionRecyclerViewAdapter(item.subServiceType)
//        if (item.visibility == 0) {
//            item.visibility = 1
//        } else {
//            item.visibility = 0
//        }
    }

    override fun getItemCount(): Int = list.size
}