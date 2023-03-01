package com.team2.handiwork.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.team2.handiwork.R
import com.team2.handiwork.databinding.RecycleViewCreateMissionSubServiceTypeItemBinding
import com.team2.handiwork.models.SubServiceType
import com.team2.handiwork.utilities.Utility
import io.reactivex.rxjava3.subjects.PublishSubject

class CreateMissionSubServiceTypeRecyclerViewAdapter(var list: List<SubServiceType>):
    RecyclerView.Adapter<CreateMissionSubServiceTypeRecyclerViewAdapter.ViewHolder>() {
    var selectSubServiceType: PublishSubject<SubServiceType> = PublishSubject.create()

    class ViewHolder(itemBinding: RecycleViewCreateMissionSubServiceTypeItemBinding):
        RecyclerView.ViewHolder(itemBinding.root) {
            val binding: RecycleViewCreateMissionSubServiceTypeItemBinding = itemBinding
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RecycleViewCreateMissionSubServiceTypeItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.recycle_view_create_mission_sub_service_type_item,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.binding.subServiceType = item
        holder.binding.ibtnServiceType.setImageResource(
            Utility.getDefaultMissionPhoto(
                item.name
            )
        )
        holder.binding.ibtnServiceType.setOnClickListener {
            item.selected = !item.selected
            selectSubServiceType.onNext(item)
        }
    }

    override fun getItemCount(): Int = list.size
}