package com.team2.handiwork.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.team2.handiwork.R
import com.team2.handiwork.databinding.RecycleViewServiceTypeItemBinding
import com.team2.handiwork.models.ServiceType
import com.team2.handiwork.utilities.Utility
import io.reactivex.rxjava3.subjects.PublishSubject

class ServiceTypeRecyclerViewAdapter(var list: List<ServiceType>) :
    RecyclerView.Adapter<ServiceTypeRecyclerViewAdapter.ViewHolder>() {
    var selectServiceType: PublishSubject<ServiceType> = PublishSubject.create()

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
        holder.binding.ibtnServiceType.setImageResource(
            getDefaultServiceTypePhoto(
                item.name
            )
        )
        if (item.selected) {
            holder.binding.ivSelected.visibility = View.VISIBLE
        } else {
            holder.binding.ivSelected.visibility = View.INVISIBLE
        }
        holder.binding.ibtnServiceType.setOnClickListener {
            item.selected = !item.selected
            if (item.selected) {
                holder.binding.ivSelected.visibility = View.VISIBLE
            } else {
                holder.binding.ivSelected.visibility = View.INVISIBLE
            }
            selectServiceType.onNext(item)
        }
    }

    override fun getItemCount(): Int = list.size


    private fun getDefaultServiceTypePhoto(serviceType: String): Int {
        return when (serviceType) {
            "Assembling" -> R.drawable.service_furniture_assembly
            "Cleaning" -> R.drawable.service_commercial_cleaning
            "Gardening" -> R.drawable.service_garden_ned_maintencance
            "Moving" -> R.drawable.service_residential_moving
            "Renovation" -> R.drawable.service_bathroom_renovation
            "Repair" -> R.drawable.service_repair_furniture
            "Delivering" -> R.drawable.service_package_delivery
            "Seasonal" -> R.drawable.service_food_delivery
            else -> R.drawable.item_bg
        }
    }

}