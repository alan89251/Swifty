package com.team2.handiwork.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.team2.handiwork.R
import com.team2.handiwork.databinding.RecycleViewCreateMissionServiceTypeItemBinding
import com.team2.handiwork.models.ServiceType
import com.team2.handiwork.utilities.Utility
import io.reactivex.rxjava3.subjects.PublishSubject

class CreateMissionServiceTypeRecyclerViewAdapter(var list: List<ServiceType>):
    RecyclerView.Adapter<CreateMissionServiceTypeRecyclerViewAdapter.ViewHolder>() {
    var selectServiceType: PublishSubject<ServiceType> = PublishSubject.create()

    class ViewHolder(itemBinding: RecycleViewCreateMissionServiceTypeItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
            val binding: RecycleViewCreateMissionServiceTypeItemBinding = itemBinding
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RecycleViewCreateMissionServiceTypeItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.recycle_view_create_mission_service_type_item,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.binding.serviceType = item
        holder.binding.ibtnServiceType.setImageResource(
            Utility.getDefaultServiceTypePhoto(
                item.name
            )
        )
        holder.binding.ibtnServiceType.setOnClickListener {
            item.selected = !item.selected
            selectServiceType.onNext(item)
        }
    }

    override fun getItemCount(): Int = list.size
}